import {
    formatDate,
    showErrorAlert,
    showModal,
    showToast,
} from '/js/common/event.js';
import { ArticleApi } from '/js/api/articleApi.js';
import { PageRoutes, UriUtils } from '/js/common/uris.js';
import { DomElements } from '/js/common/domElements.js';
import { renderMarkdown } from '/js/common/markdownRenderer.js';

/**
 * Article Detail Handler
 * Handles article detail display, likes, and comments
 */

const createDetailHandlerModule = (() => {
    // Private state
    let currentArticle = null;
    let isLikeProcessing = false;
    let commentEditState = { mode: 'create', commentId: null };
    let detailArticleId = null;

    /* -------------------------------------------------------------------------- */
    /* Helpers                                                                    */
    /* -------------------------------------------------------------------------- */

    function parseNumber(value) {
        const num = Number(value);
        return Number.isNaN(num) ? 0 : num;
    }

    function escapeHtml(value) {
        if (value === undefined || value === null) return '';
        return String(value)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#39;');
    }

    function formatCompactNumber(value) {
        const num = parseNumber(value);
        if (num >= 100000) {
            return `${Math.floor(num / 1000)}k`;
        }
        if (num >= 10000) {
            return `${Math.floor(num / 1000)}k`;
        }
        if (num >= 1000) {
            return `${Math.floor(num / 1000)}k`;
        }
        return num.toString();
    }

    function resolveLikeState(article = {}) {
        const value = article.is_liked ?? article.isLiked ?? article.like_yn ?? article.likeYn ?? false;
        if (typeof value === 'string') {
            return value.toUpperCase() === 'Y';
        }
        return Boolean(value);
    }

    /* -------------------------------------------------------------------------- */
    /* Article detail                                                             */
    /* -------------------------------------------------------------------------- */

    async function loadArticleDetail(id) {
        if (!id) return;

        try {
            const articleResponse = await ArticleApi.getArticle(id);
            const article = articleResponse?.data || articleResponse;

            currentArticle = article;
            renderArticleDetail(article);

            const embeddedComments =
                (Array.isArray(article.comment) && article.comment) ||
                null;

            if (embeddedComments) {
                renderComments(embeddedComments);
            } else {
                try {
                    const commentsResponse = await ArticleApi.getComments(id);
                    const comments = commentsResponse?.data || commentsResponse;
                    renderComments(comments);
                } catch (error) {
                    console.error('Failed to load comments:', error);
                    renderComments([]);
                }
            }
        } catch (error) {
            console.error('Failed to load article:', error);
            showErrorAlert('게시글을 불러오는데 실패했습니다.');
        }
    }

    function generateTableOfContents() {
        const tocNav = DomElements.ArticleDetail.getTocNav();
        const tocContainer = DomElements.ArticleDetail.getToc();
        const contentContainer = DomElements.ArticleDetail.getContent();

        if (!tocNav || !tocContainer || !contentContainer) return;

        // Find all h1, h2, h3 tags in the already-rendered content
        const headings = contentContainer.querySelectorAll('h1, h2, h3');

        if (headings.length === 0) {
            tocContainer.style.display = 'none';
            return;
        }

        tocContainer.style.display = 'block';
        tocNav.innerHTML = '';

        headings.forEach((heading, index) => {
            const level = heading.tagName.toLowerCase();
            const id = heading.id || `heading-${index}`;
            const text = heading.textContent;

            // Ensure heading has an ID
            if (!heading.id) {
                heading.id = id;
            }

            // Create TOC link
            const link = document.createElement('a');
            link.href = `#${id}`;
            link.className = `toc-link toc-link-${level}`;
            link.textContent = text;
            link.addEventListener('click', (e) => {
                e.preventDefault();
                const target = document.getElementById(id);
                if (target) {
                    target.scrollIntoView({ behavior: 'smooth', block: 'start' });
                    // Update active state
                    document.querySelectorAll('.toc-link').forEach(l => l.classList.remove('active'));
                    link.classList.add('active');
                }
            });

            tocNav.appendChild(link);
        });

        // Scroll spy
        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    const id = entry.target.id;
                    document.querySelectorAll('.toc-link').forEach(link => {
                        link.classList.remove('active');
                        if (link.getAttribute('href') === `#${id}`) {
                            link.classList.add('active');
                        }
                    });
                }
            });
        }, {
            rootMargin: '-100px 0px -80% 0px'
        });

        headings.forEach(heading => {
            observer.observe(heading);
        });
    }

    function renderArticleDetail(article) {
        if (!article) return;

        const authorNickname = article.user_nickname || '익명';
        const isAuthor = article.can_modify === true || article.can_modify === 'true';

        const headerContainer = DomElements.ArticleDetail.getHeader();
        const createdAt = formatDate(article.last_modified_date);

        if (headerContainer) {
            headerContainer.innerHTML = `
                <h1 class="article-detail-title">${escapeHtml(article.title)}</h1>
                <div class="article-detail-meta">
                    <div class="article-detail-author">
                        <div class="article-detail-author-avatar" aria-hidden="true"></div>
                        <div class="article-detail-author-info">
                            <span class="article-detail-author-name">${escapeHtml(authorNickname)}</span>
                            <span class="article-detail-date">${createdAt}</span>
                        </div>
                    </div>
                    ${isAuthor ? `
                    <div class="article-detail-actions">
                        <button class="btn btn-secondary" data-action="edit" data-article-id="${article.id}">수정</button>
                        <button class="btn btn-danger" data-action="delete-article">삭제</button>
                    </div>
                    ` : ''}
                </div>
            `;

            if (isAuthor) {
                const editBtn = headerContainer.querySelector('[data-action="edit"]');
                if (editBtn) {
                    editBtn.addEventListener('click', () => {
                        window.location.href = PageRoutes.articleEdit(detailArticleId);
                    });
                }

                const deleteBtn = headerContainer.querySelector('[data-action="delete-article"]');
                if (deleteBtn) {
                    deleteBtn.addEventListener('click', showDeleteArticleModal);
                }
            }
        }

        const imageContainer = DomElements.ArticleDetail.getImageContainer();
        if (imageContainer) {
            const imageUrl = article.image_url || article.imageUrl;
            if (imageUrl) {
                imageContainer.innerHTML = `<img src="${imageUrl}" class="article-detail-image" alt="게시글 이미지">`;
            } else {
                imageContainer.innerHTML = '<div class="article-image-placeholder">등록된 이미지가 없습니다.</div>';
            }
        }

        const contentContainer = DomElements.ArticleDetail.getContent();
        const rawContent = article.content || '';
        if (contentContainer) {
            // Render markdown content
            contentContainer.innerHTML = renderMarkdown(rawContent);

            // Generate TOC after content is rendered
            generateTableOfContents();
        }

        const likeCount = parseNumber(article.like_cnt);
        const viewCount = parseNumber(article.view_cnt);

        const statsContainer = DomElements.ArticleDetail.getStats();
        if (statsContainer) {
            statsContainer.innerHTML = `
                <button
                    type="button"
                    class="article-stat article-stat--like"
                    id="articleLikeBtn"
                    data-liked="false"
                    aria-pressed="false"
                >
                    <span class="article-stat-number" id="articleLikeCount">${formatCompactNumber(likeCount)}</span>
                    <span class="article-stat-label">좋아요수</span>
                </button>
                <div class="article-stat">
                    <span class="article-stat-number">${formatCompactNumber(viewCount)}</span>
                    <span class="article-stat-label">조회수</span>
                </div>
            `;
        }

        const likeBtn = DomElements.ArticleDetail.getLikeBtn();
        const likeCountElement = DomElements.ArticleDetail.getLikeCount();
        const isLiked = resolveLikeState(article);

        if (likeBtn) {
            likeBtn.dataset.liked = isLiked ? 'true' : 'false';
            likeBtn.setAttribute('aria-pressed', isLiked ? 'true' : 'false');
        }

        if (likeCountElement) {
            likeCountElement.textContent = formatCompactNumber(likeCount);
        }
    }

    function renderComments(comments = []) {
        const container = DomElements.Comment.getList();
        if (!container) return;

        // Update comment count in title
        const commentFormTitle = DomElements.Comment.getFormTitle();
        if (commentFormTitle) {
            const count = comments?.length || 0;
            commentFormTitle.textContent = `댓글 ${count}개`;
        }

        container.innerHTML = '';

        if (!comments || comments.length === 0) {
            container.innerHTML = '<p style="text-align:center;color:#666;">댓글이 없습니다.</p>';
            return;
        }

        comments.forEach(comment => {
            const commentId = comment.id;
            const commentNickname = comment.user_nickname || '익명';
            const commentDate = formatDate(comment.last_modified_date);
            const commentContent = comment.content || '';
            const canModify = comment.can_modify === true || comment.can_modify === 'true';

            const wrapper = document.createElement('div');
            wrapper.className = 'comment-item';
            wrapper.innerHTML = `
                <div class="comment-header">
                    <div class="comment-author">
                        <div class="comment-author-avatar"></div>
                        <span class="comment-author-name">${escapeHtml(commentNickname)}</span>
                        <span class="comment-date">${commentDate}</span>
                    </div>
                    ${canModify ? `
                    <div class="comment-actions">
                        <button class="btn btn-secondary" data-action="edit-comment">수정</button>
                        <button class="btn btn-danger" data-action="delete-comment">삭제</button>
                    </div>
                    ` : ''}
                </div>
                <p class="comment-content">${escapeHtml(commentContent)}</p>
            `;

            if (canModify) {
                const editBtn = wrapper.querySelector('[data-action="edit-comment"]');
                if (editBtn) {
                    editBtn.addEventListener('click', () => enterCommentEditMode({
                        id: commentId,
                        content: commentContent
                    }));
                }

                const deleteBtn = wrapper.querySelector('[data-action="delete-comment"]');
                if (deleteBtn) {
                    deleteBtn.addEventListener('click', () => showDeleteCommentModal(commentId));
                }
            }

            container.appendChild(wrapper);
        });
    }

    /* -------------------------------------------------------------------------- */
    /* Like button                                                                */
    /* -------------------------------------------------------------------------- */

    async function handleLikeButtonClick() {
        if (!currentArticle || isLikeProcessing) return;

        try {
            isLikeProcessing = true;
            await ArticleApi.likeArticle(currentArticle.id);
            await loadArticleDetail(currentArticle.id);
        } catch (error) {
            console.error('Failed to toggle like:', error);
            showErrorAlert(error.message || '좋아요 처리 중 오류가 발생했습니다.');
        } finally {
            isLikeProcessing = false;
        }
    }

    /* -------------------------------------------------------------------------- */
    /* Comment form                                                               */
    /* -------------------------------------------------------------------------- */

    function updateCommentSubmitButtonState() {
        const textarea = DomElements.Comment.getContent();
        const submitBtn = DomElements.Comment.getSubmitBtn();
        if (!textarea || !submitBtn) return;

        const hasText = textarea.value.trim().length > 0;
        submitBtn.disabled = !hasText;
        submitBtn.classList.toggle('btn-comment-enabled', hasText);
        submitBtn.classList.toggle('btn-comment-disabled', !hasText);
    }

    function handleCommentInput() {
        updateCommentSubmitButtonState();
    }

    function enterCommentEditMode(comment) {
        const textarea = DomElements.Comment.getContent();
        const submitBtn = DomElements.Comment.getSubmitBtn();
        const resetBtn = DomElements.Comment.getResetBtn();

        if (!textarea || !submitBtn || !resetBtn) return;

        commentEditState = {
            mode: 'edit',
            commentId: comment.id
        };

        textarea.value = comment.content || '';
        submitBtn.textContent = '댓글 수정';
        resetBtn.style.display = 'inline';
        updateCommentSubmitButtonState();
        textarea.focus();
    }

    function resetCommentFormState() {
        const textarea = DomElements.Comment.getContent();
        const submitBtn = DomElements.Comment.getSubmitBtn();
        const resetBtn = DomElements.Comment.getResetBtn();

        commentEditState = { mode: 'create', commentId: null };

        if (textarea) {
            textarea.value = '';
        }
        if (submitBtn) {
            submitBtn.textContent = '댓글 등록';
        }
        if (resetBtn) {
            resetBtn.style.display = 'none';
        }

        updateCommentSubmitButtonState();
    }

    async function handleCommentSubmit(event) {
        event.preventDefault();

        const textarea = DomElements.Comment.getContent();
        const submitBtn = DomElements.Comment.getSubmitBtn();
        if (!textarea || !submitBtn) return;

        const content = textarea.value.trim();
        if (!content) {
            updateCommentSubmitButtonState();
            return;
        }

        submitBtn.disabled = true;

        try {
            if (commentEditState.mode === 'edit' && commentEditState.commentId) {
                await ArticleApi.updateComment(detailArticleId, commentEditState.commentId, content);
                showToast('댓글이 수정되었습니다.');
            } else {
                await ArticleApi.createComment(detailArticleId, content);
                showToast('댓글이 등록되었습니다.');
            }
            resetCommentFormState();
            await loadArticleDetail(detailArticleId);
        } catch (error) {
            showErrorAlert(error.message);
        } finally {
            submitBtn.disabled = false;
            updateCommentSubmitButtonState();
        }
    }

    function showDeleteCommentModal(commentId) {
        showModal(
            '댓글을 삭제하시겠습니까?',
            '삭제된 내용은 복구할 수 없습니다.',
            async () => {
                try {
                    await ArticleApi.deleteComment(detailArticleId, commentId);
                    showToast('댓글이 삭제되었습니다.');
                    resetCommentFormState();
                    await loadArticleDetail(detailArticleId);
                } catch (error) {
                    showErrorAlert(error.message);
                }
            }
        );
    }

    function showDeleteArticleModal() {
        showModal(
            '게시글을 삭제하시겠습니까?',
            '삭제된 내용은 복구할 수 없습니다.',
            async () => {
                try {
                    const result = await ArticleApi.deleteArticle(detailArticleId);
                    const redirectUrl = result?.redirectUrl;
                    showToast('게시글이 삭제되었습니다.');
                    setTimeout(() => {
                        window.location.href = redirectUrl || PageRoutes.ARTICLES;
                    }, 500);
                } catch (error) {
                    showErrorAlert(error.message);
                }
            }
        );
    }

    function bindCommentInteractions() {
        const commentContent = DomElements.Comment.getContent();
        if (commentContent) {
            commentContent.addEventListener('input', handleCommentInput);
            resetCommentFormState();
        }

        const commentResetBtn = DomElements.Comment.getResetBtn();
        if (commentResetBtn) {
            commentResetBtn.addEventListener('click', resetCommentFormState);
        }

        const commentForm = DomElements.Comment.getForm();
        if (commentForm) {
            commentForm.addEventListener('submit', handleCommentSubmit);
        }
    }

    function bindArticleDetailInteractions() {
        bindCommentInteractions();

        const likeBtn = DomElements.ArticleDetail.getLikeBtn();
        if (likeBtn) {
            likeBtn.addEventListener('click', handleLikeButtonClick);
        }
    }

    /* -------------------------------------------------------------------------- */
    /* Public initializer                                                         */
    /* -------------------------------------------------------------------------- */

    async function initArticleDetailPage(articleIdParam) {
        detailArticleId = articleIdParam || UriUtils.getPathSegment(1);
        if (!detailArticleId) return;

        await loadArticleDetail(detailArticleId);
        bindArticleDetailInteractions();
    }

    // Public API
    return {
        initArticleDetailPage
    };
})();

// Export the public function
export const { initArticleDetailPage } = createDetailHandlerModule;
