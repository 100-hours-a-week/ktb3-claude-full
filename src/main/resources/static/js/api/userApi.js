// User API Module
import { ApiEndpoints, PageRoutes } from '../common/uris.js';

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
    // Login
    async login(email, password) {
        const response = await fetch(ApiEndpoints.AUTH_LOGIN, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ email, password }),
        });

        return await handleResponse(response, PageRoutes.ARTICLES);
    },

    // Signup
    async signup(formData) {
        const response = await fetch(ApiEndpoints.USER_SIGNUP, {
            method: 'POST',
            body: formData, // FormData includes file upload
        });

        return await handleResponse(response, PageRoutes.USER_LOGIN);
    },

    // Logout
    async logout() {
        const response = await fetch(ApiEndpoints.AUTH_LOGOUT, {
            method: 'POST',
        });

        return await handleResponse(response, PageRoutes.USER_LOGIN);
    },

    // Get current user
    async getCurrentUser() {
        const response = await fetch(ApiEndpoints.USER_ME, {
            method: 'GET',
        });

        return await handleResponse(response);
    },

    // Update user profile
    async updateProfile(formData) {
        const response = await fetch(ApiEndpoints.USER_ME, {
            method: 'PUT',
            body: formData,
        });

        return await handleResponse(response);
    },

    // Update password
    async updatePassword(currentPassword, newPassword) {
        const response = await fetch(ApiEndpoints.USER_ME_PASSWORD, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ currentPassword, newPassword }),
        });

        return await handleResponse(response);
    },

    // Delete user account
    async deleteAccount() {
        const response = await fetch(ApiEndpoints.USER_ME, {
            method: 'DELETE',
        });

        return await handleResponse(response);
    },

    async checkNicknameExists(nickname) {
        const response = await fetch(ApiEndpoints.USER_EXIST_NICKNAME, {
            method: 'POST',
            headers: {
                'Content-Type': 'text/plain',
            },
            body: nickname,
        });

        const result = await handleResponse(response);
        return result?.data ?? false;
    },

    async checkEmailExists(email) {
        const response = await fetch(ApiEndpoints.USER_EXIST_EMAIL, {
            method: 'POST',
            headers: {
                'Content-Type': 'text/plain',
            },
            body: email,
        });

        const result = await handleResponse(response);
        return result?.data ?? false;
    }
};
