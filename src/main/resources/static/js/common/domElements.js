/**
 * DOM 요소 선택자 관리
 * 모든 DOM 요소 ID를 중앙에서 관리하고, 캐싱 및 안전한 접근을 제공합니다.
 */

/**
 * DOM 요소 ID 상수
 */
export let ElementIds = {
    // User - Login
    LOGIN_EMAIL: 'email',
    LOGIN_PASSWORD: 'password',
    LOGIN_BTN: 'loginBtn',
    LOGIN_EMAIL_ERROR: 'emailError',
    LOGIN_PASSWORD_ERROR: 'passwordError',

    // User - Signup
    SIGNUP_FORM: 'signupForm',
    SIGNUP_BTN: 'signupBtn',
    SIGNUP_EMAIL: 'email',
    SIGNUP_PASSWORD: 'password',
    SIGNUP_PASSWORD_CONFIRM: 'passwordConfirm',
    SIGNUP_NICKNAME: 'nickname',
    SIGNUP_PROFILE_IMAGE: 'profileImage',
    SIGNUP_PROFILE_ERROR: 'profileError',
    SIGNUP_PREVIEW_IMG: 'previewImg',
    SIGNUP_PROFILE_PREVIEW: 'profilePreview',

    // User - Edit
    USER_EMAIL: 'userEmail',
    USER_NICKNAME: 'nickname',
    USER_EDIT_FORM: 'userEditForm',
    USER_PROFILE_PREVIEW_IMG: 'profilePreviewImg',
    USER_PROFILE_IMAGE_CLICK_AREA: 'profileImageClickArea',
    USER_PROFILE_IMAGE_INPUT: 'profileImageInput',
    USER_DELETE_ACCOUNT_BTN: 'deleteAccountBtn',

    // User - Password
    PASSWORD_CURRENT: 'currentPassword',
    PASSWORD_NEW: 'newPassword',
    PASSWORD_UPDATE_FORM: 'passwordUpdateForm',

    // User - Header
    USER_MENU_BTN: 'userMenuBtn',
    LOGOUT_BTN: 'logoutBtn',

    // Article - List
    ARTICLE_LIST: 'articleList',

    // Article - Detail
    ARTICLE_HEADER: 'articleHeader',
    ARTICLE_IMAGE_CONTAINER: 'articleImageContainer',
    ARTICLE_CONTENT: 'articleContent',
    ARTICLE_STATS: 'articleStats',
    ARTICLE_LIKE_BTN: 'articleLikeBtn',
    ARTICLE_LIKE_COUNT: 'articleLikeCount',

    // Article - Form
    ARTICLE_FORM: 'articleForm',
    ARTICLE_ID: 'articleId',
    ARTICLE_TITLE: 'title',
    ARTICLE_CONTENT_INPUT: 'content',
    ARTICLE_IMAGE_INPUT: 'imageInput',
    ARTICLE_FILE_NAME_TEXT: 'fileNameText',
    ARTICLE_PREVIEW_IMG: 'previewImg',
    ARTICLE_IMAGE_PREVIEW: 'imagePreview',
    ARTICLE_FILE_UPLOAD_BTN: 'fileUploadBtn',
    ARTICLE_FORM_TITLE: 'formTitle',
    ARTICLE_SUBMIT_BTN: 'submitBtn',

    // Comment
    COMMENT_LIST: 'commentList',
    COMMENT_FORM: 'commentForm',
    COMMENT_CONTENT: 'commentContent',
    COMMENT_SUBMIT_BTN: 'commentSubmitBtn',
    COMMENT_FORM_TITLE: 'commentFormTitle',
    COMMENT_RESET_BTN: 'commentResetBtn',
};

/**
 * DOM 요소 관리 클래스
 * 요소 캐싱 및 안전한 접근을 제공합니다.
 */
class DomElementManager {
    constructor() {
        this.cache = new Map();
        this.enableCache = true;
        this.initialized = false;
    }

