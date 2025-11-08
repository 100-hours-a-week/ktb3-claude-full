import {
    showModal,
    showError,
    showSuccess,
    showErrorAlert,
    clearAllErrors,
    formatDate,
    previewArticleImage,
} from '../common/event.js';
import { ArticleApi } from '../api/articleApi.js';
import { PageRoutes, UriUtils } from '../common/uris.js';
import { DomElements } from '../common/domElements.js';

// Article handler for article CRUD operations and comments

let currentUser = null;
let currentArticle = null;
let isLikeProcessing = false;
let commentEditState = { mode: 'create', commentId: null };
let detailArticleId = null;

/* -------------------------------------------------------------------------- */
/* Helpers                                                                    */
/* -------------------------------------------------------------------------- */

function getCookieValue(name) {
    if (typeof document === 'undefined') return null;
    const match = document.cookie
        ?.split(';')
        .map(cookie => cookie.trim())
        .find(cookie => cookie.startsWith(`${name}=`));
    return match ? decodeURIComponent(match.substring(name.length + 1)) : null;
}

function decodeJwtPayload(token) {
    if (!token) return null;
    const parts = token.split('.');
    if (parts.length < 2) return null;
    try {
        const base64 = parts[1].replace(/-/g, '+').replace(/_/g, '/');
        const padded = base64.padEnd(base64.length + (4 - (base64.length % 4 || 4)) % 4, '=');
        const decoded = atob(padded);
        const jsonPayload = decodeURIComponent(
            Array.from(decoded)
                .map(c => `%${c.charCodeAt(0).toString(16).padStart(2, '0')}`)
                .join('')
        );
        return JSON.parse(jsonPayload);
    } catch (error) {
        console.warn('Failed to decode JWT payload:', error);
        return null;
    }
}

function extractUserFromJwt() {
    const token = getCookieValue('jwt');
    const payload = decodeJwtPayload(token);
    if (!payload) return null;

    const id = payload.userId ?? payload.sub;
    const nickname = payload.userNickname ?? payload.nickName ?? payload.nickname;
    if (!id && !nickname) return null;

    return { id, nickname };
}

function normalizeIdentifier(value) {
    if (value === undefined || value === null) return null;
    return String(value);
}

function normalizeNickname(value) {
    if (!value || typeof value !== 'string') return null;
    return value.trim();
}

