import {
    clearAllErrors,
    showError,
    showSuccess,
    showErrorAlert,
    showModal,
    toggleUserMenu,
    previewProfileImage,
} from '../common/event.js';
import { UserApi } from '../api/userApi.js';
import { PageRoutes } from '../common/uris.js';
import { DomElements } from '../common/domElements.js';

let hasProfileImage = false;

/* -------------------------------------------------------------------------- */
/* Validation                                                                 */
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

function validateSignupForm() {
    const signupBtn = DomElements.Signup.getSignupBtn();
    const profileError = DomElements.Signup.getProfileError();

    if (profileError) {
        profileError.textContent = hasProfileImage ? '' : '**프로필 사진을 추가해주세요.';
        profileError.style.display = hasProfileImage ? 'none' : 'block';
    }

    const email = DomElements.Signup.getEmailValue();
    const password = DomElements.Signup.getPasswordValue();
    const passwordConfirm = DomElements.Signup.getPasswordConfirmValue();
    const nickname = DomElements.Signup.getNicknameValue();

    const isProfileValid = hasProfileImage;
    const isEmailValid = validateEmail(email).valid;
    const isPasswordValid = validatePassword(password).valid;
    const isPasswordConfirmValid = passwordConfirm === password && passwordConfirm.length > 0;
    const isNicknameValid = nickname.trim().length > 0 && nickname.length <= 10 && !/\s/.test(nickname);

    const isFormValid =
        isProfileValid && isEmailValid && isPasswordValid && isPasswordConfirmValid && isNicknameValid;

    if (signupBtn) {
        signupBtn.disabled = !isFormValid;
        signupBtn.classList.toggle('btn-signup-enabled', isFormValid);
        signupBtn.classList.toggle('btn-signup-disabled', !isFormValid);
    }

    return isFormValid;
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
    const signupBtn = DomElements.manager.get('signupBtn');
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
    const profileError = DomElements.Signup.getProfileError();

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

    if (profileError) {
        profileError.textContent = hasProfileImage ? '' : '**프로필 사진을 추가해주세요.';
        profileError.style.display = hasProfileImage ? 'none' : 'block';
    }

    validateSignupForm();
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

    const formData = new FormData();
    formData.append('email', email);
    formData.append('password', password);
    formData.append('nickname', nickname);
    if (profileImage) {
        formData.append('profileImage', profileImage);
    }

    try {
        const result = await UserApi.signup(formData);
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

export function initSignupPage() {
    hasProfileImage = false;
    const profileInput = DomElements.Signup.getProfileImage();
    profileInput?.addEventListener('change', handleProfileImageChange);

    const email = DomElements.Signup.getEmail();
    const password = DomElements.Signup.getPassword();
    const passwordConfirm = DomElements.Signup.getPasswordConfirm();
    const nickname = DomElements.Signup.getNickname();

    email?.addEventListener('input', validateSignupForm);
    email?.addEventListener('blur', validateSignupForm);
    password?.addEventListener('input', validateSignupForm);
    password?.addEventListener('blur', validateSignupForm);
    passwordConfirm?.addEventListener('input', validateSignupForm);
    passwordConfirm?.addEventListener('blur', validateSignupForm);
    nickname?.addEventListener('input', validateSignupForm);
    nickname?.addEventListener('blur', validateSignupForm);

    const signupForm = DomElements.Signup.getForm();
    signupForm?.addEventListener('submit', handleSignup);
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
        emailElement.textContent = user.email;
    }

    if (nicknameInput) {
        nicknameInput.value = user.nickname || '';
    }

    if (profilePreviewImg && user.profileImage) {
        profilePreviewImg.src = user.profileImage;
        profilePreviewImg.style.display = 'block';
        const placeholder = document.querySelector('.profile-image-placeholder');
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

    const formData = new FormData();
    formData.append('nickname', nickname);
    if (profileImage) {
        formData.append('profileImage', profileImage);
    }

    try {
        await UserApi.updateProfile(formData);
        showSuccess('회원정보가 수정되었습니다.');
        window.location.reload();
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