    /**
     * 페이지 초기화 시 data-element-key 속성을 가진 요소들에 ID를 자동 할당합니다.
     */
    initializeElements() {
        if (this.initialized) return;

        document.querySelectorAll('[data-element-key]').forEach(element => {
            const key = element.getAttribute('data-element-key');
            const id = ElementIds[key];

            if (id) {
                // ID가 이미 있다면 경고 (중복 방지)
                if (element.id && element.id !== id) {
                    console.warn(`Element with data-element-key="${key}" already has different id="${element.id}". Overwriting with "${id}".`);
                }
                element.id = id;
            } else {
                console.warn(`No ElementIds mapping found for key: ${key}`);
            }
        });

        this.initialized = true;
    }

    /**
     * ElementIds 키로 요소를 가져옵니다
     * @param {string} key - ElementIds의 키 (예: 'ARTICLE_LIKE_BTN')
     * @returns {HTMLElement|null} DOM 요소
     */
    getByKey(key) {
        const id = ElementIds[key];
        if (!id) {
            console.warn(`No ElementIds mapping found for key: ${key}`);
            return null;
        }
        return this.get(id);
    }

    /**
     * ID로 요소를 가져옵니다 (캐싱 지원)
     * @param {string} id - 요소 ID
     * @param {boolean} useCache - 캐시 사용 여부 (기본: true)
     * @returns {HTMLElement|null} DOM 요소
     */
    get(id, useCache = true) {
        if (this.enableCache && useCache && this.cache.has(id)) {
            return this.cache.get(id);
        }

        const element = document.getElementById(id);

        if (this.enableCache && useCache && element) {
            this.cache.set(id, element);
        }

        return element;
    }

    /**
     * 여러 요소를 한번에 가져옵니다
     * @param {string[]} ids - 요소 ID 배열
     * @returns {Object} ID를 키로 하는 요소 객체
     */
    getMultiple(ids) {
        const elements = {};
        ids.forEach(id => {
            elements[id] = this.get(id);
        });
        return elements;
    }

    /**
     * 요소의 값을 안전하게 가져옵니다
     * @param {string} id - 요소 ID
     * @param {string} defaultValue - 기본값
     * @returns {string} 요소의 값 또는 기본값
     */
    getValue(id, defaultValue = '') {
        const element = this.get(id);
        return element?.value ?? defaultValue;
    }

    /**
     * 요소에 값을 설정합니다
     * @param {string} id - 요소 ID
     * @param {string} value - 설정할 값
     * @returns {boolean} 성공 여부
     */
    setValue(id, value) {
        const element = this.get(id);
        if (element) {
            element.value = value;
            return true;
        }
        return false;
    }

    /**
     * 요소의 텍스트 콘텐츠를 가져옵니다
     * @param {string} id - 요소 ID
     * @param {string} defaultValue - 기본값
     * @returns {string} 텍스트 콘텐츠 또는 기본값
     */
    getText(id, defaultValue = '') {
        const element = this.get(id);
        return element?.textContent ?? defaultValue;
    }

    /**
     * 요소의 텍스트 콘텐츠를 설정합니다
     * @param {string} id - 요소 ID
     * @param {string} text - 설정할 텍스트
     * @returns {boolean} 성공 여부
     */
    setText(id, text) {
        const element = this.get(id);
        if (element) {
            element.textContent = text;
            return true;
        }
        return false;
    }

    /**
     * 요소에 이벤트 리스너를 추가합니다
     * @param {string} id - 요소 ID
     * @param {string} event - 이벤트 타입
     * @param {Function} handler - 이벤트 핸들러
     * @returns {boolean} 성공 여부
     */
    on(id, event, handler) {
        const element = this.get(id);
        if (element) {
            element.addEventListener(event, handler);
            return true;
        }
        return false;
    }

    /**
     * 캐시를 초기화합니다
     */
    clearCache() {
        this.cache.clear();
    }

    /**
     * 특정 요소의 캐시를 제거합니다
     * @param {string} id - 요소 ID
     */
    removeCacheItem(id) {
        this.cache.delete(id);
    }

    /**
     * 캐싱 활성화/비활성화
     * @param {boolean} enable - 활성화 여부
     */
    setCacheEnabled(enable) {
        this.enableCache = enable;
        if (!enable) {
            this.clearCache();
        }
    }
}

