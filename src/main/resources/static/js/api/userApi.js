// 사용자 API 모듈
import { ApiEndpoints, PageRoutes } from '/js/common/uris.js';
import { csrfFetch, resetCsrfToken } from '/js/common/auth/csrf.js';
import { handleResponse } from '/js/api/apiUtils.js';

export const UserApi = {
    // 로그인
    async login(email, password) {
        resetCsrfToken();
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
    async signup(email, password, nickname, profileImageBase64) {
        resetCsrfToken();
        const response = await csrfFetch(ApiEndpoints.USER_SIGNUP, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                email: email,
                password: password,
                nickname: nickname,
                profile_image_path: profileImageBase64
            })
        });

        return await handleResponse(response, PageRoutes.USER_LOGIN);
    },

    // 로그아웃
    async logout() {
        resetCsrfToken();
        const response = await csrfFetch(ApiEndpoints.AUTH_LOGOUT, {
            method: 'POST',
        });

        return await handleResponse(response, PageRoutes.USER_LOGIN);
    },

    // 현재 사용자 조회
    async getCurrentUser() {
        resetCsrfToken();
        const response = await csrfFetch(ApiEndpoints.USER_ME, {
            method: 'GET',
        });

        return await handleResponse(response, undefined);
    },

    // 사용자 프로필 수정
    async updateProfile(formData) {
        resetCsrfToken();
        const response = await csrfFetch(ApiEndpoints.USER_ME, {
            method: 'PATCH',
            body: formData,
        });

        return await handleResponse(response, undefined);
    },

    // 비밀번호 수정
    async updatePassword(currentPassword, newPassword) {
        resetCsrfToken();
        const response = await csrfFetch(ApiEndpoints.USER_ME_PASSWORD, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ currentPassword, newPassword }),
        });

        return await handleResponse(response, undefined);
    },

    // 사용자 계정 삭제
    async deleteAccount() {
        resetCsrfToken();
        const response = await csrfFetch(ApiEndpoints.USER_ME, {
            method: 'DELETE',
        });

        return await handleResponse(response, undefined);
    },

    async checkNicknameExists(nickname) {
        resetCsrfToken();
        const response = await csrfFetch(ApiEndpoints.USER_EXIST_NICKNAME, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ nickname: nickname }),
        });

        const exist = await handleResponse(response, undefined);
        return exist?.data ?? false;
    },

    async checkEmailExists(email) {
        resetCsrfToken();
        const response = await csrfFetch(ApiEndpoints.USER_EXIST_EMAIL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ email: email }),
        });

        const exist = await handleResponse(response, undefined);
        return exist?.data ?? false;
    }
};
