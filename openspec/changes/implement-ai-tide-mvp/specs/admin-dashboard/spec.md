# Admin Dashboard Specification

## ADDED Requirements

### Requirement: Admin Authentication

The system dashboard SHALL require ADMIN role for access.

#### Scenario: Access admin dashboard with admin role

- **WHEN** authenticated user with ADMIN role accesses admin dashboard
- **THEN** system grants access
- **AND** system loads admin interface

#### Scenario: Access admin dashboard without admin role

- **WHEN** authenticated user without ADMIN role attempts admin access
- **THEN** system denies access
- **AND** system returns 403 Forbidden error
- **AND** system redirects to homepage

#### Scenario: Access admin dashboard without authentication

- **WHEN** unauthenticated user attempts admin access
- **THEN** system denies access
- **AND** system redirects to login page
- **AND** system stores intended URL for post-login redirect

### Requirement: User Management

The system dashboard SHALL allow admins to manage platform users.

#### Scenario: List all users

- **WHEN** admin accesses user management page
- **THEN** system returns paginated user list
- **AND** results include username, email, role, status, register time, last login time
- **AND** results are sorted by register time descending

#### Scenario: Search users

- **WHEN** admin searches users by username or email
- **THEN** system returns matching users
- **AND** search supports partial matching

#### Scenario: Filter users by role

- **WHEN** admin filters users by role (USER, EDITOR, ADMIN)
- **THEN** system returns users with specified role

#### Scenario: Filter users by status

- **WHEN** admin filters users by account status (enabled/disabled)
- **THEN** system returns users with specified status

#### Scenario: Change user role

- **WHEN** admin changes user role
- **AND** new role is valid
- **AND** admin is not trying to promote user to higher privilege
- **THEN** system updates user role
- **AND** system logs operation
- **AND** system returns success

#### Scenario: Change user role to higher privilege (reject)

- **WHEN** admin tries to change user to higher privilege
- **THEN** system rejects operation
- **AND** system returns error "Cannot grant higher privilege"

#### Scenario: Disable user account

- **WHEN** admin disables user account
- **AND** admin provides disable reason
- **AND** user is not the only admin
- **THEN** system sets user enabled status to false
- **AND** system stores disable reason
- **AND** system invalidates user's active tokens
- **AND** system logs operation

#### Scenario: Disable only remaining admin (reject)

- **WHEN** admin tries to disable only remaining admin account
- **THEN** system rejects operation
- **AND** system returns error "Cannot disable last admin"

#### Scenario: Enable user account

- **WHEN** admin enables previously disabled account
- **THEN** system sets user enabled status to true
- **AND** system logs operation

#### Scenario: Delete user (soft delete)

- **WHEN** admin deletes user account
- **AND** admin confirms deletion
- **THEN** system soft deletes user account
- **AND** system sets deleted timestamp
- **AND** system logs operation
- **AND** user data remains recoverable

### Requirement: Content Management

The system dashboard SHALL allow admins to manage all content.

#### Scenario: List all content

- **WHEN** admin accesses content management page
- **THEN** system returns paginated content list
- **AND** results include title, type, category, author, status, stats
- **AND** results include all statuses (draft, published, archived)

#### Scenario: Filter content by status

- **WHEN** admin filters content by status (DRAFT, PUBLISHED, ARCHIVED)
- **THEN** system returns content with specified status

#### Scenario: Filter content by type

- **WHEN** admin filters content by type (MODEL, PRODUCT, ARTICLE)
- **THEN** system returns content of specified type

#### Scenario: Filter content by author

- **WHEN** admin filters content by author
- **THEN** system returns content created by specified user

#### Scenario: Edit any content

- **WHEN** admin edits any content
- **THEN** system allows edit regardless of original author
- **AND** system saves changes
- **AND** system logs operation

#### Scenario: Delete any content

- **WHEN** admin deletes any content
- **THEN** system soft deletes content
- **AND** system cascades to interactions (likes, favorites, comments, ratings)
- **AND** system logs operation

#### Scenario: Restore deleted content

- **WHEN** admin restores previously deleted content
- **THEN** system changes status from ARCHIVED to PUBLISHED
- **AND** system restores content visibility

### Requirement: Category Management

The system dashboard SHALL allow admins to manage content categories.

#### Scenario: List category tree

- **WHEN** admin accesses category management page
- **THEN** system returns hierarchical category tree
- **AND** tree shows parent-child relationships
- **AND** tree shows content count per category

#### Scenario: Create new category

- **WHEN** admin creates new category
- **AND** category name is unique at parent level
- **AND** depth does not exceed 3 levels
- **THEN** system creates category
- **AND** system sets sort order

#### Scenario: Create subcategory

- **WHEN** admin creates subcategory
- **AND** parent category exists
- **AND** resulting depth <= 3
- **THEN** system creates subcategory linked to parent

#### Scenario: Update category

