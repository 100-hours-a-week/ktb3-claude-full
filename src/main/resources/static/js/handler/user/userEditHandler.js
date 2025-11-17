import {
    clearAllErrors,
    showError,
    showSuccess,
    showErrorAlert,
    showModal,
    toggleUserMenu,
    previewProfileImage,
    showToast,
} from '/js/common/event.js';
import { UserApi } from '/js/api/userApi.js';
import { PageRoutes } from '/js/common/uris.js';
import { DomElements, ElementIds } from '/js/common/domElements.js';

/**
 * 사용자 편집 핸들러
 * 사용자 프로필 편집, 비밀번호 수정, 계정 삭제 및 인증 헤더 처리
 * 클로저 패턴을 사용하여 모듈 상태 캡슐화
 */

const createUserEditHandlerModule = (() => {
    /* -------------------------------------------------------------------------- */
    /* Helpers                                                                    */
    /* -------------------------------------------------------------------------- */

    function convertImageToBase64(file) {
        return new Promise((resolve, reject) => {
            const reader = new FileReader();
            reader.onload = () => resolve(reader.result);
            reader.onerror = reject;
            reader.readAsDataURL(file);
        });
    }

    /* -------------------------------------------------------------------------- */
    /* User profile                                                               */
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
            nicknameInput.value = user.nickname || user.user_nickname || user.nickName || '';
        }

        // 프로필 이미지 경로의 다양한 필드명 지원
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

        // 닉네임 유효성 검사
        if (!nickname || nickname.trim().length === 0) {
            showError(ElementIds.USER_NICKNAME_ERROR, '*닉네임을 입력해주세요.');
            return;
        }

        if (nickname.length > 10) {
            showError(ElementIds.USER_NICKNAME_ERROR, '*닉네임은 최대 10자 까지 작성 가능합니다.');
            return;
        }

        if (/\s/.test(nickname)) {
            showError(ElementIds.USER_NICKNAME_ERROR, '*띄어쓰기를 없애주세요');
            return;
        }

        // 닉네임 중복 확인
        try {
            const exists = await UserApi.checkNicknameExists(nickname);
            if (exists) {
                showError(ElementIds.USER_NICKNAME_ERROR, '*중복된 닉네임입니다.');
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
                const base64Image = await convertImageToBase64(profileImage);
                requestBody.profile_image_path = base64Image;
            } catch (error) {
                showError(ElementIds.USER_NICKNAME_ERROR, '프로필 이미지 처리 중 오류가 발생했습니다.');
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
            showError(ElementIds.USER_NICKNAME_ERROR, error.message);
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

    /* -------------------------------------------------------------------------- */
    /* Password update                                                            */
    /* -------------------------------------------------------------------------- */

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
            showError(ElementIds.PASSWORD_CURRENT_ERROR, error.message);
        }
    }

    /* -------------------------------------------------------------------------- */
    /* Auth header                                                                */
    /* -------------------------------------------------------------------------- */

    async function handleLogout(event) {
        event?.preventDefault();
        try {
            const result = await UserApi.logout();
            const redirectUrl = result?.redirectUrl || result?.location;
            window.location.href = redirectUrl || PageRoutes.USER_LOGIN;
        } catch (error) {
            showErrorAlert(error.message);
        }
    }

    function initAuthHeader() {
        const userMenuBtn = DomElements.Header.getUserMenuBtn();
        userMenuBtn?.addEventListener('click', toggleUserMenu);

        const logoutBtn = DomElements.Header.getLogoutBtn();
        logoutBtn?.addEventListener('click', handleLogout);
    }

    /* -------------------------------------------------------------------------- */
    /* Public initializers                                                        */
    /* -------------------------------------------------------------------------- */

    async function initUserEditPage() {
        await loadUserInfo();

        const profileImageClickArea = DomElements.UserEdit.getProfileImageClickArea();
        const profileImageInput = DomElements.UserEdit.getProfileImageInput();
        if (profileImageClickArea && profileImageInput) {
            profileImageClickArea.style.cursor = 'pointer';

            // 호버 효과 추가
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

    function initPasswordPage() {
        const passwordForm = DomElements.Password.getForm();
        passwordForm?.addEventListener('submit', handlePasswordUpdate);
    }

    // 공개 API
    return {
        initUserEditPage,
        initPasswordPage,
        initAuthHeader,
        handleLogout
    };
})();

// 공개 함수 내보내기
export const {
    initUserEditPage,
    initPasswordPage,
    initAuthHeader,
    handleLogout
} = createUserEditHandlerModule;
