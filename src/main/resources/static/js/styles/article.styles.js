/**
 * Article 페이지 스타일 정의
 * 게시글 목록, 상세, 작성/수정 등
 */

import { designTokens as dt } from './theme.js';

export const articleStyles = {
    /* Article List */
    '.article-detail-main': {
        display: 'flex',
        justifyContent: 'center',
    },

    '.article-list-header': {
        textAlign: 'center',
        marginBottom: dt.spacing[8],
    },

    '.article-list-intro': {
        fontSize: dt.fontSize.xl,
        color: dt.colors.text.secondary,
        marginBottom: dt.spacing[1],

        'strong': {
            color: dt.colors.text.primary,
            fontWeight: dt.fontWeight.semibold,
        },
    },

    '.article-create-btn': {
        display: 'inline-block',
        marginTop: dt.spacing[4],
    },

    '.article-list': {
        display: 'flex',
        flexDirection: 'column',
        gap: dt.spacing[4],
    },

    /* Article Card */
    '.article-card': {
        background: dt.colors.white,
        borderRadius: dt.borderRadius.lg,
        padding: dt.spacing[5],
        boxShadow: dt.boxShadow.sm,
        transition: `transform ${dt.transition.fast}, box-shadow ${dt.transition.fast}`,
        cursor: 'pointer',
        textDecoration: 'none',
        color: 'inherit',
        display: 'block',
        border: `1px solid ${dt.colors.gray[200]}`,

        ':hover': {
            transform: 'translateY(-2px)',
            boxShadow: dt.boxShadow.md,
        },
    },

    '.article-card-header': {
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'flex-start',
        marginBottom: dt.spacing[3],
    },

    '.article-card-title': {
        fontSize: dt.fontSize['2xl'],
        fontWeight: dt.fontWeight.semibold,
        marginBottom: dt.spacing[2],
        color: dt.colors.text.primary,
    },

    '.article-card-date': {
        fontSize: dt.fontSize.base,
        color: dt.colors.text.tertiary,
        whiteSpace: 'nowrap',
    },

    '.article-card-meta': {
        display: 'flex',
        gap: dt.spacing[4],
        fontSize: dt.fontSize.base,
        color: dt.colors.text.secondary,

        'span': {
            display: 'flex',
            alignItems: 'center',
            gap: dt.spacing[1],
        },
    },

    '.article-card-author': {
        display: 'flex',
        alignItems: 'center',
        gap: dt.spacing[2],
        marginTop: dt.spacing[3],
        paddingTop: dt.spacing[3],
        borderTop: `1px solid ${dt.colors.gray[200]}`,
    },

    '.article-card-author-avatar': {
        width: '32px',
        height: '32px',
        borderRadius: dt.borderRadius.full,
        backgroundColor: '#c4c4c4',
    },

    '.article-card-author-name': {
        fontSize: dt.fontSize.base,
        fontWeight: dt.fontWeight.medium,
        color: dt.colors.text.secondary,
    },

    /* Article Detail */
    '.article-detail-container': {
        width: '100%',
        maxWidth: '720px',
        margin: '0 auto',
    },

    '.article-detail-header': {
        background: dt.colors.white,
        padding: `${dt.spacing[8]} ${dt.spacing[9]}`,
        borderRadius: dt.borderRadius['2xl'],
        border: `1px solid ${dt.colors.border.light}`,
        boxShadow: dt.boxShadow.card,
        marginBottom: dt.spacing[7],
    },

    '.article-detail-title': {
        fontSize: dt.fontSize['4xl'],
        fontWeight: dt.fontWeight.semibold,
        marginBottom: dt.spacing[2],
    },

    '.article-detail-actions .btn': {
        padding: `${dt.spacing[2]} 18px`,
        fontSize: dt.fontSize.base,
        borderRadius: dt.borderRadius.lg,
    },

    '.article-detail-content': {
        background: dt.colors.white,
        padding: dt.spacing[9],
        borderRadius: dt.borderRadius['2xl'],
        border: `1px solid ${dt.colors.border.light}`,
        boxShadow: dt.boxShadow.cardLight,
        marginBottom: dt.spacing[7],
        lineHeight: dt.lineHeight.loose,
        fontSize: dt.fontSize.lg,
        color: dt.colors.text.primary,
        whiteSpace: 'pre-wrap',
    },

    '.article-detail-stats': {
        display: 'flex',
        justifyContent: 'space-between',
        gap: dt.spacing[4],
        marginBottom: dt.spacing[7],
    },

    '.article-stat': {
        flex: '1',
        background: dt.colors.bgTertiary,
        border: `1px solid ${dt.colors.border.light}`,
        borderRadius: dt.borderRadius.xl,
        padding: `${dt.spacing[4]} ${dt.spacing[4]}`,
        textAlign: 'center',
        boxShadow: 'inset 0 1px 0 rgba(255, 255, 255, 0.3)',
    },

    '.article-stat-number': {
        fontSize: dt.fontSize['2xl'],
        fontWeight: dt.fontWeight.semibold,
        color: dt.colors.text.primary,
        display: 'block',
        marginBottom: dt.spacing[1],
    },

    '.article-stat-label': {
        fontSize: dt.fontSize.sm,
        color: dt.colors.text.secondary,
    },

    '.article-like-section': {
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        gap: dt.spacing[4],
        background: dt.colors.white,
        border: `1px solid ${dt.colors.border.light}`,
        borderRadius: dt.borderRadius['2xl'],
        padding: `${dt.spacing[5]} ${dt.spacing[6]}`,
        marginBottom: dt.spacing[7],
        boxShadow: dt.boxShadow.cardLight,
    },

    '.article-like-button': {
        flex: '0 0 auto',
        minWidth: '160px',
        padding: `${dt.spacing[3]} ${dt.spacing[4]}`,
        borderRadius: dt.borderRadius.full,
        border: 'none',
        fontSize: dt.fontSize.md,
        fontWeight: dt.fontWeight.semibold,
        cursor: 'pointer',
        transition: `all ${dt.transition.fast}`,
        backgroundColor: dt.colors.gray[300],
        color: dt.colors.text.primary,
        boxShadow: dt.boxShadow.none,

        '&[data-liked=\"true\"]': {
            backgroundColor: dt.colors.primary,
            color: dt.colors.white,
            boxShadow: dt.boxShadow['2xl'],
        },
    },

    '.article-like-meta': {
        display: 'flex',
        flexDirection: 'column',
        gap: dt.spacing[1],
        textAlign: 'right',
        alignItems: 'flex-end',
    },

    '.article-like-count-label': {
        fontSize: dt.fontSize.sm,
        color: dt.colors.text.secondary,
    },

    '.article-like-count': {
        fontSize: dt.fontSize['2xl'],
        fontWeight: dt.fontWeight.bold,
        color: dt.colors.text.primary,
    },

    /* Comment Section */
    '.comment-section': {
        background: dt.colors.white,
        padding: dt.spacing[8],
        borderRadius: dt.borderRadius['2xl'],
        border: `1px solid ${dt.colors.border.light}`,
        boxShadow: dt.boxShadow.cardLight,
    },

    '.comment-form': {
        display: 'flex',
        flexDirection: 'column',
        gap: dt.spacing[4],
        marginBottom: dt.spacing[7],
        paddingBottom: dt.spacing[6],
        borderBottom: `1px solid ${dt.colors.gray[200]}`,
    },

    '.comment-form-title': {
        fontSize: dt.fontSize['2xl'],
        fontWeight: dt.fontWeight.bold,
        color: dt.colors.text.dark,
    },

    '.comment-form-header': {
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        marginBottom: dt.spacing[4],
    },

    '.comment-form-reset': {
        display: 'none',
        padding: `${dt.spacing[2]} ${dt.spacing[4]}`,
        background: 'transparent',
        border: `1px solid ${dt.colors.gray[300]}`,
        borderRadius: dt.borderRadius.md,
        color: dt.colors.text.secondary,
        fontSize: dt.fontSize.sm,
        cursor: 'pointer',
        transition: `all ${dt.transition.fast}`,

        ':hover': {
            backgroundColor: dt.colors.gray[100],
            borderColor: dt.colors.gray[400],
        },
    },

    '.comment-submit-btn': {
        width: '100%',
        padding: `${dt.spacing[3]} ${dt.spacing[5]}`,
        fontSize: dt.fontSize.lg,
        fontWeight: dt.fontWeight.semibold,
        borderRadius: dt.borderRadius.lg,
        border: 'none',
        cursor: 'pointer',
        transition: `all ${dt.transition.normal}`,
    },

    '.btn-comment-disabled': {
        backgroundColor: '#ACA0EB !important',
        color: `${dt.colors.white} !important`,
        cursor: 'not-allowed !important',
        opacity: '1 !important',
    },

    '.btn-comment-enabled': {
        backgroundColor: '#7F6AEE !important',
        color: `${dt.colors.white} !important`,
        cursor: 'pointer !important',

        ':hover': {
            backgroundColor: '#6B5DD3 !important',
            boxShadow: dt.boxShadow.md,
        },

        ':active': {
            transform: 'translateY(1px)',
        },
    },

    '.comment-list': {
        display: 'flex',
        flexDirection: 'column',
        gap: dt.spacing[5],
    },

    '.comment-item': {
        paddingBottom: dt.spacing[5],
        borderBottom: `1px solid ${dt.colors.gray[200]}`,

        ':last-child': {
            borderBottom: 'none',
            paddingBottom: '0',
        },
    },

    '.comment-author-avatar': {
        width: '36px',
        height: '36px',
        borderRadius: dt.borderRadius.full,
        backgroundColor: dt.colors.border.dark,
    },

    '.comment-content': {
        fontSize: dt.fontSize.md,
        color: '#444',
        lineHeight: dt.lineHeight.relaxed,
        marginLeft: '46px',
    },

    /* Article Form */
    '.article-form-container': {
        maxWidth: '720px',
        margin: '0 auto',
    },

    '.article-form-title': {
        textAlign: 'center',
        fontSize: dt.fontSize['5xl'],
        fontWeight: dt.fontWeight.bold,
        marginBottom: dt.spacing[9],
        color: dt.colors.text.dark,
    },

    '.article-form': {
        background: dt.colors.white,
        padding: '40px 44px',
        borderRadius: dt.borderRadius['2xl'],
        border: `1px solid ${dt.colors.border.light}`,
        boxShadow: dt.boxShadow.cardLight,
        display: 'flex',
        flexDirection: 'column',
        gap: dt.spacing[6],
    },

    '.article-form .form-textarea': {
        minHeight: '320px',
    },

    /* Responsive */
    '@media (max-width: 768px)': {
        '.article-form': {
            padding: `${dt.spacing[6]} ${dt.spacing[5]}`,
        },

        '.comment-content': {
            marginLeft: '0',
            marginTop: dt.spacing[2],
        },
    },
};

export default articleStyles;
