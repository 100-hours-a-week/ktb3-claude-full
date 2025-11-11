import { formatDate, showErrorAlert } from '../../common/event.js';
import { ArticleApi } from '../../api/articleApi.js';
import { PageRoutes } from '../../common/uris.js';
import { DomElements } from '../../common/domElements.js';

/**
 * Article List Handler
 * Handles article list display and navigation
 */

const createListHandlerModule = (() => {
    /* -------------------------------------------------------------------------- */
    /* Helpers                                                                    */
    /* -------------------------------------------------------------------------- */

    function parseNumber(value) {
        const num = Number(value);
        return Number.isNaN(num) ? 0 : num;
    }

    function escapeHtml(value) {
        if (value === undefined || value === null) return '';
        return String(value)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#39;');
    }

    function formatCompactNumber(value) {
        const num = parseNumber(value);
        if (num >= 100000) {
            return `${Math.floor(num / 1000)}k`;
        }
        if (num >= 10000) {
            return `${Math.floor(num / 1000)}k`;
        }
        if (num >= 1000) {
            return `${Math.floor(num / 1000)}k`;
        }
        return num.toString();
    }

    /* -------------------------------------------------------------------------- */
    /* Article list                                                               */
    /* -------------------------------------------------------------------------- */

    async function loadArticleList() {
        try {
            const response = await ArticleApi.getArticles();
            console.log('Article API response:', response);

            // Handle both response.data and direct array response
            const articles = response?.data || response;
            renderArticleList(articles);
        } catch (error) {
            console.error('Failed to load articles:', error);
            showErrorAlert('게시글을 불러오는데 실패했습니다.');
        }
    }

    function renderArticleList(articles = []) {
        const container = DomElements.ArticleList.getContainer();
        if (!container) return;
        container.innerHTML = '';

        if (!articles || articles.length === 0) {
            container.innerHTML = '<p style="text-align:center;color:#666;">게시글이 없습니다.</p>';
            return;
        }

        articles.forEach(article => {
            const card = document.createElement('div');
            card.className = 'article-card';
            card.addEventListener('click', () => location.href = PageRoutes.articleDetail(article.id));

            const likeCount = formatCompactNumber(article.like_cnt);
            const commentCount = formatCompactNumber(article.comment_cnt);
            const viewCount = formatCompactNumber(article.view_cnt);
            const createdAt = formatDate(article.last_modified_date);
            const author = escapeHtml(article.user_nickname || '익명');

            card.innerHTML = `
                <div class="article-card-header">
                    <div>
                        <h3 class="article-card-title">${escapeHtml(article.title)}</h3>
                        <div class="article-card-meta">
                            <span>좋아요 ${likeCount}</span>
                            <span>댓글 ${commentCount}</span>
                            <span>조회수 ${viewCount}</span>
                        </div>
                    </div>
                    <span class="article-card-date">${createdAt}</span>
                </div>
                <div class="article-card-author">
                    <div class="article-card-author-avatar"></div>
                    <span class="article-card-author-name">${author}</span>
                </div>
            `;

            container.appendChild(card);
        });
    }

    /* -------------------------------------------------------------------------- */
    /* Public initializer                                                         */
    /* -------------------------------------------------------------------------- */

    function initArticleListPage() {
        loadArticleList().catch(() => {
            const message = '게시글 목록을 가져오는 중 오류가 발생했습니다.';
            showErrorAlert(message);
        });
    }

    // Public API
    return {
        initArticleListPage
    };
})();

// Export the public function
export const { initArticleListPage } = createListHandlerModule;
