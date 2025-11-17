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
        minHeight: 'calc(100vh - 80px)',  // Updated for new header height
        padding: `${dt.spacing[12]} ${dt.spacing[6]}`,  // More spacious
    },

    '.auth-card': {
        width: '100%',
        maxWidth: '480px',  // Increased from 420px
        backgroundColor: dt.colors.white,
        border: `1px solid ${dt.colors.border.default}`,
        borderRadius: dt.borderRadius.xl,
        boxShadow: dt.boxShadow.lg,  // Cleaner shadow
    },

    /* Header - javascript.info style clean header */
    '.header': {
        backgroundColor: dt.colors.white,
        borderBottom: `1px solid ${dt.colors.border.default}`,
        height: '80px',      // Reduced from 104px for cleaner look
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        position: 'sticky',
        top: '0',
        zIndex: dt.zIndex.sticky,
        width: '100%',
        boxShadow: '0 1px 0 rgba(0,0,0,0.03)',  // Subtle shadow
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
        gap: dt.spacing[3],
    },

    '.header-login-btn, .header-signup-btn': {
        padding: `${dt.spacing[2]} ${dt.spacing[4]}`,
        fontSize: dt.fontSize.base,
        textDecoration: 'none',
    },

    '.header-create-btn': {
        padding: `${dt.spacing[2]} ${dt.spacing[4]}`,
        fontSize: dt.fontSize.base,
        textDecoration: 'none',
        marginRight: dt.spacing[3],
    },

    '.header-title': {
        fontFamily: dt.fontFamily.heading,
        fontSize: dt.fontSize['3xl'],  // Reduced from 5xl for cleaner look
        fontWeight: dt.fontWeight.semibold,  // Changed from normal
        lineHeight: '100%',
        letterSpacing: '-0.02em',  // Tighter letter spacing
        textAlign: 'center',
        flex: '1',
        color: dt.colors.text.primary,
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

    /* Notification Button */
    '.notification-btn': {
        background: 'none',
        border: 'none',
        cursor: 'pointer',
        padding: dt.spacing[2],
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        color: dt.colors.text.primary,
        borderRadius: dt.borderRadius.sm,
        transition: `all ${dt.transition.fast}`,

        ':hover': {
            backgroundColor: dt.colors.bgSecondary,
        },
    },

    /* User Menu */
    '.user-menu': {
        position: 'relative',
        marginLeft: dt.spacing[2],
    },

    '.user-menu-btn': {
        background: 'none',
        border: 'none',
        cursor: 'pointer',
        padding: `${dt.spacing[2]} ${dt.spacing[3]}`,
        display: 'flex',
        alignItems: 'center',
        gap: dt.spacing[2],
        borderRadius: dt.borderRadius.sm,
        transition: `all ${dt.transition.fast}`,
        color: dt.colors.text.primary,

        ':hover': {
            backgroundColor: dt.colors.bgSecondary,
        },
    },

    '.user-icon': {
        width: '20px',
        height: '20px',
    },

    '.user-name': {
        fontSize: dt.fontSize.base,
        fontWeight: dt.fontWeight.medium,
    },

    '.dropdown-arrow': {
        width: '12px',
        height: '12px',
        transition: `transform ${dt.transition.fast}`,
    },

    '.user-menu-btn[aria-expanded="true"] .dropdown-arrow': {
        transform: 'rotate(180deg)',
    },

    'body.auth-page .header-right': {
        display: 'none',
    },

    '.user-menu-dropdown': {
        position: 'absolute',
        top: 'calc(100% + 8px)',
        right: '0',
        background: dt.colors.white,
        border: `1px solid ${dt.colors.border.default}`,
        borderRadius: dt.borderRadius.lg,
        boxShadow: dt.boxShadow.xl,
        minWidth: '240px',
        overflow: 'hidden',
        zIndex: dt.zIndex.dropdown,
    },

    '.dropdown-header': {
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        padding: dt.spacing[5],
        gap: dt.spacing[2],
    },

    '.dropdown-user-avatar': {
        width: '48px',
        height: '48px',
        borderRadius: dt.borderRadius.full,
        backgroundColor: dt.colors.gray[300],
        marginBottom: dt.spacing[1],
    },

    '.dropdown-user-name': {
        fontSize: dt.fontSize.lg,
        fontWeight: dt.fontWeight.semibold,
        color: dt.colors.text.primary,
    },

    '.dropdown-user-subtitle': {
        fontSize: dt.fontSize.sm,
        color: dt.colors.text.secondary,
    },

    '.dropdown-divider': {
        height: '1px',
        backgroundColor: dt.colors.border.default,
        margin: `${dt.spacing[2]} 0`,
    },

    '.user-menu-dropdown .menu-item': {
        display: 'block',
        padding: `${dt.spacing[3]} ${dt.spacing[5]}`,
        textDecoration: 'none',
        color: dt.colors.text.primary,
        fontSize: dt.fontSize.base,
        transition: `background-color ${dt.transition.fast}`,

        ':hover': {
            backgroundColor: dt.colors.bgSecondary,
        },
    },

    '.menu-item-logout': {
        color: dt.colors.danger,

        ':hover': {
            backgroundColor: dt.colors.dangerLight + '20',
        },
    },

    /* Main Content - javascript.info style spacious layout */
    '.main-content': {
        maxWidth: '1080px',  // Increased from 960px
        margin: '0 auto',
        padding: `${dt.spacing[12]} ${dt.spacing[6]}`,  // More spacious
        minHeight: 'calc(100vh - 80px)',  // Updated for new header height
    },

    /* Buttons - javascript.info style clean buttons */
    '.btn': {
        padding: `${dt.spacing[3]} ${dt.spacing[5]}`,
        border: 'none',
        borderRadius: dt.borderRadius.md,  // Changed from sm
        fontSize: dt.fontSize.base,        // Changed from lg
        fontWeight: dt.fontWeight.medium,
        cursor: 'pointer',
        transition: `all ${dt.transition.fast}`,  // Faster transition
        textDecoration: 'none',
        display: 'inline-flex',  // Changed from inline-block
        alignItems: 'center',
        justifyContent: 'center',
        textAlign: 'center',
        boxShadow: dt.boxShadow.none,
        lineHeight: '1.5',
    },

    '.btn-primary': {
        backgroundColor: dt.colors.primary,
        color: dt.colors.white,
        border: 'none',
        boxShadow: dt.boxShadow.sm,

        ':hover': {
            backgroundColor: dt.colors.primaryDark,
            boxShadow: dt.boxShadow.md,
        },

        ':disabled': {
            backgroundColor: dt.colors.primaryLight,
            color: dt.colors.white,
            cursor: 'not-allowed',
            boxShadow: dt.boxShadow.none,
            opacity: '0.6',
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
        border: `1px solid ${dt.colors.primary}`,

        ':hover': {
            backgroundColor: dt.colors.bgTertiary,
            borderColor: dt.colors.primaryDark,
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
        padding: `${dt.spacing[3]} ${dt.spacing[4]}`,  // More consistent spacing
        border: `1px solid ${dt.colors.border.default}`,  // Lighter border
        borderRadius: dt.borderRadius.md,  // Changed from lg
        fontSize: dt.fontSize.base,
        transition: `all ${dt.transition.fast}`,  // Changed from border-color only
        backgroundColor: dt.colors.white,
        lineHeight: dt.lineHeight.base,

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
        minHeight: '140px',  // Increased from 120px
        resize: 'vertical',
        fontFamily: 'inherit',
        lineHeight: dt.lineHeight.relaxed,  // Better readability
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
        borderRadius: dt.borderRadius.md,
        background: dt.colors.white,
        cursor: 'pointer',
        fontSize: dt.fontSize.base,
        transition: `all ${dt.transition.base}`,
        color: dt.colors.text.primary,

        ':hover': {
            backgroundColor: dt.colors.bgTertiary,
            borderColor: dt.colors.primary,
            color: dt.colors.primary,
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
            backgroundColor: dt.colors.primary,
            color: dt.colors.white,

            ':hover': {
                backgroundColor: dt.colors.primaryDark,
            },
        },
    },

    /* Toast Message */
    '.toast': {
        position: 'fixed',
        top: '100px',
        left: '50%',
        transform: 'translateX(-50%)',
        backgroundColor: dt.colors.primary,
        color: dt.colors.white,
        padding: `${dt.spacing[4]} ${dt.spacing[8]}`,
        borderRadius: dt.borderRadius.md,
        boxShadow: dt.boxShadow.lg,
        fontSize: dt.fontSize.lg,
        fontWeight: dt.fontWeight.medium,
        zIndex: dt.zIndex.modal + 1,
        opacity: '0',
        transition: 'opacity 0.3s ease-in-out',
        pointerEvents: 'none',
    },

    '.toast.show': {
        opacity: '1',
    },

    /* Links */
    '.link': {
        color: dt.colors.text.link,
        textDecoration: 'none',
        fontSize: dt.fontSize.base,

        ':hover': {
            textDecoration: 'underline',
            color: dt.colors.primaryDark,
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
