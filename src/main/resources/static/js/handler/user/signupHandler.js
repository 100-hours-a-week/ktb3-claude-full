import {
    clearAllErrors,
    showError,
    showSuccess,
    showErrorAlert,
    clearError,
} from '/js/common/event.js';
import { UserApi } from '/js/api/userApi.js';
import { PageRoutes } from '/js/common/uris.js';
import { DomElements, ElementIds } from '/js/common/domElements.js';

/**
 * 회원가입 핸들러
 * 사용자 회원가입 기능 처리
 * 클로저 패턴을 사용하여 모듈 상태 캡슐화
 */

const createSignupHandlerModule = (() => {
    // 클로저로 캡슐화된 비공개 상태
    let hasProfileImage = false;
    let validationState = {
        email: false,
        password: false,
        passwordConfirm: false,
        nickname: false,
    };
    let debounceTimers = {
        email: null,
        nickname: null,
    };

    /* -------------------------------------------------------------------------- */
    /* 헬퍼 함수                                                                    */
    /* -------------------------------------------------------------------------- */

    function debounce(func, delay) {
        let timeoutId;
        return function (...args) {
            clearTimeout(timeoutId);
            timeoutId = setTimeout(() => func.apply(this, args), delay);
        };
    }

    function convertImageToBase64(file) {
        return new Promise((resolve, reject) => {
            const reader = new FileReader();
            reader.onload = () => resolve(reader.result);
            reader.onerror = reject;
            reader.readAsDataURL(file);
        });
    }

    /* -------------------------------------------------------------------------- */
    /* 유효성 검사                                                                  */
    /* -------------------------------------------------------------------------- */

    async function validateEmailField() {
        const email = DomElements.Signup.getEmailValue();

        if (!email || email.trim().length === 0) {
            showError(ElementIds.SIGNUP_EMAIL_ERROR, '*이메일을 입력해주세요.');
            validationState.email = false;
            updateSignupButtonState();
            return false;
        }

        const emailRegex = /^[\w-.]+@([\w-]+\.)+[\w-]{2,4}$/;
        if (!emailRegex.test(email)) {
            showError(ElementIds.SIGNUP_EMAIL_ERROR, '*올바른 이메일 주소 형식을 입력해주세요. (예: example@example.com)');
            validationState.email = false;
            updateSignupButtonState();
            return false;
        }

        // 이메일 중복 확인
        try {
            const exists = await UserApi.checkEmailExists(email);
            if (exists) {
                showError(ElementIds.SIGNUP_EMAIL_ERROR, '*중복된 이메일입니다.');
                validationState.email = false;
                updateSignupButtonState();
                return false;
            }
        } catch (error) {
            console.error('Email check failed:', error);
        }

        clearError(ElementIds.SIGNUP_EMAIL_ERROR);
        validationState.email = true;
        updateSignupButtonState();
        return true;
    }

    async function validatePasswordField() {
        const password = DomElements.Signup.getPasswordValue();
        const passwordConfirm = DomElements.Signup.getPasswordConfirmValue();

        if (!password || password.trim().length === 0) {
            showError(ElementIds.SIGNUP_PASSWORD_ERROR, '*비밀번호를 입력해주세요');
            validationState.password = false;
            updateSignupButtonState();
            return false;
        }

        const passwordRegExp = /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[!@#$%^&*(),.?":{}|<>]).{8,20}$/;
        if (!passwordRegExp.test(password)) {
            showError(ElementIds.SIGNUP_PASSWORD_ERROR, '*비밀번호는 8자 이상, 20자 이하이며, 대문자, 소문자, 숫자, 특수문자를 각각 최소 1개 포함해야 합니다.');
            validationState.password = false;
            updateSignupButtonState();
            return false;
        }

        if (passwordConfirm && password !== passwordConfirm) {
            showError(ElementIds.SIGNUP_PASSWORD_ERROR, '*비밀번호가 다릅니다.');
            validationState.password = false;
            updateSignupButtonState();
            return false;
        }

        clearError(ElementIds.SIGNUP_PASSWORD_ERROR);
        validationState.password = true;
        updateSignupButtonState();
        return true;
    }

    async function validatePasswordConfirmField() {
        const password = DomElements.Signup.getPasswordValue();
        const passwordConfirm = DomElements.Signup.getPasswordConfirmValue();

        if (!passwordConfirm || passwordConfirm.trim().length === 0) {
            showError(ElementIds.SIGNUP_PASSWORD_CONFIRM_ERROR, '*비밀번호를 한번더 입력해주세요.');
            validationState.passwordConfirm = false;
            updateSignupButtonState();
            return false;
        }

        if (password !== passwordConfirm) {
            showError(ElementIds.SIGNUP_PASSWORD_CONFIRM_ERROR, '*비밀번호가 다릅니다.');
            validationState.passwordConfirm = false;
            updateSignupButtonState();
            return false;
        }

        clearError(ElementIds.SIGNUP_PASSWORD_CONFIRM_ERROR);
        validationState.passwordConfirm = true;
        updateSignupButtonState();
        return true;
    }

    async function validateNicknameField() {
        const nickname = DomElements.Signup.getNicknameValue();

        if (!nickname || nickname.trim().length === 0) {
            showError(ElementIds.SIGNUP_NICKNAME_ERROR, '*닉네임을 입력해주세요.');
            validationState.nickname = false;
            updateSignupButtonState();
            return false;
        }

        if (/\s/.test(nickname)) {
            showError(ElementIds.SIGNUP_NICKNAME_ERROR, '*띄어쓰기를 없애주세요');
            validationState.nickname = false;
            updateSignupButtonState();
            return false;
        }

        if (nickname.length > 10) {
            showError(ElementIds.SIGNUP_NICKNAME_ERROR, '*닉네임은 최대 10자 까지 가능합니다.');
            validationState.nickname = false;
            updateSignupButtonState();
            return false;
        }

        // 닉네임 중복 확인
        try {
            const exists = await UserApi.checkNicknameExists(nickname);
            if (exists) {
                showError(ElementIds.SIGNUP_NICKNAME_ERROR, '*중복된 닉네임입니다.');
                validationState.nickname = false;
                updateSignupButtonState();
                return false;
            }
        } catch (error) {
            console.error('Nickname check failed:', error);
        }

        clearError(ElementIds.SIGNUP_NICKNAME_ERROR);
        validationState.nickname = true;
        updateSignupButtonState();
        return true;
    }

    function validateProfileImage() {
        const profileError = DomElements.Signup.getProfileError();

        if (profileError) {
            profileError.textContent = '';
            profileError.style.display = 'none';
        }

        updateSignupButtonState();
        return true;
    }

    function updateSignupButtonState() {
        const signupBtn = DomElements.Signup.getSignupBtn();
        const isFormValid =
            validationState.email &&
            validationState.password &&
            validationState.passwordConfirm &&
            validationState.nickname;

        if (signupBtn) {
            signupBtn.disabled = !isFormValid;
            if (isFormValid) {
                signupBtn.classList.remove('btn-signup-disabled');
                signupBtn.classList.add('btn-signup-enabled');
                signupBtn.style.backgroundColor = '#7F6AEE';
            } else {
                signupBtn.classList.remove('btn-signup-enabled');
                signupBtn.classList.add('btn-signup-disabled');
                signupBtn.style.backgroundColor = '#ACA0EB';
            }
        }
    }

    function validateSignupForm() {
        updateSignupButtonState();
        return validationState.email &&
               validationState.password &&
               validationState.passwordConfirm &&
               validationState.nickname;
    }

    /* -------------------------------------------------------------------------- */
    /* 회원가입                                                                    */
    /* -------------------------------------------------------------------------- */

    function handleProfileImageChange(event) {
        const file = event.target.files?.[0];
        const previewImg = DomElements.Signup.getPreviewImg();

        if (file && previewImg) {
            const reader = new FileReader();
            reader.onload = e => {
                previewImg.src = e.target.result;
                previewImg.style.display = 'block';
            };
            reader.readAsDataURL(file);
            hasProfileImage = true;
        } else {
            if (previewImg) {
                previewImg.src = '';
                previewImg.style.display = 'none';
            }
            hasProfileImage = false;
        }

        validateProfileImage();
    }

    async function handleSignup(event) {
        event.preventDefault();
        clearAllErrors();

        if (!validateSignupForm()) {
            return;
        }

        const email = DomElements.Signup.getEmailValue();
        const password = DomElements.Signup.getPasswordValue();
        const passwordConfirm = DomElements.Signup.getPasswordConfirmValue();
        const nickname = DomElements.Signup.getNicknameValue();
        const profileImage = DomElements.Signup.getProfileImage()?.files?.[0];

        if (password !== passwordConfirm) {
            showError(ElementIds.SIGNUP_PASSWORD_CONFIRM_ERROR, '*비밀번호가 다릅니다.');
            return;
        }

        // 서버가 @RequestBody JSON을 받으므로 JSON 형태로 전송
        let profileImageBase64 = '';
        if (profileImage) {
            try {
                profileImageBase64 = await convertImageToBase64(profileImage);
            } catch (error) {
                showError(ElementIds.SIGNUP_PROFILE_ERROR, '*프로필 이미지 처리 중 오류가 발생했습니다.');
                return;
            }
        }

        try {
            const result = await UserApi.signup(email, password, nickname, profileImageBase64);
            const redirectUrl = result?.redirectUrl || result?.location;
            showSuccess('회원가입이 완료되었습니다.');
            window.location.href = redirectUrl || PageRoutes.USER_LOGIN;
        } catch (error) {
            const message = error?.message || '회원가입에 실패했습니다.';
            if (message.includes('이메일')) {
                showError(ElementIds.SIGNUP_EMAIL_ERROR, message);
            } else if (message.includes('닉네임')) {
                showError(ElementIds.SIGNUP_NICKNAME_ERROR, message);
            } else if (message.includes('비밀번호')) {
                showError(ElementIds.SIGNUP_PASSWORD_ERROR, message);
            } else {
                showErrorAlert(message);
            }
        }
    }

    /* -------------------------------------------------------------------------- */
    /* 공개 초기화 함수                                                              */
    /* -------------------------------------------------------------------------- */

    function initSignupPage() {
        // 상태 초기화
        hasProfileImage = false;
        validationState = {
            email: false,
            password: false,
            passwordConfirm: false,
            nickname: false,
        };

        const profileInput = DomElements.Signup.getProfileImage();
        profileInput?.addEventListener('change', handleProfileImageChange);

        const email = DomElements.Signup.getEmail();
        const password = DomElements.Signup.getPassword();
        const passwordConfirm = DomElements.Signup.getPasswordConfirm();
        const nickname = DomElements.Signup.getNickname();

        // 디바운스가 적용된 이메일 중복 검사
        const debouncedEmailCheck = debounce(async () => {
            await validateEmailField();
        }, 500);

        email?.addEventListener('input', () => {
            clearError(ElementIds.SIGNUP_EMAIL_ERROR);
            validationState.email = false;
            updateSignupButtonState();
            debouncedEmailCheck();
        });
        email?.addEventListener('blur', validateEmailField);

        // 비밀번호 유효성 검사
        password?.addEventListener('input', () => {
            clearError(ElementIds.SIGNUP_PASSWORD_ERROR);
            validationState.password = false;
            updateSignupButtonState();
        });
        password?.addEventListener('blur', validatePasswordField);

        // 비밀번호 확인 유효성 검사
        passwordConfirm?.addEventListener('input', () => {
            clearError(ElementIds.SIGNUP_PASSWORD_CONFIRM_ERROR);
            validationState.passwordConfirm = false;
            updateSignupButtonState();
        });
        passwordConfirm?.addEventListener('blur', validatePasswordConfirmField);

        // 디바운스가 적용된 닉네임 중복 검사
        const debouncedNicknameCheck = debounce(async () => {
            await validateNicknameField();
        }, 500);

        nickname?.addEventListener('input', () => {
            clearError(ElementIds.SIGNUP_NICKNAME_ERROR);
            validationState.nickname = false;
            updateSignupButtonState();
            debouncedNicknameCheck();
        });
        nickname?.addEventListener('blur', validateNicknameField);

        const signupForm = DomElements.Signup.getForm();
        signupForm?.addEventListener('submit', handleSignup);

        // 초기 유효성 검사 상태
        updateSignupButtonState();
    }

    // 공개 API
    return {
        initSignupPage
    };
})();

// 공개 함수 내보내기
export const { initSignupPage } = createSignupHandlerModule;
