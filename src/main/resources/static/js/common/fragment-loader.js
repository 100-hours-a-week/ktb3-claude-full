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
        }
    } catch (error) {
        console.error('Error loading fragment:', error);
    }
}

// Fragment 내의 data-route 속성을 실제 href로 변환
function initFragmentUris(container) {
    // data-route 속성을 가진 모든 요소를 찾아서 href 설정
    const elements = container.querySelectorAll('[data-route]');
    elements.forEach(element => {
        const route = element.getAttribute('data-route');
        if (route && window.PageRoutes) {
            // PageRoutes에서 해당 경로 찾기
            const href = window.PageRoutes[route] || route;
            element.setAttribute('href', href);
        }
    });
}

// Load header fragment
async function loadHeader(type = 'with-user-menu') {
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
