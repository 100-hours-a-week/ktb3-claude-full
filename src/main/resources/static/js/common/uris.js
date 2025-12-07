/**
 * URI 관리 클래스
 * 애플리케이션에서 사용하는 모든 URI를 중앙에서 관리합니다.
 */

/**
 * API 엔드포인트 URI
 */
export const ApiEndpoints = {
    // Auth
    AUTH_BASE: '/api/v1/auth',
    AUTH_LOGIN: '/api/v1/auth/login',
    AUTH_LOGOUT: '/api/v1/auth/logout',

    // Users
    USER_BASE: '/api/v1/users',
    USER_SIGNUP: '/api/v1/users/signup',
    USER_ME: '/api/v1/users/me',
    USER_ME_PASSWORD: '/api/v1/users/me/password',
    USER_EXIST_NICKNAME: '/api/v1/users/exist/nickname',
    USER_EXIST_EMAIL: '/api/v1/users/exist/email',

    // Articles
    ARTICLE_LIST: '/api/v1/articles',
    ARTICLE_BASE: '/api/v1/article',

    /**
     * 특정 게시글 URI를 반환합니다.
     * @param {number|string} id - 게시글 ID
     * @returns {string} 게시글 URI
     */
    article: (id) => `/api/v1/article/${id}`,

    /**
     * 게시글 좋아요 URI를 반환합니다.
     * @param {number|string} id - 게시글 ID
     * @returns {string} 좋아요 URI
     */
    articleLike: (id) => `/api/v1/article/${id}/like`,

    /**
     * 게시글 댓글 URI를 반환합니다.
     * @param {number|string} articleId - 게시글 ID
     * @returns {string} 댓글 URI
     */
    articleComments: (articleId) => `/api/v1/article/${articleId}/comments`,
};

/**
 * 페이지 URI
 */
export const PageRoutes = {
    // Home
    HOME: '/',

    // Articles
    ARTICLES: '/articles',
    ARTICLE_NEW: '/article/new',

    /**
     * 게시글 상세 페이지 URI를 반환합니다.
     * @param {number|string} id - 게시글 ID
     * @returns {string} 게시글 상세 페이지 URI
     */
    articleDetail: (id) => `/article/${id}`,

    /**
     * 게시글 수정 페이지 URI를 반환합니다.
     * @param {number|string} id - 게시글 ID
     * @returns {string} 게시글 수정 페이지 URI
     */
    articleEdit: (id) => `/article/${id}/edit`,

    // Users
    USER_LOGIN: '/user/login',
    USER_SIGNUP: '/user/signup',
    USER_EDIT: '/user/edit',
    USER_PASSWORD: '/user/password',
};

/**
 * Fragment (부분 HTML) URI
 */
export const FragmentPaths = {
    HEADER_AUTH: '/fragments/header-auth.html',
    HEADER_AUTH_WITH_BACK: '/fragments/header-auth-with-back.html',
    HEADER_WITH_BACK: '/fragments/header-with-back.html',
    HEADER_WITH_USER_MENU: '/fragments/header-with-user-menu.html',
};

/**
 * URI 유틸리티 함수
 */
export const UriUtils = {
    /**
     * 쿼리 파라미터를 URL에 추가합니다.
     * @param {string} baseUrl - 기본 URL
     * @param {Object} params - 쿼리 파라미터 객체
     * @returns {string} 쿼리 파라미터가 추가된 URL
     */
    addQueryParams(baseUrl, params) {
        if (!params || Object.keys(params).length === 0) {
            return baseUrl;
        }

        const url = new URL(baseUrl, window.location.origin);
        Object.entries(params).forEach(([key, value]) => {
            if (value !== null && value !== undefined) {
                url.searchParams.append(key, value);
            }
        });

        return url.pathname + url.search;
    },

    /**
     * 현재 URL에서 특정 파라미터 값을 가져옵니다.
     * @param {string} paramName - 파라미터 이름
     * @returns {string|null} 파라미터 값
     */
    getQueryParam(paramName) {
        const params = new URLSearchParams(window.location.search);
        return params.get(paramName);
    },

    /**
     * 현재 경로에서 특정 세그먼트를 가져옵니다.
     * @param {number} index - 세그먼트 인덱스 (0부터 시작)
     * @returns {string|null} 경로 세그먼트
     */
    getPathSegment(index) {
        const segments = window.location.pathname.split('/').filter(Boolean);
        return segments[index] || null;
    },

    /**
     * URL이 특정 패턴과 일치하는지 확인합니다.
     * @param {string} pattern - 패턴 (예: '/article/:id')
     * @returns {boolean} 일치 여부
     */
    matchesPattern(pattern) {
        const patternParts = pattern.split('/').filter(Boolean);
        const pathParts = window.location.pathname.split('/').filter(Boolean);

        if (patternParts.length !== pathParts.length) {
            return false;
        }

        return patternParts.every((part, i) => {
            if (part.startsWith(':')) {
                return true; // 동적 세그먼트는 모든 값과 일치
            }
            return part === pathParts[i];
        });
    },
};

// 기본 export (편의를 위해)
export default {
    Api: ApiEndpoints,
    Page: PageRoutes,
    Fragment: FragmentPaths,
    Utils: UriUtils,
};
