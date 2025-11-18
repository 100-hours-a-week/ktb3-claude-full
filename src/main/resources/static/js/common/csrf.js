/**
 * CSRF 토큰 관리 유틸리티
 * Spring Security의 CookieCsrfTokenRepository와 함께 동작
 */

/**
 * 쿠키에서 값 읽기
 */
function getCookie(name) {
    return document.cookie
        .split('; ')
        .find(row => row.startsWith(name + '='))
        ?.substring(name.length + 1);
}

/**
 * CSRF 토큰 가져오기
 */
export function getCsrfToken() {
    return getCookie('XSRF-TOKEN');
}

/**
 * CSRF 보호가 적용된 fetch 래퍼
 */
export async function csrfFetch(url, options = {}) {
    const token = getCsrfToken();
    console.log('[CSRF DEBUG] Token from cookie:', token);

    // Headers 객체 생성
    const headers = new Headers(options.headers || {});

    // CSRF 토큰 추가
    if (token) {
        headers.set('X-XSRF-TOKEN', token);
    }

    console.log('[CSRF DEBUG] Headers entries:', Array.from(headers.entries()));

    const finalOptions = {
        ...options,
        credentials: 'include',
        headers: headers
    };

    return fetch(url, finalOptions);
}

/**
 * CSRF 토큰 초기화 (페이지 로드 시)
 */
export async function initCsrf() {
    if (!getCsrfToken()) {
        await fetch('/api/v1/csrf', { credentials: 'include' });
    }
}
