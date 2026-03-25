# Content Management Specification

## ADDED Requirements

### Requirement: Content Creation

The system SHALL allow content editors and admins to create new content.

#### Scenario: Create published content

- **WHEN** authenticated user with EDITOR or ADMIN role creates content
- **AND** user provides valid title (1-200 characters)
- **AND** user provides valid description (1-500 characters)
- **AND** user provides valid content body (Markdown format)
- **AND** user selects content type (MODEL, PRODUCT, or ARTICLE)
- **AND** user selects valid category
- **AND** user provides at least one tag
- **AND** user chooses to publish
- **THEN** system creates content entity
- **AND** system sets status to PUBLISHED
- **AND** system sets publish time to current time
- **AND** system sets author to current user
- **AND** system initializes view count, like count, favorite count, comment count to 0
- **AND** system sets average rating to 0
- **AND** system saves content to database
- **AND** system returns content ID

#### Scenario: Create draft content

- **WHEN** user creates content but chooses to save as draft
- **THEN** system creates content with status DRAFT
- **AND** publish time is null

#### Scenario: Create content with cover image

- **WHEN** user uploads cover image
- **AND** image is JPG or PNG format
- **AND** image size is less than 5MB
- **THEN** system saves image to /uploads/covers/ directory
- **AND** system sets content cover image URL

### Requirement: Content Retrieval

The system SHALL allow users to retrieve published content.

#### Scenario: Get content by ID

- **WHEN** user requests content by ID
- **AND** content exists and status is PUBLISHED
- **THEN** system returns full content details
- **AND** response includes title, description, content, category, tags, author, publish time, stats
- **AND** system increments view count

#### Scenario: Get draft content (author only)

- **WHEN** author requests their own draft content
- **THEN** system returns draft content

#### Scenario: Non-author requests draft content

- **WHEN** user requests draft content they didn't create
- **THEN** system denies access
- **AND** system returns 404 Not Found

#### Scenario: Get non-existent content

- **WHEN** user requests content that doesn't exist
- **THEN** system returns 404 Not Found

### Requirement: Content Update

The system SHALL allow content authors and admins to update content.

#### Scenario: Update own content

- **WHEN** authenticated user updates content they created
- **AND** user is EDITOR or ADMIN
- **THEN** system updates content fields
- **AND** system sets update time to current time
- **AND** system returns updated content

#### Scenario: Admin updates any content

- **WHEN** ADMIN user updates any content
- **THEN** system updates content

#### Scenario: Non-author attempts update

- **WHEN** user tries to update content they didn't create
- **AND** user is not ADMIN
- **THEN** system denies access
- **AND** system returns 403 Forbidden

#### Scenario: Update published content to draft

- **WHEN** user tries to change published content to draft
- **THEN** system rejects the change
- **AND** system returns error "Cannot change published content to draft"

### Requirement: Content Deletion

The system SHALL support soft deletion of content.

#### Scenario: Delete own content

- **WHEN** authenticated user deletes content they created
- **AND** user confirms deletion
- **THEN** system sets content status to ARCHIVED
- **AND** system does not physically delete content
- **AND** system sets deleted time
- **AND** system returns success

#### Scenario: Admin deletes any content

- **WHEN** ADMIN deletes any content
- **THEN** system soft deletes content

### Requirement: Content Listing

The system SHALL provide paginated lists of published content.

#### Scenario: Get latest content

- **WHEN** user requests latest content with page and size
- **THEN** system returns paginated list
- **AND** results sorted by publish time descending
- **AND** results only include PUBLISHED status
- **AND** response includes total count, page, size, pages

#### Scenario: Get content by category

- **WHEN** user requests content filtered by category
- **THEN** system returns content in that category and subcategories
- **AND** results are paginated

#### Scenario: Get content by type

- **WHEN** user requests content filtered by type (MODEL, PRODUCT, ARTICLE)
- **THEN** system returns content of that type only

#### Scenario: Get trending content

- **WHEN** user requests trending content
- **THEN** system returns content sorted by综合热度 (like count, view count, recency)
- **AND** returns top N results

### Requirement: Category Management

The system SHALL allow admins to manage content categories.

#### Scenario: Create category

- **WHEN** ADMIN creates new category
- **AND** category name is 1-100 characters
- **AND** name is unique at same level (same parent)
- **THEN** system creates category
- **AND** system sets sort order
- **AND** system returns category

#### Scenario: Create subcategory

- **WHEN** ADMIN creates category with parent
- **AND** depth does not exceed 3 levels
- **THEN** system creates subcategory linked to parent

#### Scenario: Delete category with content

- **WHEN** ADMIN tries to delete category that contains content
- **THEN** system rejects deletion
- **AND** system returns error "Category contains content"

#### Scenario: Delete category with subcategories

- **WHEN** ADMIN tries to delete category with subcategories
- **THEN** system rejects deletion
- **AND** system returns error "Category has subcategories"

### Requirement: Tag Management

The system SHALL allow admins to manage content tags.

#### Scenario: Create tag

- **WHEN** ADMIN creates new tag
- **AND** tag name is 1-20 characters
- **AND** name is unique
- **THEN** system creates tag
- **AND** system initializes use count to 0

#### Scenario: Get popular tags

- **WHEN** user requests popular tags
- **THEN** system returns tags sorted by use count descending
- **AND** returns top 50 tags

#### Scenario: Delete tag in use

- **WHEN** ADMIN tries to delete tag with use count > 0
- **THEN** system confirms removal from content
- **AND** if confirmed, system removes tag and associations
