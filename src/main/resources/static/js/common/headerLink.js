import { PageRoutes } from './uris.js';
import { DomElements } from './domElements.js';

const routeHandlers = {
    ARTICLES: () => (window.location.href = PageRoutes.ARTICLES),
    USER_EDIT: () => (window.location.href = PageRoutes.USER_EDIT),
    USER_PASSWORD: () => (window.location.href = PageRoutes.USER_PASSWORD),
    USER_LOGOUT: () => DomElements.Header.getLogoutBtn()?.click?.(),
    HEADER_BACK: () => window.history.back(),
};

function handleRoute(routeKey) {
    const handler = routeHandlers[routeKey];
    if (!handler) {
        console.warn(`Unknown header route: ${routeKey}`);
        return false;
    }
    handler();
    return true;
}

export function bind(root) {
    if (!root) return;

    root.addEventListener('click', event => {
        const target = event.target.closest('[data-route]');
        if (!target) return;

        if (event.metaKey || event.ctrlKey || event.shiftKey || event.altKey) {
            return; // 새 탭/창 허용
        }

        const routeKey = target.dataset.route;
        if (handleRoute(routeKey)) {
            event.preventDefault();
        }
    });
}