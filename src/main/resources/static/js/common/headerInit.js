/**
 * 헤더 초기화
 * 사용자 정보 로드 및 헤더 요소 업데이트
 */

import { DomElements } from './domElements.js';
import { PageRoutes } from './uris.js';

/**
 * 헤더에 사용자 정보 로드 및 표시
 */
export async function initializeHeaderUserInfo() {
    try {
        const response = await fetch('/api/v1/users/me');

        if (!response.ok) {
            return; // 사용자 로그인하지 않음
        }

        const data = await response.json();
        const user = data?.data || data;

        const nickname = user.nickName || user.nickname || user.user_nickname || user.userNickname || '사용자';

        // 헤더 사용자 이름 업데이트
        const headerUserName = DomElements.Header.getUserName();
        if (headerUserName) {
            headerUserName.textContent = nickname;
        }

        // 드롭다운 사용자 이름 업데이트
        const dropdownUserName = DomElements.Header.getDropdownUserName();
        if (dropdownUserName) {
            dropdownUserName.textContent = nickname;
        }

        // 헤더 생성 버튼에 클릭 핸들러 추가
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
