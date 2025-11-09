import {
    clearAllErrors,
    formatDate,
    previewArticleImage,
    showError,
    showErrorAlert,
    showModal,
    showSuccess,
    showToast,
} from '../common/event.js';
import { ArticleApi } from '../api/articleApi.js';
import { PageRoutes, UriUtils } from '../common/uris.js';
import { DomElements } from '../common/domElements.js';

// Article handler for article CRUD operations and comments

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
/* Article list                                                               */
/* -------------------------------------------------------------------------- */

async function loadArticleList() {
    try {
        const response = await ArticleApi.getArticles();
        console.log('Article API response:', response);

        // Handle both response.data and direct array response
        const articles = response?.data || response;
        renderArticleList(articles);
    } catch (error) {
        console.error('Failed to load articles:', error);
        showErrorAlert('게시글을 불러오는데 실패했습니다.');
    }
}

function renderArticleList(articles = []) {
    const container = DomElements.ArticleList.getContainer();
    if (!container) return;
    container.innerHTML = '';

    if (!articles || articles.length === 0) {
        container.innerHTML = '<p style="text-align:center;color:#666;">게시글이 없습니다.</p>';
        return;
    }

    articles.forEach(article => {
        const card = document.createElement('div');
        card.className = 'article-card';
        card.addEventListener('click', () => location.href = PageRoutes.articleDetail(article.id));

        const likeCount = formatCompactNumber(article.like_cnt);
        const commentCount = formatCompactNumber(article.comment_cnt);
        const viewCount = formatCompactNumber(article.view_cnt);
        const createdAt = formatDate(article.last_modified_date);
        const author = escapeHtml(article.user_nickname || '익명');

        card.innerHTML = `
            <div class="article-card-header">
                <div>
                    <h3 class="article-card-title">${escapeHtml(article.title)}</h3>
                    <div class="article-card-meta">
                        <span>좋아요 ${likeCount}</span>
                        <span>댓글 ${commentCount}</span>
                        <span>조회수 ${viewCount}</span>
                    </div>
                </div>
                <span class="article-card-date">${createdAt}</span>
            </div>
            <div class="article-card-author">
                <div class="article-card-author-avatar"></div>
                <span class="article-card-author-name">${author}</span>
            </div>
        `;

        container.appendChild(card);
    });
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
                editBtn.addEventListener('click', () => location.href = `/article/${article.id}/edit`);
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
    if (contentContainer) {
        contentContainer.innerHTML = escapeHtml(article.content || '').replace(/\n/g, '<br>');
    }

    const likeCount = parseNumber(article.like_cnt);
    const viewCount = parseNumber(article.view_cnt);
    const commentCount = parseNumber(
        article.comment_cnt ??
        (Array.isArray(article.comment) ? article.comment.length : 0)
    );

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
            <div class="article-stat">
                <span class="article-stat-number">${formatCompactNumber(commentCount)}</span>
                <span class="article-stat-label">댓글</span>
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
    const title = DomElements.Comment.getFormTitle();
    const submitBtn = DomElements.Comment.getSubmitBtn();
    const resetBtn = DomElements.Comment.getResetBtn();

    if (!textarea || !submitBtn || !title || !resetBtn) return;

    commentEditState = {
        mode: 'edit',
        commentId: comment.id
    };

    textarea.value = comment.content || '';
    title.textContent = '댓글을 수정합니다';
    submitBtn.textContent = '댓글 수정';
    resetBtn.style.display = 'inline';
    updateCommentSubmitButtonState();
    textarea.focus();
}

