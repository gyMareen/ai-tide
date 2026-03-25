# User Interactions Specification

## ADDED Requirements

### Requirement: Content Like

The system SHALL allow users to like content.

#### Scenario: Like content

- **WHEN** authenticated user likes content
- **AND** user has not previously liked this content
- **THEN** system creates like record
- **AND** system increments content like count
- **AND** system returns success with new like count

#### Scenario: Unlike content

- **WHEN** authenticated user unlikes content
- **AND** user has previously liked this content
- **THEN** system removes like record
- **AND** system decrements content like count
- **AND** system returns success with new like count

#### Scenario: Check like status

- **WHEN** authenticated user views content
- **THEN** system returns whether user has liked this content
- **AND** response includes like count

#### Scenario: Guest attempts to like

- **WHEN** unauthenticated user attempts to like content
- **THEN** system denies action
- **AND** system returns 401 Unauthorized error

### Requirement: Content Favorite

The system SHALL allow users to favorite content for future reference.

#### Scenario: Add to favorites

- **WHEN** authenticated user favorites content
- **AND** user has not previously favorited this content
- **THEN** system creates favorite record
- **AND** system increments content favorite count
- **AND** system returns success with new favorite count

#### Scenario: Remove from favorites

- **WHEN** authenticated user removes content from favorites
- **THEN** system deletes favorite record
- **AND** system decrements content favorite count
- **AND** system returns success with new favorite count

#### Scenario: Toggle favorite

- **WHEN** authenticated user toggles favorite on content
- **AND** user has favorited it
- **THEN** system removes favorite and decrements count
- **WHEN** authenticated user toggles favorite on content
- **AND** user has not favorited it
- **THEN** system adds favorite and increments count

#### Scenario: Get user's favorites

- **WHEN** authenticated user requests their favorites
- **THEN** system returns paginated list of favorited content
- **AND** results sorted by favorite time descending
- **AND** response includes favorite time for each item

#### Scenario: Get favorite status

- **WHEN** authenticated user views content
- **THEN** system returns whether user has favorited this content

### Requirement: Content Comments

The system SHALL allow users to comment on content.

#### Scenario: Post comment

- **WHEN** authenticated user posts comment on content
- **AND** comment length is 1-500 characters
- **THEN** system creates comment record
- **AND** system increments content comment count
- **AND** system sets publish time to current time
- **AND** system filters sensitive words
- **AND** system returns new comment

#### Scenario: Post empty comment

- **WHEN** authenticated user posts empty comment
- **THEN** system rejects comment
- **AND** system returns error "Comment cannot be empty"

#### Scenario: Post comment with profanity

- **WHEN** authenticated user posts comment containing profanity
- **THEN** system filters or blocks profanity
- **AND** system either rejects or censors words

#### Scenario: Get content comments

- **WHEN** user requests comments for content
- **THEN** system returns paginated comment list
- **AND** results sorted by publish time descending (newest first)
- **AND** each comment includes user info, content, time, like count

#### Scenario: Delete own comment

- **WHEN** authenticated user deletes their own comment
- **AND** user confirms deletion
- **THEN** system deletes comment record
- **AND** system decrements content comment count
- **AND** system returns success

#### Scenario: Delete other's comment (admin)

- **WHEN** admin deletes any comment
- **THEN** system deletes comment record
- **AND** system decrements content comment count

#### Scenario: User cannot delete others' comments

- **WHEN** user tries to delete comment they didn't create
- **AND** user is not admin
- **THEN** system denies action
- **AND** system returns 403 Forbidden error

### Requirement: Content Rating

The system SHALL allow users to rate content with 1-5 stars.

#### Scenario: Rate content

- **WHEN** authenticated user rates content
- **AND** rating is between 1 and 5
- **AND** user has not previously rated this content
- **THEN** system creates rating record
- **AND** system recalculates average rating
- **AND** system returns new average rating
- **AND** user cannot rate same content again

#### Scenario: Update existing rating

- **WHEN** authenticated user rates content they previously rated
- **THEN** system updates existing rating
- **AND** system recalculates average rating
- **AND** system returns new average rating

#### Scenario: Invalid rating value

- **WHEN** user provides rating outside 1-5 range
- **THEN** system rejects rating
- **AND** system returns error "Rating must be between 1 and 5"

#### Scenario: Get content ratings

- **WHEN** user views content
- **THEN** system returns average rating
- **AND** system returns rating distribution (number of each star level)
- **AND** system returns total rating count

#### Scenario: Get user's rating

- **WHEN** authenticated user views content they rated
- **THEN** system returns user's previous rating
- **AND** user sees their rating highlighted

### Requirement: Interaction Statistics

The system SHALL track and display interaction statistics for content.

#### Scenario: Get content stats

- **WHEN** user views content details
- **THEN** system returns interaction statistics
- **AND** stats include view count, like count, favorite count, comment count, average rating

#### Scenario: Increment view count

- **WHEN** user views content detail page
- **THEN** system increments view count
- **AND** system caches updated count
- **AND** system does not increment for same user within short period (optional)

### Requirement: Notification on Interactions

The system MAY notify content authors about interactions (optional for MVP).

#### Scenario: Comment notification (future)

- **WHEN** user comments on content
- **THEN** system (optionally) notifies content author
- **AND** notification includes commenter, preview, link
