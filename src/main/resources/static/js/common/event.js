// Common event handlers and utility functions

export function toggleUserMenu() {
    const dropdown = document.getElementById('userMenuDropdown');
    if (dropdown) {
        dropdown.style.display = dropdown.style.display === 'none' ? 'block' : 'none';
    }
}

// Close dropdown when clicking outside
document.addEventListener('click', function(event) {
    const userMenu = document.querySelector('.user-menu');
    const dropdown = document.getElementById('userMenuDropdown');

    if (dropdown && userMenu && !userMenu.contains(event.target)) {
        dropdown.style.display = 'none';
    }
});

export function showModal(title, message, onConfirm) {
    const modal = document.getElementById('modal');
    if (!modal) {
        // Create modal if it doesn't exist
        const modalHTML = `
            <div id="modal" class="modal">
                <div class="modal-content">
                    <h3 id="modalTitle"></h3>
                    <p id="modalMessage"></p>
                    <div class="modal-buttons">
                        <button class="btn btn-secondary" id="modalCancel">취소</button>
                        <button class="btn btn-primary" id="modalConfirm">확인</button>
                    </div>
                </div>
            </div>
        `;
        document.body.insertAdjacentHTML('beforeend', modalHTML);
    }

    const modalElement = document.getElementById('modal');
    const modalTitle = document.getElementById('modalTitle');
    const modalMessage = document.getElementById('modalMessage');
    const modalCancel = document.getElementById('modalCancel');
    const modalConfirm = document.getElementById('modalConfirm');

    modalTitle.textContent = title;
    modalMessage.textContent = message;
    modalElement.style.display = 'flex';
    if (!document.body.dataset.prevOverflow) {
        document.body.dataset.prevOverflow = document.body.style.overflow || '';
    }
    document.body.style.overflow = 'hidden';

    // Remove existing event listeners by cloning
    const newModalCancel = modalCancel.cloneNode(true);
    modalCancel.parentNode.replaceChild(newModalCancel, modalCancel);
    const newModalConfirm = modalConfirm.cloneNode(true);
    modalConfirm.parentNode.replaceChild(newModalConfirm, modalConfirm);

    newModalCancel.addEventListener('click', hideModal);

    newModalConfirm.addEventListener('click', function() {
        modalElement.style.display = 'none';
        if (onConfirm) onConfirm();
    });
}

// Hide modal
export function hideModal() {
    const modal = document.getElementById('modal');
    if (modal) {
        modal.style.display = 'none';
    }
    if (document.body.dataset.prevOverflow !== undefined) {
        document.body.style.overflow = document.body.dataset.prevOverflow;
        delete document.body.dataset.prevOverflow;
    }
}

// Show error message in form
export function showError(elementId, message) {
    const element = document.getElementById(elementId);
    if (element) {
        element.textContent = message;
        element.style.display = 'block';
    }
}

// Clear error message
export function clearError(elementId) {
    const element = document.getElementById(elementId);
    if (element) {
        element.textContent = '';
        element.style.display = 'none';
    }
}

// Clear all errors
export function clearAllErrors() {
    const errors = document.querySelectorAll('.form-helper');
    errors.forEach(error => {
        error.textContent = '';
        error.style.display = 'none';
    });
}

// Show success message (using browser alert for now)
export function showSuccess(message) {
    alert(message);
}

// Show error message (using browser alert for now)
export function showErrorAlert(message) {
    alert(message);
}

// Format date
export function formatDate(dateString) {
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');
    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
}

// Preview profile image
export function previewProfileImage(event) {
    const file = event.target.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = function(e) {
            const preview = document.getElementById('previewImg') || document.getElementById('profilePreviewImg');
            if (preview) {
                preview.src = e.target.result;
                preview.style.display = 'block';
            }
        };
        reader.readAsDataURL(file);
    }
}

// Preview article image
export function previewArticleImage(event) {
    const file = event.target.files[0];
    const fileNameText = document.getElementById('fileNameText');
    const imagePreview = document.getElementById('imagePreview');
    const previewImg = document.getElementById('previewImg');

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
