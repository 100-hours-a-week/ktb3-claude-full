// 공통 이벤트 핸들러 및 유틸리티 함수
// 하위 호환성을 위해 분리된 모듈들을 re-export

import { DomElements } from '/js/common/domElements.js';

// 분리된 모듈들 re-export
export { eventManager } from '/js/common/event/eventManager.js';
export { showModal, hideModal } from '/js/common/event/modalManager.js';
export { showToast } from '/js/common/event/toastManager.js';
export { previewProfileImage, previewArticleImage } from '/js/common/event/imagePreview.js';
export { formatDate } from '/js/common/util/utils.js';

/* -------------------------------------------------------------------------- */
/* 인증                                                                        */
/* -------------------------------------------------------------------------- */

/**
 * 사용자 인증 상태 확인
 * @returns {Promise<boolean>} 인증되었으면 true, 그렇지 않으면 false
 */
export async function checkAuth() {
    try {
        const response = await fetch('/api/v1/users/me');
        return response.ok;
    } catch (error) {
        return false;
    }
}

/* -------------------------------------------------------------------------- */
/* 사용자 메뉴                                                                  */
/* -------------------------------------------------------------------------- */

export function toggleUserMenu() {
    const dropdown = DomElements.Common.getUserMenuDropdown();
    if (dropdown) {
        dropdown.style.display = dropdown.style.display === 'none' ? 'block' : 'none';
    }
}

// 외부 클릭 시 드롭다운 닫기
import { eventManager } from '/js/common/event/eventManager.js';

const closeDropdownHandler = function(event) {
    const userMenu = document.querySelector('.user-menu');
    const dropdown = DomElements.Common.getUserMenuDropdown();

    if (dropdown && userMenu && !userMenu.contains(event.target)) {
        dropdown.style.display = 'none';
    }
};
eventManager.add(document, 'click', closeDropdownHandler);

/* -------------------------------------------------------------------------- */
/* 폼 에러 처리                                                                 */
/* -------------------------------------------------------------------------- */

// 폼에 오류 메시지 표시
export function showError(elementId, message) {
    const element = DomElements.manager.get(elementId);
    if (element) {
        element.textContent = message;
        element.style.display = 'block';
    }
}

// 오류 메시지 지우기
export function clearError(elementId) {
    const element = DomElements.manager.get(elementId);
    if (element) {
        element.textContent = '';
        element.style.display = 'none';
    }
}

// 모든 오류 지우기
export function clearAllErrors() {
    const errors = document.querySelectorAll('.form-helper');
    errors.forEach(error => {
        error.textContent = '';
        error.style.display = 'none';
    });
}

// 성공 메시지 표시 (현재 브라우저 알림 사용)
export function showSuccess(message) {
    alert(message);
}

// 오류 메시지 표시 (현재 브라우저 알림 사용)
export function showErrorAlert(message) {
    alert(message);
}
