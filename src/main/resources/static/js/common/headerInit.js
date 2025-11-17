/**
 * Header Initialization
 * Loads user information and updates header elements
 */

import { DomElements } from './domElements.js';
import { PageRoutes } from './uris.js';

/**
 * Load and display user information in header
 */
export async function initializeHeaderUserInfo() {
    try {
        const response = await fetch('/api/v1/users/me');

        if (!response.ok) {
            return; // User not logged in
        }

        const data = await response.json();
        const user = data?.data || data;

        const nickname = user.nickName || user.nickname || user.user_nickname || user.userNickname || '사용자';

        // Update header user name
        const headerUserName = DomElements.Header.getUserName();
        if (headerUserName) {
            headerUserName.textContent = nickname;
        }

        // Update dropdown user name
        const dropdownUserName = DomElements.Header.getDropdownUserName();
        if (dropdownUserName) {
            dropdownUserName.textContent = nickname;
        }

        // Add click handler to header create button
        const headerCreateBtn = DomElements.Header.getCreateArticleBtn();
        if (headerCreateBtn) {
            headerCreateBtn.addEventListener('click', (e) => {
                e.preventDefault();
                window.location.href = PageRoutes.ARTICLE_NEW;
            });
        }

    } catch (error) {
        console.error('Failed to load user info:', error);
    }
}
