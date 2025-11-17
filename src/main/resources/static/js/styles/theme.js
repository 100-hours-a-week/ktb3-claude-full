/**
 * 디자인 토큰 및 테마 정의
 * 모든 색상, 간격, 타이포그래피 등을 중앙에서 관리합니다.
 */

import styleManager from './styleManager.js';

export const designTokens = {
    // Colors - Spring-inspired Green Theme
    colors: {
        primary: '#6db33f',        // Spring Green
        primaryLight: '#8cc63f',   // Light Spring Green
        primaryDark: '#5ca632',    // Dark Spring Green
        primaryGradientStart: '#6db33f',
        primaryGradientEnd: '#8cc63f',

        secondary: '#2c2c2c',
        secondaryDark: '#1a1a1a',

        danger: '#dc3545',         // Bootstrap-style red
        dangerDark: '#bd2130',
        dangerLight: '#e4606d',
        dangerBorder: '#f5c6cb',

        success: '#6db33f',        // Same as primary
        warning: '#ffc107',        // Warmer yellow
        info: '#17a2b8',           // Teal info color

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
        bgSecondary: '#f8faf8',      // Very light green tint
        bgTertiary: '#f0f8f0',       // Light green background
        bgGreenLight: '#f0f8f0',
        bgGrayLight: '#e9ecef',

        // Border colors
        border: {
            light: '#e9f5e4',        // Light green border
            default: '#dee2e6',      // Clean neutral border
            dark: '#c3d9b8',         // Green-tinted border
            green: '#b8d9a8',        // Green border
            green2: '#d4eac8',       // Lighter green border
        },

        // Text colors
        text: {
            primary: '#212529',       // Darker, more readable
            secondary: '#6c757d',     // Bootstrap gray
            tertiary: '#adb5bd',
            inverse: '#ffffff',
            muted: '#869e94',         // Green-tinted muted
            dark: '#2d3e34',          // Dark green
            darkSecondary: '#5a6e5f',
            link: '#5ca632',          // Dark green for links
            error: '#dc3545',
        },

        // Shadow colors
        shadow: {
            sm: 'rgba(0, 0, 0, 0.075)',
            md: 'rgba(0, 0, 0, 0.1)',
            lg: 'rgba(0, 0, 0, 0.125)',
            green: 'rgba(109, 179, 63, 0.25)',      // Green shadow
            card: 'rgba(0, 0, 0, 0.08)',
            cardLight: 'rgba(0, 0, 0, 0.04)',
            cardMedium: 'rgba(0, 0, 0, 0.06)',
            button: 'rgba(109, 179, 63, 0.25)',     // Green button shadow
            buttonHover: 'rgba(92, 166, 50, 0.35)', // Darker green hover
        },
    },

    // Spacing - Increased for javascript.info style spacious layout
    spacing: {
        0: '0',
        1: '4px',
        2: '8px',
        3: '12px',
        4: '16px',
        5: '24px',     // Increased from 20px
        6: '32px',     // Increased from 24px
        7: '36px',     // Increased from 28px
        8: '40px',     // Increased from 32px
        9: '48px',     // Increased from 36px
        10: '56px',    // Increased from 40px
        12: '64px',    // Increased from 48px
        16: '80px',    // Increased from 64px
    },

    // Font family
    fontFamily: {
        base: "-apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', 'Helvetica', 'Arial', 'Malgun Gothic', sans-serif",
        heading: "'Sandoll GoGoRound', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif",
    },

    // Font sizes - Adjusted for better readability (javascript.info style)
    fontSize: {
        xs: '12px',
        sm: '14px',    // Increased from 13px
        base: '16px',  // Increased from 14px
        md: '17px',    // Increased from 15px
        lg: '18px',    // Increased from 16px
        xl: '20px',    // Increased from 18px
        '2xl': '24px', // Increased from 20px
        '3xl': '28px', // Increased from 24px
        '4xl': '32px', // Increased from 28px
        '5xl': '36px', // Increased from 32px
    },

    // Font weights
    fontWeight: {
        normal: '400',
        medium: '500',
        semibold: '600',
        bold: '700',
    },

    // Line heights - Increased for javascript.info style readability
    lineHeight: {
        tight: '1.5',
        base: '1.7',   // Increased from 1.6
        relaxed: '1.8', // Increased from 1.7
        loose: '2.0',   // Increased from 1.8
    },

    // Border radius - Softer, more modern (javascript.info style)
    borderRadius: {
        none: '0',
        sm: '6px',     // Increased from 4px
        md: '8px',     // Increased from 6px
        lg: '12px',    // Increased from 8px
        xl: '16px',    // Increased from 12px
        '2xl': '20px', // Increased from 16px
        full: '9999px',
    },

    // Shadows - Subtle and clean (javascript.info style)
    boxShadow: {
        none: 'none',
        sm: '0 1px 3px rgba(0, 0, 0, 0.05)',           // Softer
        md: '0 2px 8px rgba(0, 0, 0, 0.08)',           // Softer
        lg: '0 4px 12px rgba(0, 0, 0, 0.1)',           // Softer
        xl: '0 8px 20px rgba(0, 0, 0, 0.12)',          // Softer
        '2xl': '0 12px 28px rgba(0, 0, 0, 0.15)',      // Softer
        card: '0 2px 4px rgba(0, 0, 0, 0.04)',         // Very subtle
        cardLight: '0 1px 2px rgba(0, 0, 0, 0.03)',    // Very subtle
        cardMedium: '0 2px 6px rgba(0, 0, 0, 0.05)',   // Subtle
        green: '0 4px 12px rgba(109, 179, 63, 0.15)',  // Softer green
        input: '0 0 0 3px rgba(109, 179, 63, 0.1)',    // Softer input focus
        inset: 'inset 0 1px 0 rgba(255, 255, 255, 0.15)',
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
