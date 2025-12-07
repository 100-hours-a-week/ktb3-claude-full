// 토스트 메시지 관리자

/**
 * 토스트 메시지 표시
 * @param {string} message - 표시할 메시지
 * @param {number} duration - 표시 시간 (밀리초, 기본값: 2000)
 */
export function showToast(message, duration = 2000) {
    let toast = document.querySelector('.toast');

    if (!toast) {
        toast = document.createElement('div');
        toast.className = 'toast';
        document.body.appendChild(toast);
    }

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
