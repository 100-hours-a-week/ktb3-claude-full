let csrfTokenCache = null;

async function fetchCsrfToken() {
    if (csrfTokenCache) return csrfTokenCache;

    const res = await fetch('/api/v1/csrf', { credentials: 'include' });
    const data = await res.json();

    csrfTokenCache = data.token;

    return csrfTokenCache;
}

export async function csrfFetch(url, options = {}) {
    const token = await fetchCsrfToken();
    const headers = new Headers(options.headers || {});

    headers.set('X-XSRF-TOKEN', token);

    return fetch(url, {
        ...options,
        credentials: 'include',
        headers,
    });
}

// 필요 시 토큰 강제 갱신용
export function resetCsrfToken() {
    csrfTokenCache = null;
}