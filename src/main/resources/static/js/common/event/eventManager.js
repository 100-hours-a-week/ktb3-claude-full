// 이벤트 리스너 관리자 - 페이지 전환 시 정리용

class EventListenerManager {
    constructor() {
        this.listeners = [];
    }

    /**
     * 이벤트 리스너 등록 및 추적
     * @param {Element} element - 이벤트를 등록할 요소
     * @param {string} eventType - 이벤트 타입
     * @param {Function} handler - 이벤트 핸들러
     * @param {Object} options - addEventListener 옵션
     */
    add(element, eventType, handler, options) {
        if (!element) return;
        element.addEventListener(eventType, handler, options);
        this.listeners.push({ element, eventType, handler, options });
    }

    /**
     * 등록된 모든 이벤트 리스너 제거
     */
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
