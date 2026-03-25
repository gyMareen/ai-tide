# User System Specification

## ADDED Requirements

### Requirement: User Registration

The system SHALL allow users to register a new account with unique username and email.

#### Scenario: Successful registration

- **WHEN** user provides valid username, email, password, confirm password, and captcha
- **AND** username is 3-20 characters (letters, numbers, underscore only)
- **AND** email is valid format and not already registered
- **AND** password is 8-32 characters with uppercase, lowercase, and number
- **AND** confirm password matches password
- **AND** captcha is correct
- **AND** user agrees to terms
- **THEN** system creates user account with encrypted password
- **AND** default role is USER
- **AND** system returns success response
- **AND** system sends verification email (optional for MVP)

#### Scenario: Registration with duplicate username

- **WHEN** user provides username that already exists
- **THEN** system returns error with message "Username already exists"
- **AND** system does not create account

#### Scenario: Registration with duplicate email

- **WHEN** user provides email that already exists
- **THEN** system returns error with message "Email already exists"
- **AND** system does not create account

#### Scenario: Registration with weak password

- **WHEN** user provides password that doesn't meet requirements
- **THEN** system returns error with message "Password must be 8-32 characters with uppercase, lowercase, and number"

### Requirement: User Login

The system SHALL authenticate users with username/email and password.

#### Scenario: Successful login with username

- **WHEN** user provides valid username and password
- **AND** account is enabled and not locked
- **THEN** system generates JWT token
- **AND** token includes user ID, username, role, permissions
- **AND** token expires in 2 hours (7 days if "remember me" checked)
- **AND** system stores token in Redis
- **AND** system updates last login time and IP
- **AND** system returns token and user information

#### Scenario: Successful login with email

- **WHEN** user provides valid email and password
- **AND** account is enabled and not locked
- **THEN** system authenticates user same as username login

#### Scenario: Failed login with wrong password

- **WHEN** user provides invalid password
- **THEN** system increments failed login attempt counter
- **AND** system returns error "Invalid password"
- **AND** if attempts exceed 5 within 30 minutes, system locks account for 30 minutes

#### Scenario: Login to locked account

- **WHEN** user attempts to login to locked account
- **THEN** system returns error "Account is locked, please try again later"

#### Scenario: Login to disabled account

- **WHEN** user attempts to login to disabled account
- **THEN** system returns error "Account is disabled"

### Requirement: Password Reset

The system SHALL allow users to reset password via email verification.

#### Scenario: Password reset request

- **WHEN** user requests password reset with email
- **AND** email exists in system (or doesn't exist - don't reveal)
- **THEN** system sends email with reset (optional for MVP)
- **AND** system returns success "If email exists, reset instructions sent"

#### Scenario: Password reset with valid token

- **WHEN** user clicks reset link with valid token
- **AND** token is not expired (30 minute validity)
- **THEN** system shows password reset form
- **AND** system accepts new password
- **AND** system validates new password strength
- **AND** system updates password
- **AND** system invalidates reset token

### Requirement: User Profile Management

The system SHALL allow users to view and update their profile.

#### Scenario: View user profile

- **WHEN** authenticated user requests their profile
- **THEN** system returns user information
- **AND** response includes username, email, nickname, avatar, bio, register time, last login time
- **AND** response does NOT include password

#### Scenario: Update user profile

- **WHEN** authenticated user updates nickname, bio, or avatar
- **AND** new values are valid
- **THEN** system updates user information
- **AND** system returns success with updated profile

#### Scenario: Update password

- **WHEN** authenticated user provides current password, new password, and confirm new password
- **AND** current password is correct
- **AND** new password meets strength requirements
- **AND** new password matches confirm
- **THEN** system updates password with bcrypt encryption
- **AND** system invalidates all existing tokens
- **AND** system requires user to re-login

#### Scenario: Upload avatar

- **WHEN** authenticated user uploads avatar image
- **AND** image is JPG or PNG format
- **AND** image size is less than 2MB
- **THEN** system saves image to /uploads/avatars/ directory
- **AND** system generates unique filename
- **AND** system updates user avatar URL
- **AND** system returns new avatar URL

### Requirement: User Logout

The system SHALL allow users to logout and invalidate their session.

#### Scenario: Successful logout

- **WHEN** authenticated user requests logout
- **THEN** system removes token from Redis
- **AND** system returns success
- **AND** subsequent requests with that token are rejected

### Requirement: User Authorization (RBAC)

The system SHALL enforce role-based access control.

#### Scenario: Admin accessing protected resource

- **WHEN** user with ADMIN role requests admin-only resource
- **THEN** system grants access
- **AND** system allows requested operation

#### Scenario: Regular user accessing protected resource

- **WHEN** user with USER role requests admin-only resource
- **THEN** system denies access
- **AND** system returns 403 Forbidden error

#### Scenario: Guest accessing protected resource

- **WHEN** unauthenticated user requests protected resource
- **THEN** system denies access
- **AND** system returns 401 Unauthorized error