function parseNumber(value) {
    const num = Number(value);
    return Number.isNaN(num) ? 0 : num;
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

function resolveDateValue(...candidates) {
    for (const value of candidates) {
        if (value) return formatDate(value);
    }
    return '';
}

function resolveUserMeta(entity = {}) {
    if (typeof entity !== 'object') {
        return { id: null, nickname: null };
    }

    const nested = entity.userAccount || entity.user_account || {};
    const id =
        entity.user_id ??
        entity.userId ??
        entity.create_by ??
        entity.createBy ??
        nested.id ??
        null;
    const nickname =
        entity.user_nickname ??
        entity.userNickname ??
        entity.create_nickname ??
        entity.createNickname ??
        nested.nickname ??
        null;

    return { id, nickname };
}

function isCurrentUser(meta) {
    if (!currentUser || !meta) return false;

    const currentId = normalizeIdentifier(currentUser.id);
    const targetId = normalizeIdentifier(meta.id);
    if (currentId && targetId) {
        return currentId === targetId;
    }

    const currentNickname = normalizeNickname(currentUser.nickname);
    const targetNickname = normalizeNickname(meta.nickname);
    return Boolean(currentNickname && targetNickname && currentNickname === targetNickname);
}

function resolveLikeState(article = {}) {
    const value = article.is_liked ?? article.isLiked ?? article.like_yn ?? article.likeYn ?? false;
    if (typeof value === 'string') {
        return value.toUpperCase() === 'Y';
    }
    return Boolean(value);
}

async function ensureCurrentUser() {
    if (currentUser) return currentUser;
    currentUser = extractUserFromJwt();
    return currentUser;
}

/* -------------------------------------------------------------------------- */
/* Article list                                                               */
/* -------------------------------------------------------------------------- */

async function loadArticleList() {
    try {
        const response = await ArticleApi.getArticles();
        renderArticleList(response?.data || response?.content || response);


    } catch (error) {
        console.error('Failed to load articles:', error);
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
        card.addEventListener('click', () => location.href = `/article/${article.id}`);

        const likeCount = formatCompactNumber(article.like_cnt ?? article.likeCount);
        const commentCount = formatCompactNumber(article.comment_cnt ?? article.commentCount);
        const viewCount = formatCompactNumber(article.view_cnt ?? article.viewCount);
        const createdAt = resolveDateValue(article.created_at, article.create_at, article.createAt);
        const author = escapeHtml(article.user_nickname || article.userNickname || '익명');

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

        try {
            const commentsResponse = await ArticleApi.getComments(id);
            const comments = commentsResponse?.data || commentsResponse;
            renderComments(comments);
        } catch (error) {
            console.error('Failed to load comments:', error);
            renderComments([]);
        }
    } catch (error) {
        console.error('Failed to load article:', error);
        showErrorAlert('게시글을 불러오는데 실패했습니다.');
    }
}

function renderArticleDetail(article) {
    if (!article) return;

    const authorMeta = resolveUserMeta({ user_nickname: article.user_nickname, user_id: article.user_id });
    const isAuthor = isCurrentUser(authorMeta);

    const headerContainer = DomElements.ArticleDetail.getHeader();
    const createdAt = resolveDateValue(
            article.last_modified_date,
            article.updated_at,
            article.update_at,
            article.created_at,
            article.create_at
    );

    if (headerContainer) {
        headerContainer.innerHTML = `
            <div class="article-detail-header-top">
                <div>
                    <h2 class="article-detail-title">${escapeHtml(article.title)}</h2>
                    <div class="article-detail-author">
                        <div class="article-detail-author-avatar"></div>
                        <span class="article-detail-author-name">${escapeHtml(authorMeta.nickname || '익명')}</span>
                    </div>
                    <span class="article-detail-date">${createdAt}</span>
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
            imageContainer.innerHTML = '';
        }
    }

    const contentContainer = DomElements.ArticleDetail.getContent();
    if (contentContainer) {
        contentContainer.textContent = article.content || '';
    }

    const likeCount = parseNumber(article.like_cnt ?? article.likeCount);
    const commentCount = parseNumber(article.comment_cnt ?? article.commentCount);
    const viewCount = parseNumber(article.view_cnt ?? article.viewCount);

    const statsContainer = DomElements.ArticleDetail.getStats();
    if (statsContainer) {
        statsContainer.innerHTML = `
            <div class="article-stat">
                <span class="article-stat-number">${formatCompactNumber(likeCount)}</span>
                <span class="article-stat-label">좋아요수</span>
            </div>
            <div class="article-stat">
                <span class="article-stat-number">${formatCompactNumber(commentCount)}</span>
                <span class="article-stat-label">댓글</span>
            </div>
            <div class="article-stat">
                <span class="article-stat-number">${formatCompactNumber(viewCount)}</span>
                <span class="article-stat-label">조회수</span>
            </div>
        `;
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
        const meta = resolveUserMeta({
            user_id: comment.user_id ?? comment.create_by,
            user_nickname: comment.user_nickname
        });
        const commentId = comment.comment_id ?? comment.id ?? comment.commentId;
        const commentDate = resolveDateValue(
                comment.last_modified_date,
                comment.updated_at,
                comment.created_at
        );
        const commentContent = comment.comment_content ?? comment.content ?? '';
        const canModify = isCurrentUser(meta);

        const wrapper = document.createElement('div');
        wrapper.className = 'comment-item';
        wrapper.innerHTML = `
            <div class="comment-header">
                <div class="comment-author">
                    <div class="comment-author-avatar"></div>
                    <span class="comment-author-name">${escapeHtml(meta.nickname || '익명')}</span>
                </div>
                <div class="comment-header-actions">
                    <span class="comment-date">${commentDate}</span>
                    ${canModify ? `
                    <div class="comment-actions">
                        <button class="btn btn-secondary" data-action="edit-comment">수정</button>
                        <button class="btn btn-danger" data-action="delete-comment">삭제</button>
                    </div>
                    ` : ''}
                </div>
            </div>
            <p class="comment-content">${escapeHtml(commentContent)}</p>
        `;

        if (canModify) {
            const editBtn = wrapper.querySelector('[data-action="edit-comment"]');
            if (editBtn) {
                editBtn.addEventListener('click', () => enterCommentEditMode({
                    comment_id: commentId,
                    comment_content: commentContent
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
        commentId: comment.comment_id ?? comment.commentId
    };

    textarea.value = comment.comment_content ?? '';
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
            showSuccess('댓글이 수정되었습니다.');
        } else {
            await ArticleApi.createComment(detailArticleId, content);
            showSuccess('댓글이 등록되었습니다.');
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
                showSuccess('댓글이 삭제되었습니다.');
                resetCommentFormState();
                await loadArticleDetail(detailArticleId);
            } catch (error) {
                showErrorAlert(error.message);
            }
        }
    );
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

    const formData = new FormData();
    formData.append('title', title);
    formData.append('content', content);
    if (image) {
        formData.append('image', image);
    }

    try {
        if (articleIdValue) {
            const updateResult = await ArticleApi.updateArticle(articleIdValue, formData);
            const redirectUrl = updateResult?.redirectUrl;
            showSuccess('게시글이 수정되었습니다.');
            window.location.href = redirectUrl || PageRoutes.articleDetail(articleIdValue);
        } else {
            const result = await ArticleApi.createArticle(formData);
            const redirectUrl = result?.redirectUrl;
            showSuccess('게시글이 작성되었습니다.');
            if (redirectUrl) {
                window.location.href = redirectUrl;
                return;
            }
            const redirectId = result?.id;
            window.location.href = redirectId ? PageRoutes.articleDetail(redirectId) : PageRoutes.ARTICLES;
        }
    } catch (error) {
        showError('titleError', error.message);
    }
}

function showDeleteArticleModal() {
    showModal(
        '게시글을 삭제하시겠습니까?',
        '삭제된 내용은 복구할 수 없습니다.',
        async () => {
            try {
                const result = await ArticleApi.deleteArticle(detailArticleId);
                const redirectUrl = result?.redirectUrl;
                showSuccess('게시글이 삭제되었습니다.');
                window.location.href = redirectUrl || PageRoutes.ARTICLES;
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

/* -------------------------------------------------------------------------- */
/* Public initializers                                                        */
/* -------------------------------------------------------------------------- */

export async function initArticleDetailPage(articleIdParam) {
    detailArticleId = articleIdParam || UriUtils.getPathSegment(1);
    if (!detailArticleId) return;

    await ensureCurrentUser();
    await loadArticleDetail(detailArticleId);
    bindCommentInteractions();
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

    const articleForm = DomElements.ArticleForm.getForm();
    articleForm?.addEventListener('submit', handleArticleSubmit);
}
