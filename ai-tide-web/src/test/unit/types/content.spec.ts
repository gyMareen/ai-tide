import { describe, it } from 'vitest'

describe('content types', () => {
  describe('ContentType', () => {
    it('should have correct values', () => {
      const types = ['MODEL', 'PRODUCT', 'ARTICLE', 'TUTORIAL', 'RESOURCE'] as const

      types.forEach(type => {
        expect(type).toMatch(/^(MODEL|PRODUCT|ARTICLE|TUTORIAL|RESOURCE)$/)
      })
    })
  })

  describe('ContentStatus', () => {
    it('should have correct values', () => {
      const statuses = ['DRAFT', 'PUBLISHED', 'ARCHIVED'] as const

      statuses.forEach(status => {
        expect(status).toMatch(/^(DRAFT|PUBLISHED|ARCHIVED)$/)
      })
    })
  })
})
