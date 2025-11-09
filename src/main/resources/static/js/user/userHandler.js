import {
    clearAllErrors,
    showError,
    showSuccess,
    showErrorAlert,
    showModal,
    toggleUserMenu,
    previewProfileImage,
    clearError,
    showToast,
} from '../common/event.js';
import { UserApi } from '../api/userApi.js';
import { PageRoutes } from '../common/uris.js';
import { DomElements, ElementIds } from '../common/domElements.js';

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

// Debounce helper function
function debounce(func, delay) {
    let timeoutId;
    return function (...args) {
        clearTimeout(timeoutId);
        timeoutId = setTimeout(() => func.apply(this, args), delay);
    };
}

/* -------------------------------------------------------------------------- */
/* Validation                                                                 */
/* -------------------------------------------------------------------------- */

async function validateEmailField() {
    const email = DomElements.Signup.getEmailValue();

    if (!email || email.trim().length === 0) {
        showError('emailError', '*이메일을 입력해주세요.');
        validationState.email = false;
        updateSignupButtonState();
        return false;
    }

    const emailRegex = /^[\w-.]+@([\w-]+\.)+[\w-]{2,4}$/;
    if (!emailRegex.test(email)) {
        showError('emailError', '*올바른 이메일 주소 형식을 입력해주세요. (예: example@example.com)');
        validationState.email = false;
        updateSignupButtonState();
        return false;
    }

    // Check email duplication
    try {
        const exists = await UserApi.checkEmailExists(email);
        if (exists) {
            showError('emailError', '*중복된 이메일입니다.');
            validationState.email = false;
            updateSignupButtonState();
            return false;
        }
    } catch (error) {
        console.error('Email check failed:', error);
    }

    clearError('emailError');
    validationState.email = true;
    updateSignupButtonState();
    return true;
}

async function validatePasswordField() {
    const password = DomElements.Signup.getPasswordValue();
    const passwordConfirm = DomElements.Signup.getPasswordConfirmValue();

    if (!password || password.trim().length === 0) {
        showError('passwordError', '*비밀번호를 입력해주세요');
        validationState.password = false;
        updateSignupButtonState();
        return false;
    }

    const passwordRegExp = /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[!@#$%^&*(),.?":{}|<>]).{8,20}$/;
    if (!passwordRegExp.test(password)) {
        showError('passwordError', '*비밀번호는 8자 이상, 20자 이하이며, 대문자, 소문자, 숫자, 특수문자를 각각 최소 1개 포함해야 합니다.');
        validationState.password = false;
        updateSignupButtonState();
        return false;
    }

    if (passwordConfirm && password !== passwordConfirm) {
        showError('passwordError', '*비밀번호가 다릅니다.');
        validationState.password = false;
        updateSignupButtonState();
        return false;
    }

    clearError('passwordError');
    validationState.password = true;
    updateSignupButtonState();
    return true;
}

async function validatePasswordConfirmField() {
    const password = DomElements.Signup.getPasswordValue();
    const passwordConfirm = DomElements.Signup.getPasswordConfirmValue();

    if (!passwordConfirm || passwordConfirm.trim().length === 0) {
        showError('passwordConfirmError', '*비밀번호를 한번더 입력해주세요.');
        validationState.passwordConfirm = false;
        updateSignupButtonState();
        return false;
    }

    if (password !== passwordConfirm) {
        showError('passwordConfirmError', '*비밀번호가 다릅니다.');
        validationState.passwordConfirm = false;
        updateSignupButtonState();
        return false;
    }

    clearError('passwordConfirmError');
    validationState.passwordConfirm = true;
    updateSignupButtonState();
    return true;
}

async function validateNicknameField() {
    const nickname = DomElements.Signup.getNicknameValue();

    if (!nickname || nickname.trim().length === 0) {
        showError('nicknameError', '*닉네임을 입력해주세요.');
        validationState.nickname = false;
        updateSignupButtonState();
        return false;
    }

    if (/\s/.test(nickname)) {
        showError('nicknameError', '*띄어쓰기를 없애주세요');
        validationState.nickname = false;
        updateSignupButtonState();
        return false;
    }

    if (nickname.length > 10) {
        showError('nicknameError', '*닉네임은 최대 10자 까지 가능합니다.');
        validationState.nickname = false;
        updateSignupButtonState();
        return false;
    }

    // Check nickname duplication
    try {
        const exists = await UserApi.checkNicknameExists(nickname);
        if (exists) {
            showError('nicknameError', '*중복된 닉네임입니다.');
            validationState.nickname = false;
            updateSignupButtonState();
            return false;
        }
    } catch (error) {
        console.error('Nickname check failed:', error);
    }

    clearError('nicknameError');
    validationState.nickname = true;
    updateSignupButtonState();
    return true;
}

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
/* Login                                                                      */
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
        showError('passwordError', message.startsWith('*') ? message : `*${message}`);
    }
}

