import { clearAllErrors, showError } from '/js/common/event.js';
import { UserApi } from '/js/api/userApi.js';
import { PageRoutes } from '/js/common/uris.js';
import { DomElements, ElementIds } from '/js/common/domElements.js';

/**
 * 로그인 핸들러
 * 사용자 로그인 기능 처리
 * 클로저 패턴을 사용하여 모듈 상태 캡슐화
 */

const createLoginHandlerModule = (() => {
    /* -------------------------------------------------------------------------- */
    /* 유효성 검사 헬퍼                                                              */
    /* -------------------------------------------------------------------------- */

    function validateEmail(email) {
        if (!email || email.trim().length === 0) {
            return {
                valid: false,
                message: '*올바른 이메일 주소 형식을 입력해주세요. (예: example@example.com)',
            };
        }

        const emailRegex = /^[\w-.]+@([\w-]+\.)+[\w-]{2,4}$/;
        if (!emailRegex.test(email)) {
            return {
                valid: false,
                message: '*올바른 이메일 주소 형식을 입력해주세요. (예: example@example.com)',
            };
        }

        return { valid: true, message: '' };
    }

    function validatePassword(password) {
        if (!password || password.trim().length === 0) {
            return {
                valid: false,
                message: '*비밀번호를 입력해주세요',
            };
        }

        const passwordRegExp = /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[!@#$%^&*(),.?":{}|<>]).{8,20}$/;

        if (!passwordRegExp.test(password)) {
            return {
                valid: false,
                message: '*비밀번호는 8자 이상, 20자 이하이며, 대문자, 소문자, 숫자, 특수문자를 각각 최소 1개 포함해야 합니다.',
            };
        }

        return { valid: true, message: '' };
    }

    function validateLoginForm() {
        const email = DomElements.Login.getEmailValue();
        const password = DomElements.Login.getPasswordValue();
        const loginBtn = DomElements.Login.getLoginBtn();
        const emailError = DomElements.Login.getEmailError();
        const passwordError = DomElements.Login.getPasswordError();

        const emailValidation = validateEmail(email);
        if (emailError) {
            emailError.textContent = emailValidation.message;
            emailError.style.display = emailValidation.valid ? 'none' : 'block';
        }

        const passwordValidation = validatePassword(password);
        if (passwordError) {
            passwordError.textContent = passwordValidation.message;
            passwordError.style.display = passwordValidation.valid ? 'none' : 'block';
        }

        const isFormValid = emailValidation.valid && passwordValidation.valid;
        if (loginBtn) {
            loginBtn.disabled = !isFormValid;
            loginBtn.classList.toggle('btn-login-enabled', isFormValid);
            loginBtn.classList.toggle('btn-login-disabled', !isFormValid);
        }

        return isFormValid;
    }

    /* -------------------------------------------------------------------------- */
    /* 로그인                                                                      */
    /* -------------------------------------------------------------------------- */

    async function handleLogin(event) {
        event.preventDefault();
        clearAllErrors();

        if (!validateLoginForm()) {
            return;
        }

        const email = DomElements.Login.getEmailValue();
        const password = DomElements.Login.getPasswordValue();

        try {
            const result = await UserApi.login(email, password);
            const redirectUrl = result?.redirectUrl || result?.location;
            window.location.href = redirectUrl || PageRoutes.ARTICLES;
        } catch (error) {
            const message = error?.message || '*아이디 또는 비밀번호를 확인해주세요';
            showError(ElementIds.LOGIN_PASSWORD_ERROR, message.startsWith('*') ? message : `*${message}`);
        }
    }

    /* -------------------------------------------------------------------------- */
    /* 공개 초기화 함수                                                              */
    /* -------------------------------------------------------------------------- */

    function initLoginPage() {
        const signupBtn = DomElements.manager.get(ElementIds.SIGNUP_LINK_BTN);
        if (signupBtn) {
            signupBtn.addEventListener('click', () => (window.location.href = PageRoutes.USER_SIGNUP));
        }

        const emailInput = DomElements.Login.getEmail();
        const passwordInput = DomElements.Login.getPassword();
        emailInput?.addEventListener('input', validateLoginForm);
        passwordInput?.addEventListener('input', validateLoginForm);

        const loginBtn = DomElements.Login.getLoginBtn();
        loginBtn?.addEventListener('click', handleLogin);
    }

    // 공개 API
    return {
        initLoginPage
    };
})();

// 공개 함수 내보내기
export const { initLoginPage } = createLoginHandlerModule;
