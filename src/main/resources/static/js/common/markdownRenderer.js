/**
 * Markdown Renderer
 * Handles markdown to HTML conversion using marked.js
 */

export function renderMarkdown(markdownText) {
    if (!markdownText) return '';

    // Check if marked is available
    if (typeof marked === 'undefined') {
        console.warn('marked.js is not loaded. Falling back to plain text with line breaks.');
        return markdownText.replace(/\n/g, '<br>');
    }

    // Configure marked options
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
