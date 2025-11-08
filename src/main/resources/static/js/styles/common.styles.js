/**
 * Common 스타일 정의
 * 기존 common.css를 JavaScript로 변환
 */

import { designTokens as dt, css } from './theme.js';

export const commonStyles = {
    /* Reset and Base Styles */
    '*': {
        margin: '0',
        padding: '0',
        boxSizing: 'border-box',
    },

    'body': {
        fontFamily: dt.fontFamily.base,
        backgroundColor: dt.colors.bgSecondary,
        color: dt.colors.text.primary,
        lineHeight: dt.lineHeight.base,
    },

    '.auth-main': {
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        minHeight: 'calc(100vh - 104px)',
        padding: `${dt.spacing[16]} ${dt.spacing[5]}`,
    },

    '.auth-card': {
        width: '100%',
        maxWidth: '420px',
        backgroundColor: dt.colors.white,
        border: `1px solid ${dt.colors.gray[300]}`,
        borderRadius: dt.borderRadius.xl,
        boxShadow: '0 18px 48px rgba(29, 25, 82, 0.08)',
    },

    /* Header */
    '.header': {
        backgroundColor: dt.colors.white,
        borderBottom: `1px solid ${dt.colors.border.default}`,
        height: '104px',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        position: 'sticky',
        top: '0',
        zIndex: dt.zIndex.sticky,
        width: '100%',
    },

    '.header-container': {
        maxWidth: '1920px',
        width: '100%',
        height: '100%',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        position: 'relative',
        padding: `0 ${dt.spacing[8]}`,
    },

    '.header-left': {
        display: 'flex',
        alignItems: 'center',
        gap: dt.spacing[2],
    },

    '.header-right': {
        display: 'flex',
        alignItems: 'center',
        gap: dt.spacing[4],
    },

    '.header-title': {
        fontFamily: dt.fontFamily.heading,
        fontSize: dt.fontSize['5xl'],
        fontWeight: dt.fontWeight.normal,
        lineHeight: '100%',
        letterSpacing: '0',
        textAlign: 'center',
        flex: '1',
    },

    '.back-btn': {
        background: 'none',
        border: 'none',
        fontSize: dt.fontSize['3xl'],
        cursor: 'pointer',
        color: dt.colors.text.primary,
        padding: dt.spacing[1],

        ':hover': {
            color: dt.colors.text.secondary,
        },
    },

    /* User Menu */
    '.user-menu': {
        position: 'relative',
    },

    '.user-menu-btn': {
        background: 'none',
        border: 'none',
        cursor: 'pointer',
        padding: '0',
        display: 'flex',
        alignItems: 'center',
        width: '36px',
        height: '36px',

        'svg': {
            width: '36px',
            height: '36px',
        },

        ':hover': {
            opacity: '0.7',
        },
    },

    'body.auth-page .header-right': {
        display: 'none',
    },

    '.user-menu-dropdown': {
        position: 'absolute',
        top: '100%',
        right: '0',
        marginTop: dt.spacing[2],
        background: dt.colors.white,
        border: `1px solid ${dt.colors.border.default}`,
        borderRadius: dt.borderRadius.sm,
        boxShadow: dt.boxShadow.lg,
        minWidth: '160px',
        overflow: 'hidden',
    },

    '.user-menu-dropdown .menu-item': {
        display: 'block',
        padding: `${dt.spacing[3]} ${dt.spacing[4]}`,
        textDecoration: 'none',
        color: dt.colors.text.primary,
        transition: `background-color ${dt.transition.fast}`,

        ':hover': {
            backgroundColor: dt.colors.bgSecondary,
        },
    },

    /* Main Content */
    '.main-content': {
        maxWidth: '960px',
        margin: '0 auto',
        padding: `${dt.spacing[16]} ${dt.spacing[5]}`,
        minHeight: 'calc(100vh - 65px)',
    },

    /* Buttons */
    '.btn': {
        padding: `${dt.spacing[3]} ${dt.spacing[6]}`,
        border: 'none',
        borderRadius: dt.borderRadius.sm,
        fontSize: dt.fontSize.lg,
        fontWeight: dt.fontWeight.medium,
        cursor: 'pointer',
        transition: `all ${dt.transition.base}`,
        textDecoration: 'none',
        display: 'inline-block',
        textAlign: 'center',
        boxShadow: dt.boxShadow.none,
    },

    '.btn-primary': {
        background: `linear-gradient(90deg, ${dt.colors.primaryGradientStart} 0%, ${dt.colors.primaryGradientEnd} 100%)`,
        color: dt.colors.white,
        border: 'none',
        boxShadow: dt.boxShadow.xl,

        ':hover': {
            background: `linear-gradient(90deg, ${dt.colors.primaryDark} 0%, #9d8cf0 100%)`,
            boxShadow: dt.boxShadow['2xl'],
        },

        ':disabled': {
            background: dt.colors.primaryLight,
            color: dt.colors.white,
            cursor: 'not-allowed',
            boxShadow: dt.boxShadow.none,
        },
    },

    '.btn[disabled]': {
        background: dt.colors.primaryLight,
        color: dt.colors.white,
        cursor: 'not-allowed',
        boxShadow: dt.boxShadow.none,
    },

    '.btn-secondary': {
        backgroundColor: dt.colors.secondary,
        color: dt.colors.white,
        border: 'none',

        ':hover': {
            backgroundColor: dt.colors.secondaryDark,
        },
    },

    '.btn-danger': {
        backgroundColor: dt.colors.danger,
        color: dt.colors.white,
        border: 'none',

        ':hover': {
            backgroundColor: dt.colors.dangerDark,
        },
    },

    '.btn-full': {
        width: '100%',
    },

    '.btn-outline': {
        backgroundColor: dt.colors.white,
        color: dt.colors.primary,
        border: `1px solid ${dt.colors.border.purple}`,

        ':hover': {
            backgroundColor: dt.colors.bgTertiary,
        },
    },

    /* Forms */
    '.form-group': {
        marginBottom: dt.spacing[5],
    },

    '.form-label': {
        display: 'block',
        marginBottom: dt.spacing[2],
        fontWeight: dt.fontWeight.medium,
        fontSize: dt.fontSize.base,

        '&.required::after': {
            content: '"*"',
            color: dt.colors.danger,
            marginLeft: dt.spacing[1],
        },
    },

    '.form-input, .form-textarea': {
        width: '100%',
        padding: `14px 18px`,
        border: `1px solid ${dt.colors.border.dark}`,
        borderRadius: dt.borderRadius.lg,
        fontSize: dt.fontSize.base,
        transition: `border-color ${dt.transition.base}`,
        backgroundColor: dt.colors.white,

        ':focus': {
            outline: 'none',
            borderColor: dt.colors.primary,
            boxShadow: dt.boxShadow.input,
        },

        '&.error': {
            borderColor: dt.colors.danger,
        },
    },

    '.form-textarea': {
        minHeight: '120px',
        resize: 'vertical',
        fontFamily: 'inherit',
    },

    '.form-helper': {
        display: 'block',
        marginTop: dt.spacing[1],
        fontSize: dt.fontSize.xs,
        color: dt.colors.danger,
    },

    /* File Upload */
    '.file-upload': {
        display: 'flex',
        alignItems: 'center',
        gap: dt.spacing[3],
    },

    '.file-upload-btn': {
        padding: `10px 18px`,
        border: `1px solid ${dt.colors.border.dark}`,
        borderRadius: dt.borderRadius.lg,
        background: dt.colors.bgTertiary,
        cursor: 'pointer',
        fontSize: dt.fontSize.base,
        transition: `background-color ${dt.transition.base}`,
        color: '#5a55a1',

        ':hover': {
            backgroundColor: '#e7e2ff',
        },
    },

    '.file-upload-text': {
        fontSize: dt.fontSize.base,
        color: dt.colors.text.secondary,
    },

    /* Profile Image */
    '.profile-image-container': {
        display: 'flex',
        justifyContent: 'center',
        marginBottom: dt.spacing[6],
    },

    '.profile-image': {
        width: '120px',
        height: '120px',
        borderRadius: dt.borderRadius.full,
        backgroundColor: '#c4c4c4',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        cursor: 'pointer',
        position: 'relative',
        overflow: 'hidden',

        'img': {
            width: '100%',
            height: '100%',
            objectFit: 'cover',
        },
    },

    '.profile-image-placeholder': {
        fontSize: '48px',
        color: dt.colors.text.secondary,
    },

    '.profile-image-change': {
        position: 'absolute',
        bottom: '0',
        left: '0',
        right: '0',
        backgroundColor: 'rgba(0, 0, 0, 0.5)',
        color: dt.colors.white,
        padding: dt.spacing[2],
        textAlign: 'center',
        fontSize: dt.fontSize.xs,
    },

    /* Modal */
    '.modal': {
        position: 'fixed',
        top: '0',
        left: '0',
        right: '0',
        bottom: '0',
        backgroundColor: 'rgba(0, 0, 0, 0.5)',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        zIndex: dt.zIndex.modal,
    },

    '.modal-content': {
        background: dt.colors.white,
        borderRadius: dt.borderRadius.xl,
        padding: dt.spacing[8],
        maxWidth: '400px',
        width: '90%',
        textAlign: 'center',

        'h3': {
            fontSize: dt.fontSize['2xl'],
            marginBottom: dt.spacing[3],
            fontWeight: dt.fontWeight.semibold,
        },

        'p': {
            fontSize: dt.fontSize.base,
            color: dt.colors.text.secondary,
            marginBottom: dt.spacing[6],
            lineHeight: dt.lineHeight.tight,
        },
    },

    '.modal-buttons': {
        display: 'flex',
        gap: dt.spacing[3],

        '.btn': {
            flex: '1',
        },

        '.btn-secondary': {
            backgroundColor: dt.colors.secondary,
            color: dt.colors.white,

            ':hover': {
                backgroundColor: dt.colors.secondaryDark,
            },
        },

        '.btn-primary': {
            backgroundColor: '#8b7add',
            color: dt.colors.white,

            ':hover': {
                backgroundColor: '#7a68cc',
            },
        },
    },

    /* Links */
    '.link': {
        color: '#8b7add',
        textDecoration: 'none',
        fontSize: dt.fontSize.base,

        ':hover': {
            textDecoration: 'underline',
        },
    },

    '.text-center': {
        textAlign: 'center',
    },

    /* Utility Classes */
    '.mt-1': { marginTop: dt.spacing[2] },
    '.mt-2': { marginTop: dt.spacing[4] },
    '.mt-3': { marginTop: dt.spacing[6] },
    '.mt-4': { marginTop: dt.spacing[8] },

    '.mb-1': { marginBottom: dt.spacing[2] },
    '.mb-2': { marginBottom: dt.spacing[4] },
    '.mb-3': { marginBottom: dt.spacing[6] },
    '.mb-4': { marginBottom: dt.spacing[8] },

    '.hidden': {
        display: 'none !important',
    },
};

export default commonStyles;
