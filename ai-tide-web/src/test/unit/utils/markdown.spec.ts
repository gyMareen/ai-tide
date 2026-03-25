import { describe, it, expect } from 'vitest'
import { renderMarkdown, stripMarkdown, extractPlainText, md } from '@/utils/markdown'

describe('markdown utilities', () => {
  describe('renderMarkdown', () => {
    it('should render simple markdown to HTML', () => {
      const input = '# Heading\n\nThis is a paragraph.'
      const result = renderMarkdown(input)

      expect(result).toContain('<h1')
      expect(result).toContain('Heading</h1>')
      expect(result).toContain('<p>')
      expect(result).toContain('This is a paragraph.')
    })

    it('should render bold text', () => {
      const input = '**Bold** text'
      const result = renderMarkdown(input)

      expect(result).toContain('<strong>')
      expect(result).toContain('Bold</strong>')
    })

    it('should render italic text', () => {
      const input = '*Italic* text'
      const result = renderMarkdown(input)

      expect(result).toContain('<em>')
      expect(result).toContain('Italic</em>')
    })

    it('should render links', () => {
      const input = '[Link](https://example.com)'
      const result = renderMarkdown(input)

      expect(result).toContain('<a')
      expect(result).toContain('href="https://example.com"')
      expect(result).toContain('>Link</a>')
    })

    it('should render code blocks', () => {
      const input = '```javascript\nconst x = 1;\n```'
      const result = renderMarkdown(input)

      expect(result).toContain('<pre>')
      expect(result).toContain('<code class="language-javascript">')
    })

    it('should render inline code', () => {
      const input = 'This is `code`'
      const result = renderMarkdown(input)

      expect(result).toContain('<code>')
    })

    it('should render lists', () => {
      const input = '- Item 1\n- Item 2\n- Item 3'
      const result = renderMarkdown(input)

      expect(result).toContain('<ul>')
      expect(result).toContain('<li>')
    })

    it('should handle empty string', () => {
      const result = renderMarkdown('')
      expect(result).toBeDefined()
    })
  })

  describe('stripMarkdown', () => {
    it('should remove HTML tags from markdown', () => {
      const input = '**Bold** and *Italic*'
      const result = stripMarkdown(input)

      expect(result).not.toContain('<')
      expect(result).not.toContain('>')
      expect(result).toContain('Bold')
      expect(result).toContain('Italic')
    })

    it('should remove links but keep text', () => {
      const input = '[Link text](https://example.com)'
      const result = stripMarkdown(input)

      expect(result).not.toContain('<a')
      expect(result). not.toContain('href=')
      expect(result).toContain('Link text')
    })

    it('should remove code blocks but keep content', () => {
      const input = '```javascript\nconst x = 1;\n```'
      const result = stripMarkdown(input)

      expect(result).not.toContain('<pre>')
      expect(result).not.toContain('<code>')
      expect(result).toContain('const x = 1;')
    })

    it('should handle complex markdown', () => {
      const input = '# Title\n\n**Bold** text\n\n- List item\n\n```code block```'
      const result = stripMarkdown(input)

      expect(result).not.toContain('<h1>')
      expect(result).not.toContain('<strong>')
      expect(result).not.toContain('<ul>')
      expect(result).not.toContain('<pre>')
      expect(result).toContain('Title')
      expect(result).toContain('Bold')
      expect(result).toContain('List item')
      expect(result).toContain('code block')
    })

    it('should handle empty string', () => {
      const result = stripMarkdown('')
      expect(result).toBe('')
    })
  })

  describe('extractPlainText', () => {
    it('should extract plain text from markdown', () => {
      const input = '**Bold** and *Italic*'
      const result = extractPlainText(input)

      expect(result).not.toContain('*')
      expect(result).toContain('Bold')
      expect(result).toContain('Italic')
    })

    it('should handle empty string', () => {
      const result = extractPlainText('')
      expect(result).toBe('')
    })

    it('should work like stripMarkdown', () => {
      const input = '# Title\n\n```code```'
      const result1 = extractPlainText(input)
      const result2 = stripMarkdown(input)

      expect(result1).toBe(result2)
    })
  })

  describe('md export', () => {
    it('should be a MarkdownIt instance', () => {
      expect(md).toBeDefined()
      expect(typeof md.render).toBe('function')
    })
  })
})
