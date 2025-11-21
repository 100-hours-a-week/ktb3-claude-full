/**
 * 마크다운 렌더러
 * marked.js를 사용한 마크다운에서 HTML로의 변환 처리
 */

export function renderMarkdown(markdownText) {
    if (!markdownText) return '';

    // marked가 사용 가능한지 확인
    if (typeof marked === 'undefined') {
        console.warn('marked.js is not loaded. Falling back to plain text with line breaks.');
        return markdownText.replace(/\n/g, '<br>');
    }

    // marked 옵션 설정
    marked.setOptions({
        breaks: true,        // Convert \n to <br>
        gfm: true,          // GitHub Flavored Markdown
        headerIds: true,    // Generate IDs for headings
        mangle: false,      // Don't mangle email addresses
        sanitize: false,    // We'll handle sanitization separately if needed
    });

    try {
        return marked.parse(markdownText);
    } catch (error) {
        console.error('Failed to parse markdown:', error);
        return markdownText.replace(/\n/g, '<br>');
    }
}
