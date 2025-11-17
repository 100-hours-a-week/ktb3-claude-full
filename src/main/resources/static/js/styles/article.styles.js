/**
 * Article 페이지 스타일 정의
 * 게시글 목록, 상세, 작성/수정 등
 */

import { designTokens as dt } from './theme.js';

export const articleStyles = {
    /* 게시글 목록 */
    '.article-detail-main': {
        display: 'flex',
        justifyContent: 'center',
    },

    '.article-list-header': {
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        marginBottom: dt.spacing[10],
    },

    '.article-list-intro-wrapper': {
        flex: '1',
    },

    '.article-list-intro': {
        fontSize: dt.fontSize['2xl'],
        color: dt.colors.text.secondary,
        marginBottom: '0',
        lineHeight: dt.lineHeight.relaxed,
        textAlign: 'left',

        'strong': {
            color: dt.colors.text.primary,
            fontWeight: dt.fontWeight.semibold,
        },
    },

    '.article-create-btn': {
        display: 'inline-flex',
        flexShrink: '0',
        marginLeft: dt.spacing[4],
    },

    '.article-list': {
        display: 'flex',
        flexDirection: 'column',
        gap: dt.spacing[5],  // Increased from spacing[4]
    },

    /* 게시글 카드 */
    '.article-card': {
        background: dt.colors.white,
        borderRadius: dt.borderRadius.lg,
        padding: dt.spacing[6],  // Increased from spacing[5]
        boxShadow: dt.boxShadow.card,  // Softer shadow
        transition: `all ${dt.transition.fast}`,
        cursor: 'pointer',
        textDecoration: 'none',
        color: 'inherit',
        display: 'block',
        border: `1px solid ${dt.colors.border.default}`,

        ':hover': {
            transform: 'translateY(-1px)',  // Reduced from -2px
            boxShadow: dt.boxShadow.md,
            borderColor: dt.colors.border.dark,  // Subtle border change
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
        marginBottom: dt.spacing[3],  // Increased spacing
        color: dt.colors.text.primary,
        lineHeight: dt.lineHeight.tight,
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

    /* 게시글 상세 */
    '.article-detail-container': {
        width: '100%',
        maxWidth: '720px',
        margin: '0 auto',
    },

    '.article-toc': {
        position: 'sticky',
        top: '100px',
        width: '240px',
        height: 'fit-content',
        maxHeight: 'calc(100vh - 120px)',
        overflowY: 'auto',
        flexShrink: '0',
    },

    '.article-toc-title': {
        fontSize: dt.fontSize.sm,
        fontWeight: dt.fontWeight.bold,
        color: dt.colors.text.secondary,
        marginBottom: dt.spacing[3],
        textTransform: 'uppercase',
        letterSpacing: '0.05em',
    },

    '.article-toc-nav': {
        display: 'flex',
        flexDirection: 'column',
        gap: dt.spacing[1],
    },

    '.toc-link': {
        display: 'block',
        fontSize: dt.fontSize.sm,
        color: dt.colors.text.secondary,
        textDecoration: 'none',
        padding: `${dt.spacing[1]} ${dt.spacing[2]}`,
        borderLeft: `2px solid transparent`,
        transition: `all ${dt.transition.fast}`,

        ':hover': {
            color: dt.colors.primary,
            borderLeftColor: dt.colors.primary,
        },

        '&.active': {
            color: dt.colors.primary,
            borderLeftColor: dt.colors.primary,
            fontWeight: dt.fontWeight.semibold,
        },
    },

    '.toc-link-h2': {
        paddingLeft: dt.spacing[2],
    },

    '.toc-link-h3': {
        paddingLeft: dt.spacing[4],
        fontSize: dt.fontSize.xs,
    },

    '.article-detail-header': {
        background: dt.colors.white,
        padding: `${dt.spacing[8]} ${dt.spacing[8]}`,  // More balanced padding
        borderRadius: dt.borderRadius.xl,  // Changed from 2xl
        border: `1px solid ${dt.colors.border.default}`,
        boxShadow: dt.boxShadow.card,
        marginBottom: dt.spacing[6],  // Reduced from 7
    },

    '.article-detail-title': {
        fontSize: dt.fontSize['4xl'],
        fontWeight: dt.fontWeight.bold,  // Changed from semibold
        marginBottom: dt.spacing[5],     // Increased spacing
        lineHeight: dt.lineHeight.tight,
        color: dt.colors.text.primary,
    },

    '.article-detail-meta': {
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
    },

    '.article-detail-author': {
        display: 'flex',
        alignItems: 'center',
        gap: dt.spacing[2],
    },

    '.article-detail-author-avatar': {
        width: '40px',
        height: '40px',
        borderRadius: dt.borderRadius.full,
        backgroundColor: '#c4c4c4',
    },

    '.article-detail-author-info': {
        display: 'flex',
        flexDirection: 'column',
        gap: '2px',
    },

    '.article-detail-author-name': {
        fontSize: dt.fontSize.base,
        fontWeight: dt.fontWeight.medium,
        color: dt.colors.text.primary,
    },

    '.article-detail-date': {
        fontSize: dt.fontSize.sm,
        color: dt.colors.text.secondary,
    },

    '.article-detail-actions': {
        display: 'flex',
        gap: dt.spacing[2],
    },

    '.article-detail-actions .btn': {
        padding: `${dt.spacing[2]} 18px`,
        fontSize: dt.fontSize.base,
        borderRadius: dt.borderRadius.lg,
    },

    '.article-detail-content': {
        background: dt.colors.white,
        padding: dt.spacing[8],  // Reduced from 9
        borderRadius: dt.borderRadius.xl,  // Changed from 2xl
        border: `1px solid ${dt.colors.border.default}`,
        boxShadow: dt.boxShadow.card,
        marginBottom: dt.spacing[6],  // Reduced from 7
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

    '.meta-item': {
        color: dt.colors.text.secondary,
    },

    '.meta-divider': {
        color: dt.colors.text.tertiary,
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

    /* 댓글 섹션 */
    '.comment-section': {
        background: dt.colors.white,
        padding: dt.spacing[8],
        borderRadius: dt.borderRadius.xl,
        border: `1px solid ${dt.colors.border.default}`,
        boxShadow: dt.boxShadow.card,
    },

    '.comment-section-title': {
        fontSize: dt.fontSize['2xl'],
        fontWeight: dt.fontWeight.bold,
        color: dt.colors.text.primary,
        lineHeight: dt.lineHeight.tight,
        marginBottom: dt.spacing[6],
    },

    '.comment-form': {
        display: 'flex',
        flexDirection: 'column',
        gap: dt.spacing[4],
        marginBottom: dt.spacing[8],
        paddingBottom: dt.spacing[7],
        borderBottom: `1px solid ${dt.colors.border.default}`,
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
        backgroundColor: `${dt.colors.primaryLight} !important`,
        color: `${dt.colors.white} !important`,
        cursor: 'not-allowed !important',
        opacity: '0.6 !important',
    },

    '.btn-comment-enabled': {
        backgroundColor: `${dt.colors.primary} !important`,
        color: `${dt.colors.white} !important`,
        cursor: 'pointer !important',

        ':hover': {
            backgroundColor: `${dt.colors.primaryDark} !important`,
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

    '.comment-header': {
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        marginBottom: dt.spacing[2],
    },

    '.comment-author': {
        display: 'flex',
        alignItems: 'center',
        gap: dt.spacing[2],
    },

    '.comment-author-avatar': {
        width: '36px',
        height: '36px',
        borderRadius: dt.borderRadius.full,
        backgroundColor: dt.colors.border.dark,
    },

    '.comment-author-name': {
        fontSize: dt.fontSize.base,
        fontWeight: dt.fontWeight.medium,
        color: dt.colors.text.primary,
    },

    '.comment-date': {
        fontSize: dt.fontSize.sm,
        color: dt.colors.text.secondary,
        marginLeft: dt.spacing[2],
    },

    '.comment-actions': {
        display: 'flex',
        gap: dt.spacing[2],
    },

    '.comment-actions .btn': {
        padding: `${dt.spacing[1]} ${dt.spacing[3]}`,
        fontSize: dt.fontSize.sm,
    },

    '.comment-content': {
        fontSize: dt.fontSize.base,  // Changed from md
        color: dt.colors.text.primary,  // Changed from hardcoded #444
        lineHeight: dt.lineHeight.relaxed,
        marginLeft: '48px',  // Adjusted from 46px
        marginTop: dt.spacing[2],
    },

    /* 게시글 폼 */
    '.article-form-container': {
        maxWidth: '800px',  // Increased from 720px
        margin: '0 auto',
    },

    '.article-form-title': {
        textAlign: 'center',
        fontSize: dt.fontSize['4xl'],  // Reduced from 5xl
        fontWeight: dt.fontWeight.bold,
        marginBottom: dt.spacing[10],  // Increased from 9
        color: dt.colors.text.primary,
        lineHeight: dt.lineHeight.tight,
    },

    '.article-form': {
        background: dt.colors.white,
        padding: `${dt.spacing[8]} ${dt.spacing[8]}`,  // More consistent
        borderRadius: dt.borderRadius.xl,  // Changed from 2xl
        border: `1px solid ${dt.colors.border.default}`,
        boxShadow: dt.boxShadow.card,
        display: 'flex',
        flexDirection: 'column',
        gap: dt.spacing[6],
    },

    '.article-form .form-textarea': {
        minHeight: '360px',  // Increased from 320px
        lineHeight: dt.lineHeight.relaxed,
    },

    /* 반응형 */
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
