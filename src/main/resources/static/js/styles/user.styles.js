/**
 * User 페이지 스타일 정의
 * 로그인, 회원가입, 회원정보 수정 등
 */

import { designTokens as dt } from './theme.js';

export const userStyles = {
    /* Auth Container */
    '.auth-container': {
        maxWidth: '400px',
        margin: '0 auto',
        background: 'transparent',
        padding: `48px 40px`,
        borderRadius: dt.borderRadius.xl,
    },

    '.auth-title': {
        textAlign: 'center',
        fontSize: dt.fontSize['5xl'],
        fontWeight: dt.fontWeight.bold,
        letterSpacing: '-0.5px',
        marginBottom: dt.spacing[9],
        color: dt.colors.text.primary,
    },

    '.auth-form .form-group:last-of-type': {
        marginBottom: dt.spacing[6],
    },

    '.auth-actions': {
        marginTop: dt.spacing[2],
    },

    '.auth-links': {
        marginTop: dt.spacing[4],
        display: 'flex',
        justifyContent: 'center',
    },

    /* Login/Signup Buttons */
    '.btn-login-disabled, .btn-signup-disabled': {
        backgroundColor: `${dt.colors.primaryLight} !important`,
        cursor: 'not-allowed',
        opacity: '1',
    },

    '.btn-login-enabled, .btn-signup-enabled': {
        backgroundColor: `${dt.colors.primary} !important`,
        cursor: 'pointer',
        opacity: '1',
    },

    /* Signup Profile */
    '.signup-profile': {
        marginBottom: dt.spacing[8],

        '.profile-image': {
            margin: `0 auto ${dt.spacing[4]}`,
        },

        '.profile-label': {
            textAlign: 'center',
            fontSize: dt.fontSize.base,
            fontWeight: dt.fontWeight.medium,
            marginBottom: dt.spacing[1],
        },

        '.form-helper': {
            textAlign: 'center',
        },
    },

    '.profile-upload': {
        textAlign: 'center',
    },

    '.profile-upload-label': {
        display: 'block',
        fontWeight: dt.fontWeight.medium,
        marginBottom: dt.spacing[2],
    },

    '.profile-upload-image': {
        width: '120px',
        height: '120px',
        borderRadius: dt.borderRadius.full,
        backgroundColor: '#c4c4c4',
        margin: `0 auto ${dt.spacing[4]}`,
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        cursor: 'pointer',
        position: 'relative',
        overflow: 'hidden',

        '::before': {
            content: '"+"',
            fontSize: '48px',
            color: dt.colors.text.secondary,
        },

        'img': {
            position: 'absolute',
            width: '100%',
            height: '100%',
            objectFit: 'cover',
        },
    },

    '.profile-upload-input': {
        display: 'none',
    },

    /* User Edit */
    '.user-edit-container': {
        maxWidth: '600px',
        margin: '0 auto',
    },

    '.user-edit-title': {
        textAlign: 'center',
        fontSize: dt.fontSize['4xl'],
        fontWeight: dt.fontWeight.semibold,
        marginBottom: dt.spacing[8],
    },

    '.user-edit-form': {
        background: dt.colors.white,
        padding: dt.spacing[8],
        borderRadius: dt.borderRadius.lg,
        boxShadow: dt.boxShadow.none,
    },

    '.user-edit-profile': {
        marginBottom: dt.spacing[8],

        '.profile-image': {
            margin: `0 auto ${dt.spacing[4]}`,
        },

        '.profile-label': {
            textAlign: 'center',
            fontWeight: dt.fontWeight.medium,
            marginBottom: dt.spacing[2],
        },
    },

    '.user-email-display': {
        backgroundColor: dt.colors.bgSecondary,
        padding: `${dt.spacing[3]} ${dt.spacing[4]}`,
        borderRadius: dt.borderRadius.sm,
        fontSize: dt.fontSize.base,
        color: dt.colors.text.secondary,
        marginBottom: dt.spacing[6],
    },

    '.user-actions': {
        display: 'flex',
        flexDirection: 'column',
        gap: dt.spacing[3],
        marginTop: dt.spacing[6],

        '.btn-danger': {
            backgroundColor: dt.colors.white,
            color: dt.colors.danger,
            border: `1px solid ${dt.colors.danger}`,

            ':hover': {
                backgroundColor: dt.colors.danger,
                color: dt.colors.white,
            },
        },
    },

    /* Password Update */
    '.password-update-container': {
        maxWidth: '500px',
        margin: '0 auto',
    },

    '.password-update-title': {
        textAlign: 'center',
        fontSize: dt.fontSize['4xl'],
        fontWeight: dt.fontWeight.semibold,
        marginBottom: dt.spacing[8],
    },

    '.password-update-form': {
        background: dt.colors.white,
        padding: dt.spacing[8],
        borderRadius: dt.borderRadius.lg,
        boxShadow: dt.boxShadow.none,
    },

    /* Auth Links */
    '.auth-link': {
        display: 'block',
        textAlign: 'center',
        marginTop: dt.spacing[4],
        color: dt.colors.text.secondary,
        textDecoration: 'none',
        fontSize: dt.fontSize.base,

        ':hover': {
            color: dt.colors.text.primary,
            textDecoration: 'underline',
        },
    },

    '.auth-link-btn': {
        display: 'inline-flex',
        alignItems: 'center',
        justifyContent: 'center',
        minWidth: '128px',
        padding: `${dt.spacing[3]} ${dt.spacing[5]}`,
        marginTop: '0',
        backgroundColor: dt.colors.white,
        color: dt.colors.primary,
        border: `1px solid ${dt.colors.primary}`,
        borderRadius: dt.borderRadius.md,
        fontSize: dt.fontSize.md,
        fontWeight: dt.fontWeight.semibold,
        cursor: 'pointer',
        textAlign: 'center',
        textDecoration: 'none',
        transition: `all ${dt.transition.base}`,

        ':hover': {
            backgroundColor: dt.colors.bgTertiary,
            color: dt.colors.primaryDark,
            borderColor: dt.colors.primaryDark,
        },
    },

    '.signup-links': {
        display: 'flex',
        justifyContent: 'center',
        marginTop: dt.spacing[7],
        width: '100%',
    },

    /* Responsive */
    '@media (max-width: 480px)': {
        '.auth-container, .user-edit-form, .password-update-form': {
            padding: `${dt.spacing[6]} ${dt.spacing[5]}`,
        },

        '.auth-title, .user-edit-title, .password-update-title': {
            fontSize: dt.fontSize['3xl'],
        },
    },
};

export default userStyles;
