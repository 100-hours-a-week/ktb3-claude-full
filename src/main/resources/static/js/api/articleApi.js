// 게시글 API 모듈
import { ApiEndpoints, PageRoutes, UriUtils } from '/js/common/uris.js';
import { csrfFetch, resetCsrfToken } from '/js/common/auth/csrf.js';
import { handleResponse } from '/js/api/apiUtils.js';

const DEFAULT_ARTICLE_ERROR_MESSAGE = '게시글 요청 처리 중 문제가 발생했습니다.';

export const ArticleApi = {
    // 모든 게시글 조회
    async getArticles(after = 0, limit = 10) {
        const url = UriUtils.addQueryParams(ApiEndpoints.ARTICLE_LIST, { after, limit })

        resetCsrfToken();
        const response = await csrfFetch(url, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            },
        });
        return await handleResponse(response, undefined, DEFAULT_ARTICLE_ERROR_MESSAGE);
    },

    // ID로 게시글 조회
    async getArticle(id) {
        resetCsrfToken();
        const response = await csrfFetch(ApiEndpoints.article(id), {
            method: 'GET',
        });

        return await handleResponse(response, undefined, DEFAULT_ARTICLE_ERROR_MESSAGE);
    },

    // 게시글 생성
    async createArticle(requestBody) {
        resetCsrfToken();
        const response = await csrfFetch(ApiEndpoints.ARTICLE_BASE, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(requestBody),
        });

        return await handleResponse(response, PageRoutes.ARTICLES, DEFAULT_ARTICLE_ERROR_MESSAGE);
    },

    // 게시글 수정
    async updateArticle(id, requestBody) {
        resetCsrfToken();
        const response = await csrfFetch(ApiEndpoints.article(id), {
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

        return await handleResponse(response, PageRoutes.articleDetail(id), DEFAULT_ARTICLE_ERROR_MESSAGE);
    },

    // 게시글 삭제
    async deleteArticle(id) {
        resetCsrfToken();
        const response = await csrfFetch(ApiEndpoints.article(id), {
            method: 'DELETE',
        });

        return await handleResponse(response, PageRoutes.ARTICLES, DEFAULT_ARTICLE_ERROR_MESSAGE);
    },

    // 게시글 좋아요
    async likeArticle(id) {
        resetCsrfToken();
        const response = await csrfFetch(ApiEndpoints.articleLike(id), {
            method: 'POST',
        });

        return await handleResponse(response, undefined, DEFAULT_ARTICLE_ERROR_MESSAGE);
    },

    // 게시글의 댓글 조회
    async getComments(articleId) {
        resetCsrfToken();
        const response = await csrfFetch(ApiEndpoints.articleComments(articleId), {
            method: 'GET',
        });

        return await handleResponse(response, undefined, DEFAULT_ARTICLE_ERROR_MESSAGE);
    },

    // 댓글 생성
    async createComment(articleId, content) {
        resetCsrfToken();
        const response = await csrfFetch(ApiEndpoints.articleComments(articleId), {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ comment_content: content }),
        });

        return await handleResponse(response, undefined, DEFAULT_ARTICLE_ERROR_MESSAGE);
    },

    // 댓글 수정
    async updateComment(articleId, commentId, content) {
        resetCsrfToken();
        const response = await csrfFetch(ApiEndpoints.articleComments(articleId), {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                comment_id: commentId,
                comment_content: content,
            }),
        });

        return await handleResponse(response, PageRoutes.articleDetail(articleId), DEFAULT_ARTICLE_ERROR_MESSAGE);
    },

    // 댓글 삭제
    async deleteComment(articleId, commentId) {
        resetCsrfToken();
        const response = await csrfFetch(ApiEndpoints.articleComments(articleId), {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ comment_id: commentId }),
        });

        return await handleResponse(response, PageRoutes.articleDetail(articleId), DEFAULT_ARTICLE_ERROR_MESSAGE);
    },
};
