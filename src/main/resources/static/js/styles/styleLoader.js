/**
 * 스타일 로더
 * 페이지별로 필요한 스타일을 자동으로 로드합니다.
 */

import styleManager from './styleManager.js';
import { lightTheme } from './theme.js';
import commonStyles from './common.styles.js';
import userStyles from './user.styles.js';
import articleStyles from './article.styles.js';

/**
 * 기본 스타일 로드 (모든 페이지에서 사용)
 */
export function loadBaseStyles() {
    // 테마 변수 적용
    styleManager.setCSSVariables(lightTheme.variables);

    // 공통 스타일 주입
    styleManager.injectStyles('common', commonStyles, { priority: 'high' });
}

/**
 * User 페이지 스타일 로드
 */
export function loadUserStyles() {
    styleManager.injectStyles('user', userStyles);
}

/**
 * Article 페이지 스타일 로드
 */
export function loadArticleStyles() {
    styleManager.injectStyles('article', articleStyles);
}

/**
 * 페이지 타입에 따라 자동으로 스타일 로드
 * @param {string} pageType - 페이지 타입 ('user', 'article', 'common')
 */
export function loadStylesForPage(pageType) {
    // 기본 스타일은 항상 로드
    loadBaseStyles();

    // 페이지별 스타일 로드
    switch(pageType) {
        case 'user':
            loadUserStyles();
            break;
        case 'article':
            loadArticleStyles();
            break;
        case 'all':
            loadUserStyles();
            loadArticleStyles();
            break;
        default:
            // 기본 스타일만 로드
            break;
    }
}

/**
 * 현재 URL을 기반으로 자동으로 페이지 타입 감지 및 스타일 로드
 */
export function autoLoadStyles() {
    const path = window.location.pathname;

    let pageType = 'common';

    if (path.includes('/user/') || path.includes('/auth/')) {
        pageType = 'user';
    } else if (path.includes('/article')) {
        pageType = 'article';
    }

    loadStylesForPage(pageType);
}

/**
 * 동적으로 테마 변경
 * @param {Object} theme - 테마 객체
 */
export function changeTheme(theme) {
    styleManager.setCSSVariables(theme.variables);
}

/**
 * 특정 스타일 추가 (런타임 동적 스타일)
 * @param {string} id - 스타일 ID
 * @param {Object|string} styles - 스타일 객체 또는 CSS 문자열
 */
export function addCustomStyles(id, styles) {
    styleManager.injectStyles(id, styles);
}

/**
 * 스타일 제거
 * @param {string} id - 제거할 스타일 ID
 */
export function removeStyles(id) {
    styleManager.removeStyles(id);
}
