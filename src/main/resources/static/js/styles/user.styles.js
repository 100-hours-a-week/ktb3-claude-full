/**
 * User 페이지 스타일 정의
 * 로그인, 회원가입, 회원정보 수정 등
 */

import { designTokens as dt } from './theme.js';

export const userStyles = {
    /* 인증 컨테이너 */
    '.auth-container': {
        maxWidth: '480px',  // Increased from 400px
        margin: '0 auto',
        background: 'transparent',
        padding: `${dt.spacing[10]} ${dt.spacing[8]}`,  // More spacious
        borderRadius: dt.borderRadius.xl,
    },

    '.auth-title': {
        textAlign: 'center',
        fontSize: dt.fontSize['4xl'],  // Reduced from 5xl
        fontWeight: dt.fontWeight.bold,
        letterSpacing: '-0.02em',  // Adjusted from -0.5px
        marginBottom: dt.spacing[10],  // Increased from 9
        color: dt.colors.text.primary,
        lineHeight: dt.lineHeight.tight,
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

    /* 로그인/회원가입 버튼 */
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

    /* 회원가입 프로필 */
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

    /* 사용자 편집 */
    '.user-edit-container': {
        maxWidth: '680px',  // Increased from 600px
        margin: '0 auto',
    },

    '.user-edit-title': {
        textAlign: 'center',
        fontSize: dt.fontSize['4xl'],
        fontWeight: dt.fontWeight.bold,  // Changed from semibold
        marginBottom: dt.spacing[10],    // Increased from 8
        lineHeight: dt.lineHeight.tight,
        color: dt.colors.text.primary,
    },

    '.user-edit-form': {
        background: dt.colors.white,
        padding: dt.spacing[8],
        borderRadius: dt.borderRadius.xl,  // Changed from lg
        border: `1px solid ${dt.colors.border.default}`,  // Added border
        boxShadow: dt.boxShadow.card,  // Changed from none
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

    /* 비밀번호 수정 */
    '.password-update-container': {
        maxWidth: '560px',  // Increased from 500px
        margin: '0 auto',
    },

    '.password-update-title': {
        textAlign: 'center',
        fontSize: dt.fontSize['4xl'],
        fontWeight: dt.fontWeight.bold,  // Changed from semibold
        marginBottom: dt.spacing[10],    // Increased from 8
        lineHeight: dt.lineHeight.tight,
        color: dt.colors.text.primary,
    },

    '.password-update-form': {
        background: dt.colors.white,
        padding: dt.spacing[8],
        borderRadius: dt.borderRadius.xl,  // Changed from lg
        border: `1px solid ${dt.colors.border.default}`,  // Added border
        boxShadow: dt.boxShadow.card,  // Changed from none
    },

    /* 인증 링크 */
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
        minWidth: '140px',  // Increased from 128px
        padding: `${dt.spacing[3]} ${dt.spacing[6]}`,  // Increased padding
        marginTop: '0',
        backgroundColor: dt.colors.white,
        color: dt.colors.primary,
        border: `1px solid ${dt.colors.primary}`,
        borderRadius: dt.borderRadius.md,
        fontSize: dt.fontSize.base,  // Changed from md
        fontWeight: dt.fontWeight.medium,  // Changed from semibold
        cursor: 'pointer',
        textAlign: 'center',
        textDecoration: 'none',
        transition: `all ${dt.transition.fast}`,  // Faster transition
        lineHeight: '1.5',

        ':hover': {
            backgroundColor: dt.colors.bgTertiary,
            color: dt.colors.primaryDark,
            borderColor: dt.colors.primaryDark,
            transform: 'translateY(-1px)',  // Subtle lift effect
            boxShadow: dt.boxShadow.sm,
        },
    },

    '.signup-links': {
        display: 'flex',
        justifyContent: 'center',
        marginTop: dt.spacing[7],
        width: '100%',
    },

    /* 인증 분할 레이아웃 (로그인 페이지) */
    '.auth-split-container': {
        display: 'flex',
        minHeight: 'calc(100vh - 80px)',
        width: '100%',
    },

    '.auth-split-left': {
        flex: '1',
        background: `linear-gradient(135deg, ${dt.colors.primary} 0%, ${dt.colors.primaryDark} 100%)`,
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        padding: dt.spacing[12],
    },

    '.auth-brand-image': {
        textAlign: 'center',
        color: '#ffffff',
    },

    '.brand-logo': {
        fontSize: '4rem',
        fontWeight: dt.fontWeight.bold,
        marginBottom: dt.spacing[4],
        textShadow: '0 2px 10px rgba(0,0,0,0.2)',
    },

    '.brand-tagline': {
        fontSize: dt.fontSize['2xl'],
        fontWeight: dt.fontWeight.medium,
        opacity: '0.9',
    },

    '.auth-split-right': {
        flex: '1',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        padding: dt.spacing[8],
        backgroundColor: '#ffffff',
    },

    /* 반응형 */
    '@media (max-width: 768px)': {
        '.auth-split-container': {
            flexDirection: 'column',
        },

        '.auth-split-left': {
            minHeight: '200px',
            flex: 'none',
        },

        '.brand-logo': {
            fontSize: '3rem',
        },

        '.brand-tagline': {
            fontSize: dt.fontSize.xl,
        },
    },

    '@media (max-width: 480px)': {
        '.auth-container, .user-edit-form, .password-update-form': {
            padding: `${dt.spacing[6]} ${dt.spacing[5]}`,
        },

        '.auth-title, .user-edit-title, .password-update-title': {
            fontSize: dt.fontSize['3xl'],
        },

        '.auth-split-left': {
            padding: dt.spacing[6],
        },

        '.brand-logo': {
            fontSize: '2.5rem',
        },
    },
};

export default userStyles;
