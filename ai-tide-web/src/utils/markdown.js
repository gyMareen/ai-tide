import MarkdownIt from 'markdown-it';
const md = new MarkdownIt();
export function renderMarkdown(markdown) {
    return md.render(markdown);
}
export function stripMarkdown(markdown) {
    return md.render(markdown).replace(/<[^>]*>?/g, '');
}
export function extractPlainText(markdown) {
    return md.render(markdown).replace(/<[^>]*>?/g, '');
}
export { md };
