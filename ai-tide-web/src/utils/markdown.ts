import MarkdownIt from 'markdown-it'

const md = new MarkdownIt()

export function renderMarkdown(markdown: string): string {
  return md.render(markdown)
}

export function stripMarkdown(markdown: string): string {
  return md.render(markdown).replace(/<[^>]*>?/g, '')
}

export function extractPlainText(markdown: string): string {
  return md.render(markdown).replace(/<[^>]*>?/g, '')
}

export { md }