export function initLoginPage() {
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

/* -------------------------------------------------------------------------- */
/* Signup                                                                     */
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
        showError('passwordConfirmError', '*비밀번호가 다릅니다.');
        return;
    }

    // 서버가 @RequestBody JSON을 받으므로 JSON 형태로 전송
    let profileImageBase64 = '';
    if (profileImage) {
        try {
            profileImageBase64 = await convertImageToBase64User(profileImage);
        } catch (error) {
            showError('profileError', '*프로필 이미지 처리 중 오류가 발생했습니다.');
            return;
        }
    }

    const requestBody = {
        email,
        password,
        nickname,
        profile_image_path: profileImageBase64
    };

    try {
        const result = await UserApi.signup(requestBody);
        const redirectUrl = result?.redirectUrl || result?.location;
        showSuccess('회원가입이 완료되었습니다.');
        window.location.href = redirectUrl || PageRoutes.USER_LOGIN;
    } catch (error) {
        const message = error?.message || '회원가입에 실패했습니다.';
        if (message.includes('이메일')) {
            showError('emailError', message);
        } else if (message.includes('닉네임')) {
            showError('nicknameError', message);
        } else if (message.includes('비밀번호')) {
            showError('passwordError', message);
        } else {
            showErrorAlert(message);
        }
    }
}

// 이미지를 Base64로 변환하는 헬퍼 함수
function convertImageToBase64User(file) {
    return new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.onload = () => resolve(reader.result);
        reader.onerror = reject;
        reader.readAsDataURL(file);
    });
}

export function initSignupPage() {
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

    // Email validation with debounced duplicate check
    const debouncedEmailCheck = debounce(async () => {
        await validateEmailField();
    }, 500);

    email?.addEventListener('input', () => {
        clearError('emailError');
        validationState.email = false;
        updateSignupButtonState();
        debouncedEmailCheck();
    });
    email?.addEventListener('blur', validateEmailField);

    // Password validation
    password?.addEventListener('input', () => {
        clearError('passwordError');
        validationState.password = false;
        updateSignupButtonState();
    });
    password?.addEventListener('blur', validatePasswordField);

    // Password confirm validation
    passwordConfirm?.addEventListener('input', () => {
        clearError('passwordConfirmError');
        validationState.passwordConfirm = false;
        updateSignupButtonState();
    });
    passwordConfirm?.addEventListener('blur', validatePasswordConfirmField);

    // Nickname validation with debounced duplicate check
    const debouncedNicknameCheck = debounce(async () => {
        await validateNicknameField();
    }, 500);

    nickname?.addEventListener('input', () => {
        clearError('nicknameError');
        validationState.nickname = false;
        updateSignupButtonState();
        debouncedNicknameCheck();
    });
    nickname?.addEventListener('blur', validateNicknameField);

    const signupForm = DomElements.Signup.getForm();
    signupForm?.addEventListener('submit', handleSignup);

    // Initial validation state
    updateSignupButtonState();
}

/* -------------------------------------------------------------------------- */
/* User profile / password                                                    */
/* -------------------------------------------------------------------------- */

async function loadUserInfo() {
    const response = await UserApi.getCurrentUser();
    const user = response?.data || response;
    const emailElement = DomElements.UserEdit.getEmail();
    const nicknameInput = DomElements.UserEdit.getNickname();
    const profilePreviewImg = DomElements.UserEdit.getProfilePreviewImg();

    if (emailElement) {
        emailElement.textContent = user.email || user.user_email || '';
    }

    if (nicknameInput) {
        nicknameInput.value = user.nickname || user.user_nickname || user.userNickname || '';
    }

    // Support multiple field name variations for profile image path
    const profileImageUrl = user.profile_image_path || user.profileImage || user.profileImagePath || user.profile_image || user.user_profile_image;
    if (profilePreviewImg && profileImageUrl) {
        profilePreviewImg.src = profileImageUrl;
        profilePreviewImg.style.display = 'block';
        const placeholder = DomElements.UserEdit.getProfileImagePlaceholder();
        if (placeholder) {
            placeholder.style.display = 'none';
        }
    }
}

