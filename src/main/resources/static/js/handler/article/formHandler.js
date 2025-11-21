import {
    clearAllErrors,
    previewArticleImage,
    showError,
    showErrorAlert,
    showToast,
} from '/js/common/event/event.js';
import { ArticleApi } from '/js/api/articleApi.js';
import { PageRoutes } from '/js/common/uris.js';
import { DomElements, ElementIds } from '/js/common/domElements.js';

/**
 * 게시글 폼 핸들러
 * 게시글 생성 및 수정 처리
 */

const createFormHandlerModule = (() => {
    /* -------------------------------------------------------------------------- */
    /* Article form validation                                                    */
    /* -------------------------------------------------------------------------- */

    function validateArticleForm() {
        const title = DomElements.ArticleForm.getTitleValue();
        const content = DomElements.ArticleForm.getContentValue();
        const submitBtn = DomElements.ArticleForm.getSubmitBtn();
        const titleError = DomElements.ArticleForm.getTitleError();
        const contentError = DomElements.ArticleForm.getContentError();

        const isTitleValid = title && title.trim().length > 0;
        const isContentValid = content && content.trim().length > 0;
        const isFormValid = isTitleValid && isContentValid;

        // 오류 메시지 표시/숨김
        if (titleError && contentError) {
            if (!isFormValid && (!isTitleValid || !isContentValid)) {
                if (!isTitleValid && !isContentValid) {
                    titleError.textContent = '*제목, 내용을 모두 작성해주세요.';
                    titleError.style.display = 'block';
                    contentError.style.display = 'none';
                } else if (!isTitleValid) {
                    titleError.textContent = '*제목을 작성해주세요.';
                    titleError.style.display = 'block';
                    contentError.style.display = 'none';
                } else if (!isContentValid) {
                    contentError.textContent = '*내용을 작성해주세요.';
                    contentError.style.display = 'block';
                    titleError.style.display = 'none';
                }
            } else {
                titleError.style.display = 'none';
                contentError.style.display = 'none';
            }
        }

        // 버튼 상태 업데이트
        if (submitBtn) {
            submitBtn.disabled = !isFormValid;
            if (isFormValid) {
                submitBtn.style.backgroundColor = '#7F6AEE';
            } else {
                submitBtn.style.backgroundColor = '#ACA0EB';
            }
        }

        return isFormValid;
    }

    /* -------------------------------------------------------------------------- */
    /* Article CRUD (create/update)                                               */
    /* -------------------------------------------------------------------------- */

    async function loadArticleForEdit(id) {
        try {
            const articleResponse = await ArticleApi.getArticle(id);
            const article = articleResponse?.data || articleResponse;

            DomElements.manager.setText(ElementIds.ARTICLE_FORM_TITLE, '게시글 수정');
            DomElements.manager.setText(ElementIds.ARTICLE_SUBMIT_BTN, '수정하기');

            DomElements.manager.setValue(ElementIds.ARTICLE_ID, article.id);
            DomElements.manager.setValue(ElementIds.ARTICLE_TITLE, article.title);
            DomElements.manager.setValue(ElementIds.ARTICLE_CONTENT_INPUT, article.content);

            if (article.article_image_path || article.imageUrl) {
                DomElements.manager.setText(ElementIds.ARTICLE_FILE_NAME_TEXT, '기존 파일');
                const previewImg = DomElements.ArticleForm.getPreviewImg();
                const imagePreview = DomElements.ArticleForm.getImagePreview();
                if (previewImg && imagePreview) {
                    previewImg.src = article.image_url || article.imageUrl;
                    imagePreview.style.display = 'block';
                }
            }
        } catch (error) {
            console.error('Failed to load article for edit:', error);
            showErrorAlert('게시글을 불러오지 못했습니다.');
        }
    }

    async function handleArticleSubmit(event) {
        event.preventDefault();
        clearAllErrors();

        const articleIdInput = DomElements.ArticleForm.getArticleId();
        const articleIdValue = articleIdInput ? articleIdInput.value : '';
        const title = DomElements.ArticleForm.getTitleValue();
        const content = DomElements.ArticleForm.getContentValue();
        const imageInput = DomElements.ArticleForm.getImageInput();
        const image = imageInput?.files?.[0];

        // 서버가 @RequestBody JSON을 받으므로 JSON 형태로 전송
        const requestBody = {
            title,
            content,
            article_image_path: ''
        };

        // 이미지가 있으면 base64로 인코딩하여 전송
        if (image) {
            try {
                const base64Image = await convertImageToBase64(image);
                requestBody.article_image_path = base64Image;
            } catch (error) {
                showError(ElementIds.ARTICLE_IMAGE_ERROR, '이미지 처리 중 오류가 발생했습니다.');
                return;
            }
        }

        try {
            if (articleIdValue) {
                const updateResult = await ArticleApi.updateArticle(articleIdValue, requestBody);
                const redirectUrl = updateResult?.redirectUrl;
                showToast('게시글이 수정되었습니다.');
                setTimeout(() => {
                    window.location.href = redirectUrl || PageRoutes.articleDetail(articleIdValue);
                }, 500);
            } else {
                const result = await ArticleApi.createArticle(requestBody);
                const redirectUrl = result?.redirectUrl;
                showToast('게시글이 작성되었습니다.');
                setTimeout(() => {
                    if (redirectUrl) {
                        window.location.href = redirectUrl;
                    } else {
                        const redirectId = result?.id;
                        window.location.href = redirectId ? PageRoutes.articleDetail(redirectId) : PageRoutes.ARTICLES;
                    }
                }, 500);
            }
        } catch (error) {
            showError(ElementIds.ARTICLE_TITLE_ERROR, error.message);
        }
    }

    // 이미지를 Base64로 변환하는 헬퍼 함수
    function convertImageToBase64(file) {
        return new Promise((resolve, reject) => {
            const reader = new FileReader();
            reader.onload = () => resolve(reader.result);
            reader.onerror = reject;
            reader.readAsDataURL(file);
        });
    }

    /* -------------------------------------------------------------------------- */
    /* Public initializer                                                         */
    /* -------------------------------------------------------------------------- */

    async function initArticleFormPage(articleIdParam) {
        const hiddenIdField = DomElements.ArticleForm.getArticleId();
        if (articleIdParam && hiddenIdField) {
            hiddenIdField.value = articleIdParam;
        }

        const fileUploadBtn = DomElements.ArticleForm.getFileUploadBtn();
        const imageInput = DomElements.ArticleForm.getImageInput();
        fileUploadBtn?.addEventListener('click', () => imageInput?.click());
        imageInput?.addEventListener('change', previewArticleImage);

        if (hiddenIdField?.value) {
            await loadArticleForEdit(hiddenIdField.value);
        }

        // 유효성 검사 이벤트 리스너 추가
        const titleInput = DomElements.ArticleForm.getTitle();
        const contentInput = DomElements.ArticleForm.getContent();

        titleInput?.addEventListener('input', validateArticleForm);
        contentInput?.addEventListener('input', validateArticleForm);

        const articleForm = DomElements.ArticleForm.getForm();
        articleForm?.addEventListener('submit', handleArticleSubmit);

        // 초기 유효성 검사
        validateArticleForm();
    }

    // 공개 API
    return {
        initArticleFormPage
    };
})();

// 공개 함수 내보내기
export const { initArticleFormPage } = createFormHandlerModule;
