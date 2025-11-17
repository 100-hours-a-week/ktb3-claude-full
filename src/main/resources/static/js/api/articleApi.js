// 게시글 API 모듈
import { ApiEndpoints, PageRoutes } from '/js/common/uris.js';

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
    // 모든 게시글 조회
    async getArticles(after = 0, limit = 10) {
        const url = new URL(ApiEndpoints.ARTICLE_LIST, window.location.origin);
        url.searchParams.append('after', after);
        url.searchParams.append('limit', limit);

        const response = await fetch(url, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            },
        });
        return await handleArticleResponse(response);
    },

    // ID로 게시글 조회
    async getArticle(id) {
        const response = await fetch(ApiEndpoints.article(id), {
            method: 'GET',
        });

        return await handleArticleResponse(response);
    },

    // 게시글 생성
    async createArticle(requestBody) {
        const response = await fetch(ApiEndpoints.ARTICLE_BASE, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(requestBody),
        });

        return await handleArticleResponse(response, PageRoutes.ARTICLES);
    },

    // 게시글 수정
    async updateArticle(id, requestBody) {
        const response = await fetch(ApiEndpoints.article(id), {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(requestBody),
        });

        // Location 헤더에서 리다이렉트 URL 추출
        const redirectUrl = response.headers.get('Location');
        if (redirectUrl) {
            return { redirectUrl };
        }

        return await handleArticleResponse(response, PageRoutes.articleDetail(id));
    },

    // 게시글 삭제
    async deleteArticle(id) {
        const response = await fetch(ApiEndpoints.article(id), {
            method: 'DELETE',
        });

        return await handleArticleResponse(response, PageRoutes.ARTICLES);
    },

    // 게시글 좋아요
    async likeArticle(id) {
        const response = await fetch(ApiEndpoints.articleLike(id), {
            method: 'POST',
        });

        return await handleArticleResponse(response);
    },

    // 게시글의 댓글 조회
    async getComments(articleId) {
        const response = await fetch(ApiEndpoints.articleComments(articleId), {
            method: 'GET',
        });

        return await handleArticleResponse(response);
    },

    // 댓글 생성
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

    // 댓글 수정
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

    // 댓글 삭제
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
