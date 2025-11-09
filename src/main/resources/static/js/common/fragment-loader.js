import { PageRoutes } from './uris.js';
import { bind as HeaderLinkManager } from './headerLink.js';

// Fragment loader utility
async function loadFragment(url, targetId) {
    try {
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error(`Failed to load fragment: ${url}`);
        }
        const html = await response.text();
        const target = document.getElementById(targetId);
        if (target) {
            target.innerHTML = html;
            // Fragment 로드 후 URI 설정
            initFragmentUris(target);
            bindHeaderLinks(target);
        }
    } catch (error) {
        console.error('Error loading fragment:', error);
    }
}

// Fragment 내의 data-route 속성을 실제 href로 변환
function initFragmentUris(container) {
    const elements = container.querySelectorAll('[data-route]');
    const routes = PageRoutes || {};
    elements.forEach(element => {
        const routeKey = element.getAttribute('data-route');
        const routeValue = routes[routeKey];
        if (typeof routeValue !== 'string') {
            return;
        }
        if (element.tagName === 'A') {
            element.setAttribute('href', routeValue);
        }
    });
}
let headerLinkScriptPromise = null;
function bindHeaderLinks(container) {
    if (!container) {
        return;
    }
    const header = container.querySelector('.header');
    if (!header) {
        return;
    }


    if (!headerLinkScriptPromise) {
        headerLinkScriptPromise = new Promise((resolve, reject) => {
            const script = document.createElement('script');
            script.src = '/js/common/headerLink.js';
            script.onload = () => resolve(HeaderLinkManager);
            script.onerror = reject;
            document.head.appendChild(script);
        });
    }
    headerLinkScriptPromise
        .then(() => HeaderLinkManager(header))
        .catch(error => console.error('Failed to load header link script', error));
}


// Load header fragment
export async function loadHeader(type = 'with-user-menu') {
    const headerMap = {
        'with-user-menu': '/fragments/header-with-user-menu.html',
        'with-back': '/fragments/header-with-back.html',
        'auth': '/fragments/header-auth.html',
        'auth-with-back': '/fragments/header-auth-with-back.html'
    };

    const url = headerMap[type];
    if (!url) {
        console.error('Unknown header type:', type);
        return;
    }

    await loadFragment(url, 'header-container');
}
