// 공통 이벤트 핸들러 및 유틸리티 함수
import { DomElements } from './domElements.js';

/* -------------------------------------------------------------------------- */
/* 이벤트 리스너 관리자 - 페이지 전환 시 정리용                                    */
/* -------------------------------------------------------------------------- */

class EventListenerManager {
    constructor() {
        this.listeners = [];
    }

    add(element, eventType, handler, options) {
        if (!element) return;
        element.addEventListener(eventType, handler, options);
        this.listeners.push({ element, eventType, handler, options });
    }

    removeAll() {
        this.listeners.forEach(({ element, eventType, handler, options }) => {
            element.removeEventListener(eventType, handler, options);
        });
        this.listeners = [];
    }
}

export const eventManager = new EventListenerManager();

// 페이지 이탈 시 이벤트 리스너 정리
window.addEventListener('beforeunload', () => {
    eventManager.removeAll();
});

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
const closeDropdownHandler = function(event) {
    const userMenu = document.querySelector('.user-menu');
    const dropdown = DomElements.Common.getUserMenuDropdown();

    if (dropdown && userMenu && !userMenu.contains(event.target)) {
        dropdown.style.display = 'none';
    }
};
eventManager.add(document, 'click', closeDropdownHandler);

const MODAL_IDS = {
    WRAPPER: 'globalModal',
    TITLE: 'globalModalTitle',
    MESSAGE: 'globalModalMessage',
    CANCEL: 'globalModalCancel',
    CONFIRM: 'globalModalConfirm',
};

function ensureModalStructure() {
    let modal = document.getElementById(MODAL_IDS.WRAPPER);
    if (!modal) {
        modal = document.createElement('div');
        modal.id = MODAL_IDS.WRAPPER;
        modal.className = 'modal';
        modal.innerHTML = `
            <div class="modal-content">
                <h3 id="${MODAL_IDS.TITLE}"></h3>
                <p id="${MODAL_IDS.MESSAGE}"></p>
                <div class="modal-buttons">
                    <button class="btn btn-secondary" id="${MODAL_IDS.CANCEL}">취소</button>
                    <button class="btn btn-primary" id="${MODAL_IDS.CONFIRM}">확인</button>
                </div>
            </div>
        `;
        document.body.appendChild(modal);
    }

    return {
        modal,
        title: document.getElementById(MODAL_IDS.TITLE),
        message: document.getElementById(MODAL_IDS.MESSAGE),
        cancel: document.getElementById(MODAL_IDS.CANCEL),
        confirm: document.getElementById(MODAL_IDS.CONFIRM),
    };
}

export function showModal(title, message, onConfirm) {
    const { modal, title: titleEl, message: messageEl, cancel, confirm } = ensureModalStructure();

    titleEl.textContent = title;
    messageEl.textContent = message;
    modal.style.display = 'flex';
    if (!document.body.dataset.prevOverflow) {
        document.body.dataset.prevOverflow = document.body.style.overflow || '';
    }
    document.body.style.overflow = 'hidden';

    const newCancel = cancel.cloneNode(true);
    cancel.parentNode.replaceChild(newCancel, cancel);
    const newConfirm = confirm.cloneNode(true);
    confirm.parentNode.replaceChild(newConfirm, confirm);

    const cancelHandler = () => hideModal();
    const confirmHandler = () => {
        hideModal();
        if (onConfirm) onConfirm();
    };

    eventManager.add(newCancel, 'click', cancelHandler);
    eventManager.add(newConfirm, 'click', confirmHandler);
}

export function hideModal() {
    const modal = document.getElementById(MODAL_IDS.WRAPPER);
    if (modal) {
        modal.style.display = 'none';
    }
    if (document.body.dataset.prevOverflow !== undefined) {
        document.body.style.overflow = document.body.dataset.prevOverflow;
        delete document.body.dataset.prevOverflow;
    }
}

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

// 토스트 메시지 표시
export function showToast(message, duration = 2000) {
    // 토스트가 이미 존재하는지 확인
    let toast = document.querySelector('.toast');

    if (!toast) {
        // 토스트 요소 생성
        toast = document.createElement('div');
        toast.className = 'toast';
        document.body.appendChild(toast);
    }

    // 메시지 설정
    toast.textContent = message;

    // 토스트 표시
    setTimeout(() => {
        toast.classList.add('show');
    }, 10);

    // 일정 시간 후 토스트 숨기기
    setTimeout(() => {
        toast.classList.remove('show');
    }, duration);
}

// 날짜 포맷
export function formatDate(dateString) {
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');
    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
}

// 프로필 이미지 미리보기
export function previewProfileImage(event) {
    const file = event.target.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = function(e) {
            const preview = DomElements.Signup.getPreviewImg() || DomElements.UserEdit.getProfilePreviewImg();
            if (preview) {
                preview.src = e.target.result;
                preview.style.display = 'block';
            }
        };
        reader.readAsDataURL(file);
    }
}

// 게시글 이미지 미리보기
export function previewArticleImage(event) {
    const file = event.target.files[0];
    const fileNameText = DomElements.ArticleForm.getFileNameText();
    const imagePreview = DomElements.ArticleForm.getImagePreview();
    const previewImg = DomElements.ArticleForm.getPreviewImg();

    if (file) {
        if (fileNameText) {
            fileNameText.textContent = file.name;
        }

        const reader = new FileReader();
        reader.onload = function(e) {
            if (previewImg) {
                previewImg.src = e.target.result;
            }
            if (imagePreview) {
                imagePreview.style.display = 'block';
            }
        };
        reader.readAsDataURL(file);
    }
}