async function handleUserEdit(event) {
    event.preventDefault();
    clearAllErrors();

    const nickname = DomElements.UserEdit.getNicknameValue();
    const profileImage = DomElements.UserEdit.getProfileImageInput()?.files?.[0];

    // Validate nickname
    if (!nickname || nickname.trim().length === 0) {
        showError('nicknameError', '*닉네임을 입력해주세요.');
        return;
    }

    if (nickname.length > 10) {
        showError('nicknameError', '*닉네임은 최대 10자 까지 작성 가능합니다.');
        return;
    }

    if (/\s/.test(nickname)) {
        showError('nicknameError', '*띄어쓰기를 없애주세요');
        return;
    }

    // Check nickname duplication
    try {
        const exists = await UserApi.checkNicknameExists(nickname);
        if (exists) {
            showError('nicknameError', '*중복된 닉네임입니다.');
            return;
        }
    } catch (error) {
        console.error('Nickname check failed:', error);
    }

    // 서버가 @RequestBody JSON을 받으므로 JSON 형태로 전송
    const requestBody = {
        nickname
    };

    if (profileImage) {
        try {
            const base64Image = await convertImageToBase64User(profileImage);
            requestBody.profile_image_path = base64Image;
        } catch (error) {
            showError('nicknameError', '프로필 이미지 처리 중 오류가 발생했습니다.');
            return;
        }
    }

    try {
        await UserApi.updateProfile(requestBody);
        showToast('수정 완료');
        setTimeout(() => {
            window.location.reload();
        }, 2500);
    } catch (error) {
        showError('nicknameError', error.message);
    }
}

async function handlePasswordUpdate(event) {
    event.preventDefault();
    clearAllErrors();

    const currentPassword = DomElements.Password.getCurrentPasswordValue();
    const newPassword = DomElements.Password.getNewPasswordValue();

    try {
        await UserApi.updatePassword(currentPassword, newPassword);
        showSuccess('비밀번호가 수정되었습니다.');
        window.location.href = PageRoutes.ARTICLES;
    } catch (error) {
        showError('currentPasswordError', error.message);
    }
}

function showDeleteConfirmModal() {
    showModal(
        '회원탈퇴 하시겠습니까?',
        '작성된 게시글과 댓글은 삭제됩니다.',
        async () => {
            try {
                await UserApi.deleteAccount();
                showSuccess('회원탈퇴가 완료되었습니다.');
                window.location.href = PageRoutes.USER_LOGIN;
            } catch (error) {
                showErrorAlert(error.message);
            }
        }
    );
}

export async function initUserEditPage() {
    await loadUserInfo();

    const profileImageClickArea = DomElements.UserEdit.getProfileImageClickArea();
    const profileImageInput = DomElements.UserEdit.getProfileImageInput();
    if (profileImageClickArea && profileImageInput) {
        profileImageClickArea.style.cursor = 'pointer';

        // Add hover effect
        profileImageClickArea.addEventListener('mouseenter', () => {
            profileImageClickArea.style.backgroundColor = '#E9E9E9';
        });
        profileImageClickArea.addEventListener('mouseleave', () => {
            profileImageClickArea.style.backgroundColor = '';
        });

        profileImageClickArea.addEventListener('click', () => profileImageInput.click());
        profileImageInput.addEventListener('change', previewProfileImage);
    }

    const userEditForm = DomElements.UserEdit.getForm();
    userEditForm?.addEventListener('submit', handleUserEdit);

    const deleteAccountBtn = DomElements.UserEdit.getDeleteAccountBtn();
    deleteAccountBtn?.addEventListener('click', showDeleteConfirmModal);
}

export function initPasswordPage() {
    const passwordForm = DomElements.Password.getForm();
    passwordForm?.addEventListener('submit', handlePasswordUpdate);
}

/* -------------------------------------------------------------------------- */
/* Auth header                                                                */
/* -------------------------------------------------------------------------- */

export async function handleLogout(event) {
    event?.preventDefault();
    try {
        const result = await UserApi.logout();
        const redirectUrl = result?.redirectUrl || result?.location;
        window.location.href = redirectUrl || PageRoutes.USER_LOGIN;
    } catch (error) {
        showErrorAlert(error.message);
    }
}

export function initAuthHeader() {
    const userMenuBtn = DomElements.Header.getUserMenuBtn();
    userMenuBtn?.addEventListener('click', toggleUserMenu);

    const logoutBtn = DomElements.Header.getLogoutBtn();
    logoutBtn?.addEventListener('click', handleLogout);
}
