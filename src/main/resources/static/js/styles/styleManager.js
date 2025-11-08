/**
 * CSS-in-JS 스타일 관리 시스템
 * JavaScript로 스타일을 완전히 제어합니다.
 */

class StyleManager {
    constructor() {
        this.styleSheets = new Map();
        this.cssCache = new Map();
    }

    /**
     * CSS 객체를 문자열로 변환
     * @param {Object} styles - CSS 스타일 객체
     * @param {string} selector - CSS 선택자
     * @returns {string} CSS 문자열
     */
    objectToCSS(styles, selector = '') {
        if (typeof styles === 'string') {
            return styles;
        }

        let css = '';
        const rules = [];
        const nestedRules = [];

        Object.entries(styles).forEach(([key, value]) => {
            // 중첩된 선택자 (미디어 쿼리, 가상 선택자 등)
            if (typeof value === 'object' && value !== null) {
                const nestedSelector = key.startsWith('@') || key.startsWith(':') || key.startsWith('>')
                    ? key
                    : ` ${key}`;

                if (key.startsWith('@media')) {
                    nestedRules.push(`${key} { ${this.objectToCSS(value, selector)} }`);
                } else if (key.startsWith('@')) {
                    nestedRules.push(`${key} { ${this.objectToCSS(value, '')} }`);
                } else {
                    const fullSelector = selector + nestedSelector;
                    nestedRules.push(this.objectToCSS(value, fullSelector));
                }
            } else {
                // 일반 CSS 속성
                const cssKey = key.replace(/([A-Z])/g, '-$1').toLowerCase();
                rules.push(`  ${cssKey}: ${value};`);
            }
        });

        if (rules.length > 0) {
            css += `${selector} {\n${rules.join('\n')}\n}\n`;
        }

        if (nestedRules.length > 0) {
            css += nestedRules.join('\n') + '\n';
        }

        return css;
    }

    /**
     * 스타일 시트 생성 및 DOM에 삽입
     * @param {string} id - 스타일 시트 ID
     * @param {Object|string} styles - CSS 스타일 객체 또는 문자열
     * @param {Object} options - 옵션 { priority: 'high'|'normal'|'low' }
     */
    injectStyles(id, styles, options = {}) {
        // 이미 주입된 스타일이면 업데이트
        if (this.styleSheets.has(id)) {
            this.updateStyles(id, styles);
            return;
        }

        const cssText = typeof styles === 'string'
            ? styles
            : Object.entries(styles)
                .map(([selector, rules]) => this.objectToCSS(rules, selector))
                .join('\n');

        const styleElement = document.createElement('style');
        styleElement.id = `style-${id}`;
        styleElement.textContent = cssText;

        // 우선순위에 따라 삽입 위치 결정
        const priority = options.priority || 'normal';
        if (priority === 'high') {
            document.head.insertBefore(styleElement, document.head.firstChild);
        } else if (priority === 'low') {
            document.head.appendChild(styleElement);
        } else {
            // normal: 다른 스타일 태그 다음에 삽입
            const styleElements = document.head.querySelectorAll('style');
            if (styleElements.length > 0) {
                const lastStyle = styleElements[styleElements.length - 1];
                lastStyle.after(styleElement);
            } else {
                document.head.appendChild(styleElement);
            }
        }

        this.styleSheets.set(id, styleElement);
        this.cssCache.set(id, cssText);
    }

    /**
     * 기존 스타일 시트 업데이트
     * @param {string} id - 스타일 시트 ID
     * @param {Object|string} styles - 새로운 CSS 스타일
     */
    updateStyles(id, styles) {
        const styleElement = this.styleSheets.get(id);
        if (!styleElement) {
            console.warn(`Style sheet with id "${id}" not found`);
            return;
        }

        const cssText = typeof styles === 'string'
            ? styles
            : Object.entries(styles)
                .map(([selector, rules]) => this.objectToCSS(rules, selector))
                .join('\n');

        styleElement.textContent = cssText;
        this.cssCache.set(id, cssText);
    }

    /**
     * 스타일 시트 제거
     * @param {string} id - 스타일 시트 ID
     */
    removeStyles(id) {
        const styleElement = this.styleSheets.get(id);
        if (styleElement && styleElement.parentNode) {
            styleElement.parentNode.removeChild(styleElement);
        }
        this.styleSheets.delete(id);
        this.cssCache.delete(id);
    }

    /**
     * 모든 스타일 시트 제거
     */
    clearAll() {
        this.styleSheets.forEach((styleElement, id) => {
            if (styleElement.parentNode) {
                styleElement.parentNode.removeChild(styleElement);
            }
        });
        this.styleSheets.clear();
        this.cssCache.clear();
    }

    /**
     * 특정 스타일 시트가 로드되었는지 확인
     * @param {string} id - 스타일 시트 ID
     * @returns {boolean}
     */
    hasStyles(id) {
        return this.styleSheets.has(id);
    }

    /**
     * CSS 변수 설정
     * @param {Object} variables - CSS 변수 객체 { '--color-primary': '#8b7add' }
     * @param {HTMLElement} target - 적용할 대상 요소 (기본값: document.documentElement)
     */
    setCSSVariables(variables, target = document.documentElement) {
        Object.entries(variables).forEach(([key, value]) => {
            const varName = key.startsWith('--') ? key : `--${key}`;
            target.style.setProperty(varName, value);
        });
    }

    /**
     * CSS 변수 가져오기
     * @param {string} name - CSS 변수 이름
     * @param {HTMLElement} target - 대상 요소 (기본값: document.documentElement)
     * @returns {string}
     */
    getCSSVariable(name, target = document.documentElement) {
        const varName = name.startsWith('--') ? name : `--${name}`;
        return getComputedStyle(target).getPropertyValue(varName).trim();
    }

    /**
     * 테마 적용
     * @param {Object} theme - 테마 객체
     */
    applyTheme(theme) {
        if (theme.variables) {
            this.setCSSVariables(theme.variables);
        }
        if (theme.styles) {
            Object.entries(theme.styles).forEach(([id, styles]) => {
                this.injectStyles(id, styles);
            });
        }
    }
}

// 싱글톤 인스턴스 생성
const styleManager = new StyleManager();

// 전역 접근을 위해 window 객체에 추가
if (typeof window !== 'undefined') {
    window.styleManager = styleManager;
}

export default styleManager;
