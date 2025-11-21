// 공통 API 유틸리티 모듈

const DEFAULT_ERROR_MESSAGE = '요청 처리에 실패했습니다.';

/**
 * Response에서 안전하게 JSON을 파싱합니다.
 * @param {Response} response - Fetch API Response 객체
 * @returns {Promise<any|null>} 파싱된 JSON 데이터 또는 null
 */
export async function parseJsonSafe(response) {
    const contentType = response.headers.get('Content-Type') || '';
    if (response.status === 204 || !contentType.includes('application/json')) {
        return null;
    }
    return await response.json();
}

/**
 * Response에서 에러 메시지를 추출합니다.
 * @param {Response} response - Fetch API Response 객체
 * @param {string} defaultMessage - 기본 에러 메시지 (선택)
 * @returns {Promise<string>} 에러 메시지
 */
export async function extractErrorMessage(response, defaultMessage = DEFAULT_ERROR_MESSAGE) {
    try {
        const data = await parseJsonSafe(response);
        if (!data) return defaultMessage;
        if (typeof data === 'string') return data;
        if (data.message) return data.message;
        if (data.error) return data.error;
        return defaultMessage;
    } catch (e) {
        return defaultMessage;
    }
}

/**
 * Response를 처리하고 리다이렉트, 에러, JSON 데이터를 반환합니다.
 * @param {Response} response - Fetch API Response 객체
 * @param {string} fallbackRedirect - 리다이렉트 실패 시 사용할 URL (선택)
 * @param {string} defaultErrorMessage - 기본 에러 메시지 (선택)
 * @returns {Promise<any>} 처리된 데이터 또는 {redirectUrl}
 * @throws {Error} Response가 성공적이지 않을 때
 */
export async function handleResponse(response, fallbackRedirect, defaultErrorMessage = DEFAULT_ERROR_MESSAGE) {
    const redirectUrl =
        response.redirected || (response.status >= 300 && response.status < 400)
            ? response.headers.get('Location') || response.url || fallbackRedirect
            : null;

    if (redirectUrl) {
        return { redirectUrl };
    }

    if (!response.ok) {
        throw new Error(await extractErrorMessage(response, defaultErrorMessage));
    }

    return await parseJsonSafe(response);
}
