// 모달 관리자

import { eventManager } from '/js/common/event/eventManager.js';

const MODAL_IDS = {
    WRAPPER: 'globalModal',
    TITLE: 'globalModalTitle',
    MESSAGE: 'globalModalMessage',
    CANCEL: 'globalModalCancel',
    CONFIRM: 'globalModalConfirm',
};

/**
 * 모달 구조 생성 (없으면 생성)
 * @returns {Object} 모달 요소들
 */
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

/**
 * 모달 표시
 * @param {string} title - 모달 제목
 * @param {string} message - 모달 메시지
 * @param {Function} onConfirm - 확인 버튼 클릭 시 콜백
 */
export function showModal(title, message, onConfirm) {
    const { modal, title: titleEl, message: messageEl, cancel, confirm } = ensureModalStructure();

    titleEl.textContent = title;
    messageEl.textContent = message;
    modal.style.display = 'flex';

    if (!document.body.dataset.prevOverflow) {
        document.body.dataset.prevOverflow = document.body.style.overflow || '';
    }
    document.body.style.overflow = 'hidden';

    // 이전 이벤트 리스너 제거를 위해 버튼 교체
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

/**
 * 모달 숨기기
 */
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
