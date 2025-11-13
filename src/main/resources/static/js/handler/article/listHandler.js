import { formatDate, showErrorAlert } from '/js/common/event.js';
import { ArticleApi } from '/js/api/articleApi.js';
import { PageRoutes } from '/js/common/uris.js';
import { DomElements } from '/js/common/domElements.js';

/**
 * Article List Handler
 * Handles article list display and navigation with cursor-based pagination
 */

const createListHandlerModule = (() => {
    /* -------------------------------------------------------------------------- */
    /* State Management                                                           */
    /* -------------------------------------------------------------------------- */

    let state = {
        endCursor: 0,           // Current cursor position
        hasNext: true,          // Whether more articles exist
        isLoading: false,       // Prevent duplicate requests
        pageSize: 10            // Number of articles per page
    };

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

    /**
     * Throttle function - limits execution to once per delay period
     * @param {Function} func - Function to throttle
     * @param {number} delay - Delay in milliseconds
     * @returns {Function} Throttled function
     */
    function throttle(func, delay) {
        let lastCall = 0;
        let timeoutId = null;

        return function throttled(...args) {
            const now = Date.now();
            const timeSinceLastCall = now - lastCall;

            if (timeSinceLastCall >= delay) {
                lastCall = now;
                func.apply(this, args);
            } else {
                // Schedule the function to be called after the remaining delay
                clearTimeout(timeoutId);
                timeoutId = setTimeout(() => {
                    lastCall = Date.now();
                    func.apply(this, args);
                }, delay - timeSinceLastCall);
            }
        };
    }

    /* -------------------------------------------------------------------------- */
    /* Article list                                                               */
    /* -------------------------------------------------------------------------- */

    async function loadArticleList(append = false) {
        // Prevent duplicate requests
        if (state.isLoading) return;

        // No more articles to load
        if (!state.hasNext && append) return;

        try {
            state.isLoading = true;

            const response = await ArticleApi.getArticles(state.endCursor, state.pageSize);
            console.log('Article API response:', response);

            // Extract data and pageInfo from response
            const articles = response?.data || [];
            const pageInfo = response?.pageInfo || { hasNext: false, endCursor: 0 };

            // Update state with new cursor and hasNext flag
            state.hasNext = pageInfo.hasNext;
            state.endCursor = pageInfo.endCursor || 0;

            renderArticleList(articles, append);
        } catch (error) {
            console.error('Failed to load articles:', error);
            showErrorAlert('게시글을 불러오는데 실패했습니다.');
        } finally {
            state.isLoading = false;
        }
    }

    function renderArticleList(articles = [], append = false) {
        const container = DomElements.ArticleList.getContainer();
        if (!container) return;

        // Clear container only on initial load (not append)
        if (!append) {
            container.innerHTML = '';
        }

        if (!articles || articles.length === 0) {
            if (!append) {
                container.innerHTML = '<p style="text-align:center;color:#666;">게시글이 없습니다.</p>';
            }
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
    /* Infinite Scroll                                                            */
    /* -------------------------------------------------------------------------- */

    /**
     * Check if user has scrolled near bottom of page
     * @param {number} threshold - Distance from bottom in pixels
     * @returns {boolean} True if near bottom
     */
    function isNearBottom(threshold = 300) {
        const scrollTop = document.documentElement.scrollTop;
        const windowHeight = window.innerHeight;
        const documentHeight = document.documentElement.scrollHeight;

        return scrollTop + windowHeight >= documentHeight - threshold;
    }

    /**
     * Handle scroll event for infinite loading
     * Throttled to prevent excessive API calls
     */
    const handleScroll = throttle(async () => {
        if (isNearBottom() && state.hasNext && !state.isLoading) {
            console.log('Loading more articles... (cursor:', state.endCursor, ')');
            await loadArticleList(true);
        }
    }, 200); // Throttle to max once per 200ms

    /**
     * Initialize infinite scroll listener
     */
    function initInfiniteScroll() {
        window.addEventListener('scroll', handleScroll);
        console.log('Infinite scroll initialized');
    }

    /**
     * Cleanup infinite scroll listener
     */
    function cleanupInfiniteScroll() {
        window.removeEventListener('scroll', handleScroll);
        console.log('Infinite scroll cleaned up');
    }

    /* -------------------------------------------------------------------------- */
    /* Public initializer                                                         */
    /* -------------------------------------------------------------------------- */

    function initArticleListPage() {
        // Reset state for fresh page load
        state.endCursor = 0;
        state.hasNext = true;
        state.isLoading = false;

        // Load initial articles
        loadArticleList(false).catch(() => {
            const message = '게시글 목록을 가져오는 중 오류가 발생했습니다.';
            showErrorAlert(message);
        });

        // Initialize infinite scroll
        initInfiniteScroll();
    }

    // Public API
    return {
        initArticleListPage,
        cleanupInfiniteScroll
    };
})();

// Export the public functions
export const { initArticleListPage, cleanupInfiniteScroll } = createListHandlerModule;
