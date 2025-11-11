/**
 * 디자인 토큰 및 테마 정의
 * 모든 색상, 간격, 타이포그래피 등을 중앙에서 관리합니다.
 */

import styleManager from './styleManager.js';

export const designTokens = {
    // Colors
    colors: {
        primary: '#7f6aee',
        primaryLight: '#aca0eb',
        primaryDark: '#6a56e8',
        primaryGradientStart: '#7f6aee',
        primaryGradientEnd: '#aca0eb',

        secondary: '#2c2c2c',
        secondaryDark: '#1a1a1a',

        danger: '#ff4444',
        dangerDark: '#cc0000',
        dangerLight: '#ff5a5a',
        dangerBorder: '#ffb5b5',

        success: '#00cc66',
        warning: '#ffaa00',
        info: '#3399ff',

        // Neutral colors
        white: '#ffffff',
        black: '#000000',

        gray: {
            50: '#f9fafb',
            100: '#f5f5f5',
            200: '#f0f0f0',
            300: '#e8e8f0',
            400: '#d8d5e9',
            500: '#999',
            600: '#666',
            700: '#555',
            800: '#333',
            900: '#1a1a1a',
        },

        // Background colors
        bgPrimary: '#ffffff',
        bgSecondary: '#f5f5f5',
        bgTertiary: '#f4f2ff',
        bgPurpleLight: '#f4f2ff',
        bgGrayLight: '#d9d9d9',

        // Border colors
        border: {
            light: '#ecebf5',
            default: '#ddd',
            dark: '#d8d5e9',
            purple: '#cfc4ff',
            purple2: '#e0dafc',
        },

        // Text colors
        text: {
            primary: '#333',
            secondary: '#666',
            tertiary: '#999',
            inverse: '#ffffff',
            muted: '#8b88a8',
            dark: '#302c63',
            darkSecondary: '#6f6d83',
            link: '#8b7add',
            error: '#ff4444',
        },

        // Shadow colors
        shadow: {
            sm: 'rgba(0, 0, 0, 0.08)',
            md: 'rgba(0, 0, 0, 0.12)',
            lg: 'rgba(0, 0, 0, 0.15)',
            purple: 'rgba(172, 160, 235, 0.35)',
            card: 'rgba(25, 20, 62, 0.08)',
            cardLight: 'rgba(25, 20, 62, 0.04)',
            cardMedium: 'rgba(25, 20, 62, 0.05)',
            button: 'rgba(127, 106, 238, 0.28)',
            buttonHover: 'rgba(107, 87, 229, 0.32)',
        },
    },

    // Spacing
    spacing: {
        0: '0',
        1: '4px',
        2: '8px',
        3: '12px',
        4: '16px',
        5: '20px',
        6: '24px',
        7: '28px',
        8: '32px',
        9: '36px',
        10: '40px',
        12: '48px',
        16: '64px',
    },

    // Font family
    fontFamily: {
        base: "-apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', 'Helvetica', 'Arial', 'Malgun Gothic', sans-serif",
        heading: "'Sandoll GoGoRound', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif",
    },

    // Font sizes
    fontSize: {
        xs: '12px',
        sm: '13px',
        base: '14px',
        md: '15px',
        lg: '16px',
        xl: '18px',
        '2xl': '20px',
        '3xl': '24px',
        '4xl': '28px',
        '5xl': '32px',
    },

    // Font weights
    fontWeight: {
        normal: '400',
        medium: '500',
        semibold: '600',
        bold: '700',
    },

    // Line heights
    lineHeight: {
        tight: '1.5',
        base: '1.6',
        relaxed: '1.7',
        loose: '1.8',
    },

    // Border radius
    borderRadius: {
        none: '0',
        sm: '8px',
        md: '10px',
        lg: '12px',
        xl: '16px',
        '2xl': '18px',
        full: '9999px',
    },

    // Shadows
    boxShadow: {
        none: 'none',
        sm: '0 1px 4px rgba(0, 0, 0, 0.08)',
        md: '0 2px 8px rgba(0, 0, 0, 0.12)',
        lg: '0 4px 12px rgba(0, 0, 0, 0.15)',
        xl: '0 10px 24px rgba(127, 106, 238, 0.28)',
        '2xl': '0 18px 36px rgba(107, 87, 229, 0.32)',
        card: '0 20px 40px rgba(25, 20, 62, 0.08)',
        cardLight: '0 20px 40px rgba(25, 20, 62, 0.04)',
        cardMedium: '0 12px 32px rgba(25, 20, 62, 0.05)',
        purple: '0 10px 24px rgba(172, 160, 235, 0.35)',
        input: '0 0 0 3px rgba(127, 106, 238, 0.12)',
        inset: 'inset 0 1px 0 rgba(255, 255, 255, 0.2)',
    },

    // Transitions
    transition: {
        fast: '0.2s',
        base: '0.3s',
        slow: '0.5s',
    },

    // Z-index
    zIndex: {
        dropdown: '10',
        sticky: '100',
        fixed: '500',
        modal: '1000',
        tooltip: '1500',
    },

    // Breakpoints
    breakpoints: {
        sm: '480px',
        md: '768px',
        lg: '960px',
        xl: '1920px',
    },
};

