import { formatDate, showErrorAlert, eventManager } from '/js/common/event.js';
import { ArticleApi } from '/js/api/articleApi.js';
import { PageRoutes } from '/js/common/uris.js';
import { DomElements } from '/js/common/domElements.js';

/**
 * Article List Handler
 * 커서 기반 페이지네이션을 사용한 게시글 목록 표시 및 탐색 처리
 */

const createListHandlerModule = (() => {
    /* -------------------------------------------------------------------------- */
    /* 상태 관리                                                                   */
    /* -------------------------------------------------------------------------- */

    let state = {
        endCursor: 0,           // 현재 커서 위치
        hasNext: true,          // 더 불러올 게시글 존재 여부
        isLoading: false,       // 중복 요청 방지
        pageSize: 10            // 페이지당 게시글 개수
    };

    /* -------------------------------------------------------------------------- */
    /* 헬퍼 함수                                                                   */
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
     * 스로틀 함수 - 지정된 시간 간격당 최대 한 번만 실행되도록 제한
     * @param {Function} func - 스로틀을 적용할 함수
     * @param {number} delay - 지연 시간 (밀리초)
     * @returns {Function} 스로틀이 적용된 함수
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
                // 남은 지연 시간 후 함수가 호출되도록 예약
                clearTimeout(timeoutId);
                timeoutId = setTimeout(() => {
                    lastCall = Date.now();
                    func.apply(this, args);
                }, delay - timeSinceLastCall);
            }
        };
    }

    /* -------------------------------------------------------------------------- */
    /* 게시글 목록                                                                 */
    /* -------------------------------------------------------------------------- */

    async function loadArticleList(append = false) {
        // 중복 요청 방지
        if (state.isLoading) return;

        // 더 불러올 게시글이 없으면 중단
        if (!state.hasNext && append) return;

        try {
            state.isLoading = true;

            const response = await ArticleApi.getArticles(state.endCursor, state.pageSize);

            // 응답에서 데이터와 페이지 정보 추출
            const articles = response?.data || [];
            const pageInfo = response?.pageInfo || { hasNext: false, endCursor: 0 };

            // 새로운 커서와 다음 페이지 존재 여부로 상태 업데이트
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

        // 초기 로드 시에만 이벤트 위임 리스너 등록 (1회만)
        if (!append && !container.dataset.delegated) {
            container.addEventListener('click', (e) => {
                const card = e.target.closest('.article-card');
                if (card && card.dataset.articleId) {
                    location.href = PageRoutes.articleDetail(card.dataset.articleId);
                }
            });
            container.dataset.delegated = 'true';
        }

        // 초기 로드 시에만 컨테이너 초기화 (append 모드가 아닐 때)
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
            card.dataset.articleId = article.id;

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
    /* 무한 스크롤                                                                 */
    /* -------------------------------------------------------------------------- */

    /**
     * 사용자가 페이지 하단 근처까지 스크롤했는지 확인
     * @param {number} threshold - 하단으로부터의 거리 (픽셀)
     * @returns {boolean} 하단 근처이면 true
     */
    function isNearBottom(threshold = 300) {
        const scrollTop = document.documentElement.scrollTop;
        const windowHeight = window.innerHeight;
        const documentHeight = document.documentElement.scrollHeight;

        return scrollTop + windowHeight >= documentHeight - threshold;
    }

    /**
     * 무한 로딩을 위한 스크롤 이벤트 핸들러
     * 과도한 API 호출을 방지하기 위해 스로틀 적용
     */
    const handleScroll = throttle(async () => {
        if (isNearBottom() && state.hasNext && !state.isLoading) {
            await loadArticleList(true);
        }
    }, 200); // 200ms마다 최대 1회 실행으로 제한

    /**
     * 무한 스크롤 리스너 초기화
     */
    function initInfiniteScroll() {
        window.addEventListener('scroll', handleScroll);
    }

    /**
     * 무한 스크롤 리스너 정리 (메모리 누수 방지)
     */
    function cleanupInfiniteScroll() {
        window.removeEventListener('scroll', handleScroll);
    }

    /* -------------------------------------------------------------------------- */
    /* 공개 초기화 함수                                                             */
    /* -------------------------------------------------------------------------- */

    function initArticleListPage() {
        // 새로운 페이지 로드를 위해 상태 초기화
        state.endCursor = 0;
        state.hasNext = true;
        state.isLoading = false;

        // 초기 게시글 목록 로드
        loadArticleList(false).catch(() => {
            const message = '게시글 목록을 가져오는 중 오류가 발생했습니다.';
            showErrorAlert(message);
        });

        // 무한 스크롤 초기화
        initInfiniteScroll();
    }

    // 공개 API
    return {
        initArticleListPage,
        cleanupInfiniteScroll
    };
})();

// 공개 함수 내보내기
export const { initArticleListPage, cleanupInfiniteScroll } = createListHandlerModule;
