// Article API Module
import { ApiEndpoints, PageRoutes } from '../common/uris.js';

const DEFAULT_ARTICLE_ERROR_MESSAGE = '게시글 요청 처리 중 문제가 발생했습니다.';

async function parseArticleJsonSafe(response) {
    const contentType = response.headers.get('Content-Type') || '';
    if (response.status === 204 || !contentType.includes('application/json')) {
        return null;
    }
    return await response.json();
}

async function extractArticleErrorMessage(response) {
    try {
        const data = await parseArticleJsonSafe(response);
        if (!data) return DEFAULT_ARTICLE_ERROR_MESSAGE;
        if (typeof data === 'string') return data;
        if (data.message) return data.message;
        if (data.error) return data.error;
        return DEFAULT_ARTICLE_ERROR_MESSAGE;
    } catch (e) {
        return DEFAULT_ARTICLE_ERROR_MESSAGE;
    }
}

async function handleArticleResponse(response, fallbackRedirect) {
    const redirectUrl =
        response.redirected || (response.status >= 300 && response.status < 400)
            ? response.headers.get('Location') || response.url || fallbackRedirect
            : null;

    if (redirectUrl) {
        return { redirectUrl };
    }

    if (!response.ok) {
        throw new Error(await extractArticleErrorMessage(response));
    }

    return await parseArticleJsonSafe(response);
}

export const ArticleApi = {
    // Get all articles
    async getArticles(after = 0, limit = 10) {
        const response = await fetch(ApiEndpoints.ARTICLE_LIST, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ after, limit }),
        });
        return await handleArticleResponse(response);
    },

    // Get article by ID
    async getArticle(id) {
        const response = await fetch(ApiEndpoints.article(id), {
            method: 'GET',
        });

        return await handleArticleResponse(response);
    },

    // Create article
    async createArticle(formData) {
        const response = await fetch(ApiEndpoints.ARTICLE_BASE, {
            method: 'POST',
            body: formData,
        });

        return await handleArticleResponse(response, PageRoutes.ARTICLES);
    },

    // Update article
    async updateArticle(id, formData) {
        const response = await fetch(ApiEndpoints.article(id), {
            method: 'PUT',
            body: formData,
        });

        return await handleArticleResponse(response, PageRoutes.articleDetail(id));
    },

    // Delete article
    async deleteArticle(id) {
        const response = await fetch(ApiEndpoints.article(id), {
            method: 'DELETE',
        });

        return await handleArticleResponse(response, PageRoutes.ARTICLES);
    },

    // Like article
    async likeArticle(id) {
        const response = await fetch(ApiEndpoints.articleLike(id), {
            method: 'POST',
        });

        return await handleArticleResponse(response);
    },

    // Get comments for article
    async getComments(articleId) {
        const response = await fetch(ApiEndpoints.articleComments(articleId), {
            method: 'GET',
        });

        return await handleArticleResponse(response);
    },

    // Create comment
    async createComment(articleId, content) {
        const response = await fetch(ApiEndpoints.articleComments(articleId), {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ comment_content: content }),
        });

        return await handleArticleResponse(response);
    },

    // Update comment
    async updateComment(articleId, commentId, content) {
        const response = await fetch(ApiEndpoints.articleComments(articleId), {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                comment_id: commentId,
                comment_content: content,
            }),
        });

        return await handleArticleResponse(response, PageRoutes.articleDetail(articleId));
    },

    // Delete comment
    async deleteComment(articleId, commentId) {
        const response = await fetch(ApiEndpoints.articleComments(articleId), {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ comment_id: commentId }),
        });

        return await handleArticleResponse(response, PageRoutes.articleDetail(articleId));
    },
};