/**
 * 페이지별 DOM 요소 헬퍼
 */
export let DomElements = {
    manager: new DomElementManager(),

    /**
     * Login 페이지 요소들
     */
    Login: {
        getEmail: () => DomElements.manager.get(ElementIds.LOGIN_EMAIL),
        getPassword: () => DomElements.manager.get(ElementIds.LOGIN_PASSWORD),
        getLoginBtn: () => DomElements.manager.get(ElementIds.LOGIN_BTN),
        getEmailError: () => DomElements.manager.get(ElementIds.LOGIN_EMAIL_ERROR),
        getPasswordError: () => DomElements.manager.get(ElementIds.LOGIN_PASSWORD_ERROR),

        getEmailValue: () => DomElements.manager.getValue(ElementIds.LOGIN_EMAIL),
        getPasswordValue: () => DomElements.manager.getValue(ElementIds.LOGIN_PASSWORD),
    },

    /**
     * Signup 페이지 요소들
     */
    Signup: {
        getForm: () => DomElements.manager.get(ElementIds.SIGNUP_FORM),
        getEmail: () => DomElements.manager.get(ElementIds.SIGNUP_EMAIL),
        getPassword: () => DomElements.manager.get(ElementIds.SIGNUP_PASSWORD),
        getPasswordConfirm: () => DomElements.manager.get(ElementIds.SIGNUP_PASSWORD_CONFIRM),
        getNickname: () => DomElements.manager.get(ElementIds.SIGNUP_NICKNAME),
        getProfileImage: () => DomElements.manager.get(ElementIds.SIGNUP_PROFILE_IMAGE),
        getSignupBtn: () => DomElements.manager.get(ElementIds.SIGNUP_BTN),
        getProfileError: () => DomElements.manager.get(ElementIds.SIGNUP_PROFILE_ERROR),
        getPreviewImg: () => DomElements.manager.get(ElementIds.SIGNUP_PREVIEW_IMG),
        getProfilePreview: () => DomElements.manager.get(ElementIds.SIGNUP_PROFILE_PREVIEW),

        getEmailValue: () => DomElements.manager.getValue(ElementIds.SIGNUP_EMAIL),
        getPasswordValue: () => DomElements.manager.getValue(ElementIds.SIGNUP_PASSWORD),
        getPasswordConfirmValue: () => DomElements.manager.getValue(ElementIds.SIGNUP_PASSWORD_CONFIRM),
        getNicknameValue: () => DomElements.manager.getValue(ElementIds.SIGNUP_NICKNAME),
    },

    /**
     * User Edit 페이지 요소들
     */
    UserEdit: {
        getForm: () => DomElements.manager.get(ElementIds.USER_EDIT_FORM),
        getEmail: () => DomElements.manager.get(ElementIds.USER_EMAIL),
        getNickname: () => DomElements.manager.get(ElementIds.USER_NICKNAME),
        getProfilePreviewImg: () => DomElements.manager.get(ElementIds.USER_PROFILE_PREVIEW_IMG),
        getProfileImageClickArea: () => DomElements.manager.get(ElementIds.USER_PROFILE_IMAGE_CLICK_AREA),
        getProfileImageInput: () => DomElements.manager.get(ElementIds.USER_PROFILE_IMAGE_INPUT),
        getDeleteAccountBtn: () => DomElements.manager.get(ElementIds.USER_DELETE_ACCOUNT_BTN),

        getNicknameValue: () => DomElements.manager.getValue(ElementIds.USER_NICKNAME),
    },

    /**
     * Password 페이지 요소들
     */
    Password: {
        getForm: () => DomElements.manager.get(ElementIds.PASSWORD_UPDATE_FORM),
        getCurrentPassword: () => DomElements.manager.get(ElementIds.PASSWORD_CURRENT),
        getNewPassword: () => DomElements.manager.get(ElementIds.PASSWORD_NEW),

        getCurrentPasswordValue: () => DomElements.manager.getValue(ElementIds.PASSWORD_CURRENT),
        getNewPasswordValue: () => DomElements.manager.getValue(ElementIds.PASSWORD_NEW),
    },

    /**
     * Header 요소들
     */
    Header: {
        getUserMenuBtn: () => DomElements.manager.get(ElementIds.USER_MENU_BTN),
        getLogoutBtn: () => DomElements.manager.get(ElementIds.LOGOUT_BTN),
    },

    /**
     * Article List 페이지 요소들
     */
    ArticleList: {
        getContainer: () => DomElements.manager.get(ElementIds.ARTICLE_LIST),
    },

    /**
     * Article Detail 페이지 요소들
     */
    ArticleDetail: {
        getHeader: () => DomElements.manager.get(ElementIds.ARTICLE_HEADER),
        getImageContainer: () => DomElements.manager.get(ElementIds.ARTICLE_IMAGE_CONTAINER),
        getContent: () => DomElements.manager.get(ElementIds.ARTICLE_CONTENT),
        getStats: () => DomElements.manager.get(ElementIds.ARTICLE_STATS),
        getLikeBtn: () => DomElements.manager.get(ElementIds.ARTICLE_LIKE_BTN),
        getLikeCount: () => DomElements.manager.get(ElementIds.ARTICLE_LIKE_COUNT),
    },

    /**
     * Article Form 페이지 요소들
     */
    ArticleForm: {
        getForm: () => DomElements.manager.get(ElementIds.ARTICLE_FORM),
        getArticleId: () => DomElements.manager.get(ElementIds.ARTICLE_ID),
        getTitle: () => DomElements.manager.get(ElementIds.ARTICLE_TITLE),
        getContent: () => DomElements.manager.get(ElementIds.ARTICLE_CONTENT_INPUT),
        getImageInput: () => DomElements.manager.get(ElementIds.ARTICLE_IMAGE_INPUT),
        getFileNameText: () => DomElements.manager.get(ElementIds.ARTICLE_FILE_NAME_TEXT),
        getPreviewImg: () => DomElements.manager.get(ElementIds.ARTICLE_PREVIEW_IMG),
        getImagePreview: () => DomElements.manager.get(ElementIds.ARTICLE_IMAGE_PREVIEW),
        getFileUploadBtn: () => DomElements.manager.get(ElementIds.ARTICLE_FILE_UPLOAD_BTN),
        getFormTitle: () => DomElements.manager.get(ElementIds.ARTICLE_FORM_TITLE),
        getSubmitBtn: () => DomElements.manager.get(ElementIds.ARTICLE_SUBMIT_BTN),

        getTitleValue: () => DomElements.manager.getValue(ElementIds.ARTICLE_TITLE),
        getContentValue: () => DomElements.manager.getValue(ElementIds.ARTICLE_CONTENT_INPUT),
    },

    /**
     * Comment 요소들
     */
    Comment: {
        getList: () => DomElements.manager.get(ElementIds.COMMENT_LIST),
        getForm: () => DomElements.manager.get(ElementIds.COMMENT_FORM),
        getContent: () => DomElements.manager.get(ElementIds.COMMENT_CONTENT),
        getSubmitBtn: () => DomElements.manager.get(ElementIds.COMMENT_SUBMIT_BTN),
        getFormTitle: () => DomElements.manager.get(ElementIds.COMMENT_FORM_TITLE),
        getResetBtn: () => DomElements.manager.get(ElementIds.COMMENT_RESET_BTN),

        getContentValue: () => DomElements.manager.getValue(ElementIds.COMMENT_CONTENT),
    },

    /**
     * 캐시 관리
     */
    clearCache: () => DomElements.manager.clearCache(),
    setCacheEnabled: (enable) => DomElements.manager.setCacheEnabled(enable),

    /**
     * DOM 요소 초기화 (data-element-key를 id로 변환)
     */
    initialize: () => DomElements.manager.initializeElements(),
};

// 전역 객체에 노출 (디버깅 용도)
if (typeof window !== 'undefined') {
    window.ElementIds = ElementIds;
    window.DomElements = DomElements;

    // DOMContentLoaded 시 자동으로 data-element-key 요소들을 초기화
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', () => {
            DomElements.initialize();
        });
    } else {
        // 이미 로드된 경우 즉시 초기화
        DomElements.initialize();
    }
}

export default DomElements;
