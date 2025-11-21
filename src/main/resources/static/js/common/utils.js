// 공통 유틸리티 함수

/**
 * 디바운스 함수 - 연속 호출 시 마지막 호출만 실행
 * @param {Function} func - 디바운스를 적용할 함수
 * @param {number} delay - 지연 시간 (밀리초)
 * @returns {Function} 디바운스가 적용된 함수
 */
export function debounce(func, delay) {
    let timeoutId;
    return function (...args) {
        clearTimeout(timeoutId);
        timeoutId = setTimeout(() => func.apply(this, args), delay);
    };
}

/**
 * 스로틀 함수 - 지정된 시간 간격당 최대 한 번만 실행되도록 제한
 * @param {Function} func - 스로틀을 적용할 함수
 * @param {number} delay - 지연 시간 (밀리초)
 * @returns {Function} 스로틀이 적용된 함수
 */
export function throttle(func, delay) {
    let lastCall = 0;
    let timeoutId = null;

    return function throttled(...args) {
        const now = Date.now();
        const timeSinceLastCall = now - lastCall;

        if (timeSinceLastCall >= delay) {
            lastCall = now;
            func.apply(this, args);
        } else {
            clearTimeout(timeoutId);
            timeoutId = setTimeout(() => {
                lastCall = Date.now();
                func.apply(this, args);
            }, delay - timeSinceLastCall);
        }
    };
}