function resetCommentFormState() {
    const textarea = DomElements.Comment.getContent();
    const title = DomElements.Comment.getFormTitle();
    const submitBtn = DomElements.Comment.getSubmitBtn();
    const resetBtn = DomElements.Comment.getResetBtn();

    commentEditState = { mode: 'create', commentId: null };

    if (textarea) {
        textarea.value = '';
    }
    if (title) {
        title.textContent = '댓글을 남겨주세요!';
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

/* -------------------------------------------------------------------------- */
/* Article form validation                                                    */
/* -------------------------------------------------------------------------- */

function validateArticleForm() {
    const title = DomElements.ArticleForm.getTitleValue();
    const content = DomElements.ArticleForm.getContentValue();
    const submitBtn = DomElements.ArticleForm.getSubmitBtn();
    const titleError = DomElements.manager.get('titleError');
    const contentError = DomElements.manager.get('contentError');

    const isTitleValid = title && title.trim().length > 0;
    const isContentValid = content && content.trim().length > 0;
    const isFormValid = isTitleValid && isContentValid;

    // Show/hide error messages
    if (titleError && contentError) {
        if (!isFormValid && (!isTitleValid || !isContentValid)) {
            if (!isTitleValid && !isContentValid) {
                titleError.textContent = '*제목, 내용을 모두 작성해주세요.';
                titleError.style.display = 'block';
                contentError.style.display = 'none';
            } else if (!isTitleValid) {
                titleError.textContent = '*제목을 작성해주세요.';
                titleError.style.display = 'block';
                contentError.style.display = 'none';
            } else if (!isContentValid) {
                contentError.textContent = '*내용을 작성해주세요.';
                contentError.style.display = 'block';
                titleError.style.display = 'none';
            }
        } else {
            titleError.style.display = 'none';
            contentError.style.display = 'none';
        }
    }

    // Update button state
    if (submitBtn) {
        submitBtn.disabled = !isFormValid;
        if (isFormValid) {
            submitBtn.style.backgroundColor = '#7F6AEE';
        } else {
            submitBtn.style.backgroundColor = '#ACA0EB';
        }
    }

    return isFormValid;
}

/* -------------------------------------------------------------------------- */
/* Article CRUD (create/update/delete)                                        */
/* -------------------------------------------------------------------------- */

async function loadArticleForEdit(id) {
    try {
        const articleResponse = await ArticleApi.getArticle(id);
        const article = articleResponse?.data || articleResponse;

        DomElements.manager.setText('formTitle', '게시글 수정');
        DomElements.manager.setText('submitBtn', '수정하기');

        DomElements.manager.setValue('articleId', article.id);
        DomElements.manager.setValue('title', article.title);
        DomElements.manager.setValue('content', article.content);

        if (article.image_url || article.imageUrl) {
            DomElements.manager.setText('fileNameText', '기존 파일');
            const previewImg = DomElements.ArticleForm.getPreviewImg();
            const imagePreview = DomElements.ArticleForm.getImagePreview();
            if (previewImg && imagePreview) {
                previewImg.src = article.image_url || article.imageUrl;
                imagePreview.style.display = 'block';
            }
        }
    } catch (error) {
        console.error('Failed to load article for edit:', error);
        showErrorAlert('게시글을 불러오지 못했습니다.');
    }
}

async function handleArticleSubmit(event) {
    event.preventDefault();
    clearAllErrors();

    const articleIdInput = DomElements.ArticleForm.getArticleId();
    const articleIdValue = articleIdInput ? articleIdInput.value : '';
    const title = DomElements.ArticleForm.getTitleValue();
    const content = DomElements.ArticleForm.getContentValue();
    const imageInput = DomElements.ArticleForm.getImageInput();
    const image = imageInput?.files?.[0];

    // 서버가 @RequestBody JSON을 받으므로 JSON 형태로 전송
    const requestBody = {
        title,
        content,
        article_image_path: ''
    };

    // 이미지가 있으면 base64로 인코딩하여 전송
    if (image) {
        try {
            const base64Image = await convertImageToBase64(image);
            requestBody.article_image_path = base64Image;
        } catch (error) {
            showError('imageError', '이미지 처리 중 오류가 발생했습니다.');
            return;
        }
    }

    try {
        if (articleIdValue) {
            const updateResult = await ArticleApi.updateArticle(articleIdValue, requestBody);
            const redirectUrl = updateResult?.redirectUrl;
            showToast('게시글이 수정되었습니다.');
            setTimeout(() => {
                window.location.href = redirectUrl || PageRoutes.articleDetail(articleIdValue);
            }, 500);
        } else {
            const result = await ArticleApi.createArticle(requestBody);
            const redirectUrl = result?.redirectUrl;
            showToast('게시글이 작성되었습니다.');
            setTimeout(() => {
                if (redirectUrl) {
                    window.location.href = redirectUrl;
                } else {
                    const redirectId = result?.id;
                    window.location.href = redirectId ? PageRoutes.articleDetail(redirectId) : PageRoutes.ARTICLES;
                }
            }, 500);
        }
    } catch (error) {
        showError('titleError', error.message);
    }
}

// 이미지를 Base64로 변환하는 헬퍼 함수
function convertImageToBase64(file) {
    return new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.onload = () => resolve(reader.result);
        reader.onerror = reject;
        reader.readAsDataURL(file);
    });
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
/* Public initializers                                                        */
/* -------------------------------------------------------------------------- */

export async function initArticleDetailPage(articleIdParam) {
    detailArticleId = articleIdParam || UriUtils.getPathSegment(1);
    if (!detailArticleId) return;

    await loadArticleDetail(detailArticleId);
    bindArticleDetailInteractions();
}

export function initArticleListPage() {
    loadArticleList();
}

export async function initArticleFormPage(articleIdParam) {
    const hiddenIdField = DomElements.ArticleForm.getArticleId();
    if (articleIdParam && hiddenIdField) {
        hiddenIdField.value = articleIdParam;
    }

    const fileUploadBtn = DomElements.ArticleForm.getFileUploadBtn();
    const imageInput = DomElements.ArticleForm.getImageInput();
    fileUploadBtn?.addEventListener('click', () => imageInput?.click());
    imageInput?.addEventListener('change', previewArticleImage);

    if (hiddenIdField?.value) {
        await loadArticleForEdit(hiddenIdField.value);
    }

    // Add validation event listeners
    const titleInput = DomElements.ArticleForm.getTitle();
    const contentInput = DomElements.ArticleForm.getContent();

    titleInput?.addEventListener('input', validateArticleForm);
    contentInput?.addEventListener('input', validateArticleForm);

    const articleForm = DomElements.ArticleForm.getForm();
    articleForm?.addEventListener('submit', handleArticleSubmit);

    // Initial validation
    validateArticleForm();
}