/**
 * 기본 라이트 테마
 */
export const lightTheme = {
    name: 'light',
    variables: {
        '--color-primary': designTokens.colors.primary,
        '--color-primary-light': designTokens.colors.primaryLight,
        '--color-primary-dark': designTokens.colors.primaryDark,
        '--color-secondary': designTokens.colors.secondary,
        '--color-danger': designTokens.colors.danger,

        '--color-bg-primary': designTokens.colors.bgPrimary,
        '--color-bg-secondary': designTokens.colors.bgSecondary,
        '--color-bg-tertiary': designTokens.colors.bgTertiary,

        '--color-text-primary': designTokens.colors.text.primary,
        '--color-text-secondary': designTokens.colors.text.secondary,
        '--color-text-tertiary': designTokens.colors.text.tertiary,

        '--border-color': designTokens.colors.border.default,
        '--border-radius': designTokens.borderRadius.sm,

        '--font-family-base': designTokens.fontFamily.base,
        '--font-family-heading': designTokens.fontFamily.heading,
    },
};

/**
 * 다크 테마 (선택 사항)
 */
export const darkTheme = {
    name: 'dark',
    variables: {
        '--color-primary': designTokens.colors.primary,
        '--color-primary-light': designTokens.colors.primaryLight,
        '--color-primary-dark': designTokens.colors.primaryDark,
        '--color-secondary': designTokens.colors.white,
        '--color-danger': designTokens.colors.danger,

        '--color-bg-primary': designTokens.colors.gray[900],
        '--color-bg-secondary': designTokens.colors.gray[800],
        '--color-bg-tertiary': designTokens.colors.gray[700],

        '--color-text-primary': designTokens.colors.white,
        '--color-text-secondary': designTokens.colors.gray[300],
        '--color-text-tertiary': designTokens.colors.gray[400],

        '--border-color': designTokens.colors.gray[700],
        '--border-radius': designTokens.borderRadius.sm,

        '--font-family-base': designTokens.fontFamily.base,
        '--font-family-heading': designTokens.fontFamily.heading,
    },
};

/**
 * 현재 활성 테마 관리
 */
const themeManager = (() => {
    let currentTheme = lightTheme;

    return {
        getCurrentTheme: () => currentTheme,
        setTheme: (theme) => {
            currentTheme = theme;
            styleManager.setCSSVariables(theme.variables);
        }
    };
})();

/**
 * 현재 활성 테마 (getter)
 */
export const getCurrentTheme = themeManager.getCurrentTheme;

/**
 * 테마 변경 함수
 */
export const setTheme = themeManager.setTheme;

/**
 * 현재 테마 객체 접근용 프록시
 * For backwards compatibility with code that accesses currentTheme directly
 */
export const currentTheme = {
    get name() { return themeManager.getCurrentTheme().name; },
    get variables() { return themeManager.getCurrentTheme().variables; }
};

/**
 * CSS 헬퍼 함수들
 */
export const css = {
    /**
     * 색상 값 가져오기
     */
    color: (path) => {
        const keys = path.split('.');
        let value = designTokens.colors;
        for (const key of keys) {
            value = value[key];
            if (value === undefined) return path;
        }
        return value;
    },

    /**
     * 간격 값 가져오기
     */
    spacing: (key) => designTokens.spacing[key] || key,

    /**
     * 폰트 크기 가져오기
     */
    fontSize: (key) => designTokens.fontSize[key] || key,

    /**
     * 그림자 값 가져오기
     */
    boxShadow: (key) => designTokens.boxShadow[key] || key,

    /**
     * border-radius 값 가져오기
     */
    borderRadius: (key) => designTokens.borderRadius[key] || key,

    /**
     * 미디어 쿼리 생성
     */
    media: (breakpoint) => `@media (max-width: ${designTokens.breakpoints[breakpoint]})`,
};

export default {
    designTokens,
    lightTheme,
    darkTheme,
    currentTheme,
    getCurrentTheme,
    setTheme,
    css,
};