- **WHEN** admin updates category name or description
- **AND** new name is unique at parent level
- **THEN** system updates category

#### Scenario: Delete empty category

- **WHEN** admin deletes category with no content or subcategories
- **THEN** system deletes category
- **AND** system returns success

#### Scenario: Delete category with content (reject)

- **WHEN** admin tries to delete category containing content
- **THEN** system rejects deletion
- **AND** system returns error "Category contains content"

#### Scenario: Delete category with subcategories (reject)

- **WHEN** admin tries to delete category with subcategories
- **THEN** system rejects deletion
- **AND** system returns error "Category has subcategories"

#### Scenario: Reorder categories

- **WHEN** admin drags category to new position
- **THEN** system updates sort order
- **AND** system saves new order

### Requirement: Tag Management

The system dashboard SHALL allow admins to manage content tags.

#### Scenario: List all tags

- **WHEN** admin accesses tag management page
- **THEN** system returns all tags
- **AND** results include name, use count, creation time

#### Scenario: Create new tag

- **WHEN** admin creates new tag
- **AND** tag name is 1-20 characters
- **AND** tag name is unique
- **THEN** system creates tag
- **AND** system initializes use count to 0

#### Scenario: Update tag

- **WHEN** admin renames tag
- **AND** new name is unique
- **THEN** system updates tag name

#### Scenario: Delete unused tag

- **WHEN** admin deletes tag with use count 0
- **THEN** system deletes tag
- **AND** system returns success

#### Scenario: Delete used tag with confirmation

- **WHEN** admin deletes tag with use count > 0
- **AND** admin confirms removal from content
- **THEN** system deletes tag
- **AND** system removes tag from all content
- **AND** system returns success

#### Scenario: Delete used tag without confirmation

- **WHEN** admin tries to delete used tag without confirmation
- **THEN** system prompts for confirmation
- **AND** system does not delete

### Requirement: System Configuration

The system dashboard SHALL allow admins to manage system configuration.

#### Scenario: View system configuration

- **WHEN** admin accesses configuration page
- **THEN** system returns all configuration options
- **AND** sensitive values (passwords) are masked

#### Scenario: Update basic configuration

- **WHEN** admin updates site name, description, or contact email
- **THEN** system saves new values
- **AND** system applies changes immediately

#### Scenario: Update user configuration

- **WHEN** admin updates user registration settings
- **THEN** system saves new settings
- **AND** settings apply to future registrations

#### Scenario: Update email configuration

- **WHEN** admin updates SMTP settings
- **THEN** system saves new settings
- **AND** system validates connection (optional)

### Requirement: Data Statistics

The system dashboard SHALL display platform usage statistics.

#### Scenario: View overview statistics

- **WHEN** admin accesses statistics dashboard
- **THEN** system returns aggregated statistics
- **AND** stats include:
  - Total users
  - New users today/week/month
  - Total content
  - New content today/week/month
  - Total interactions (likes, comments, favorites)
  - Active users (last 7 days)

#### Scenario: View content statistics

- **WHEN** admin views content statistics
- **THEN** system returns breakdown by type
- **AND** stats include count per type
- **AND** stats include top content by various metrics

#### Scenario: View user activity chart

- **WHEN** admin requests user activity chart
- **THEN** system returns time-series data
- **AND** data covers specified date range
- **AND** data shows user registrations, logins, actions

### Requirement: Operation Logs

The system dashboard SHALL provide audit logs of all operations.

#### Scenario: View operation logs

- **WHEN** admin accesses operation logs page
- **THEN** system returns paginated log entries
- **AND** each entry includes timestamp, user, operation, details, result

#### Scenario: Filter logs by operation type

- **WHEN** admin filters logs by operation type (user, content, admin)
- **THEN** system returns logs of specified type

#### Scenario: Filter logs by date range

- **WHEN** admin filters logs by date range
- **THEN** system returns logs within specified range

#### Scenario: Filter logs by user

- **WHEN** admin filters logs by specific user
- **THEN** system returns logs for that user

#### Scenario: Auto-cleanup old logs

- **WHEN** system performs daily maintenance
- **THEN** system deletes logs older than 90 days
- **AND** system archives deleted logs (optional)

### Requirement: Batch Operations

The system dashboard SHALL support batch operations on content.

#### Scenario: Batch delete content

- **WHEN** admin selects multiple content items
- **AND** admin confirms batch delete
- **THEN** system soft deletes all selected content
- **AND** system returns count of deleted items

#### Scenario: Batch archive content

- **WHEN** admin selects multiple content items
- **AND** admin confirms batch archive
- **THEN** system archives all selected content
- **AND** system returns count of archived items

#### Scenario: Batch publish drafts

- **WHEN** admin selects multiple draft items
- **AND** admin confirms batch publish
- **THEN** system publishes all selected drafts
- **AND** system sets publish time
- **AND** system returns count of published items
