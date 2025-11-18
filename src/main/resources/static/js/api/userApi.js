// 사용자 API 모듈
import { ApiEndpoints, PageRoutes } from '/js/common/uris.js';
import { csrfFetch } from '/js/common/csrf.js';

const DEFAULT_ERROR_MESSAGE = '요청 처리에 실패했습니다.';

async function parseJsonSafe(response) {
    const contentType = response.headers.get('Content-Type') || '';
    if (response.status === 204 || !contentType.includes('application/json')) {
        return null;
    }
    return await response.json();
}

async function extractErrorMessage(response) {
    try {
        const data = await parseJsonSafe(response);
        if (!data) return DEFAULT_ERROR_MESSAGE;
        if (typeof data === 'string') return data;
        if (data.message) return data.message;
        if (data.error) return data.error;
        return DEFAULT_ERROR_MESSAGE;
    } catch (e) {
        return DEFAULT_ERROR_MESSAGE;
    }
}

async function handleResponse(response, fallbackRedirect) {
    const redirectUrl =
        response.redirected || (response.status >= 300 && response.status < 400)
            ? response.headers.get('Location') || response.url || fallbackRedirect
            : null;

    if (redirectUrl) {
        return { redirectUrl };
    }

    if (!response.ok) {
        throw new Error(await extractErrorMessage(response));
    }

    return await parseJsonSafe(response);
}

export const UserApi = {
    // 로그인
    async login(email, password) {
        const response = await csrfFetch(ApiEndpoints.AUTH_LOGIN, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ email, password }),
        });

        return await handleResponse(response, PageRoutes.ARTICLES);
    },

    // 회원가입
    async signup(formData) {
        const response = await csrfFetch(ApiEndpoints.USER_SIGNUP, {
            method: 'POST',
            body: formData, // FormData에 파일 업로드 포함
        });

        return await handleResponse(response, PageRoutes.USER_LOGIN);
    },

    // 로그아웃
    async logout() {
        const response = await csrfFetch(ApiEndpoints.AUTH_LOGOUT, {
            method: 'POST',
        });

        return await handleResponse(response, PageRoutes.USER_LOGIN);
    },

    // 현재 사용자 조회
    async getCurrentUser() {
        const response = await csrfFetch(ApiEndpoints.USER_ME, {
            method: 'GET',
        });

        return await handleResponse(response);
    },

    // 사용자 프로필 수정
    async updateProfile(formData) {
        const response = await csrfFetch(ApiEndpoints.USER_ME, {
            method: 'PUT',
            body: formData,
        });

        return await handleResponse(response);
    },

    // 비밀번호 수정
    async updatePassword(currentPassword, newPassword) {
        const response = await csrfFetch(ApiEndpoints.USER_ME_PASSWORD, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ currentPassword, newPassword }),
        });

        return await handleResponse(response);
    },

    // 사용자 계정 삭제
    async deleteAccount() {
        const response = await csrfFetch(ApiEndpoints.USER_ME, {
            method: 'DELETE',
        });

        return await handleResponse(response);
    },

    async checkNicknameExists(nickname) {
        const response = await csrfFetch(ApiEndpoints.USER_EXIST_NICKNAME, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: nickname,
        });

        const result = await handleResponse(response);
        return result?.data ?? false;
    },

    async checkEmailExists(email) {
        const response = await csrfFetch(ApiEndpoints.USER_EXIST_EMAIL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: email,
        });

        const result = await handleResponse(response);
        return result?.data ?? false;
    }
};
