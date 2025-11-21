// 이미지 미리보기 유틸리티

import { DomElements } from '/js/common/ui/domElements.js';

/**
 * 프로필 이미지 미리보기
 * @param {Event} event - input change 이벤트
 */
export function previewProfileImage(event) {
    const file = event.target.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = function(e) {
            const preview = DomElements.Signup.getPreviewImg() || DomElements.UserEdit.getProfilePreviewImg();
            if (preview) {
                preview.src = e.target.result;
                preview.style.display = 'block';
            }
        };
        reader.readAsDataURL(file);
    }
}

/**
 * 게시글 이미지 미리보기
 * @param {Event} event - input change 이벤트
 */
export function previewArticleImage(event) {
    const file = event.target.files[0];
    const fileNameText = DomElements.ArticleForm.getFileNameText();
    const imagePreview = DomElements.ArticleForm.getImagePreview();
    const previewImg = DomElements.ArticleForm.getPreviewImg();

    if (file) {
        if (fileNameText) {
            fileNameText.textContent = file.name;
        }

        const reader = new FileReader();
        reader.onload = function(e) {
            if (previewImg) {
                previewImg.src = e.target.result;
            }
            if (imagePreview) {
                imagePreview.style.display = 'block';
            }
        };
        reader.readAsDataURL(file);
    }
}
