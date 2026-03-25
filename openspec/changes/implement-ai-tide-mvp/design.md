# AI-Tide MVP Design

## Context

**Current State:**
- Project documentation complete (PRD, architecture, database design, API specs)
- Frontend has only HomeView.vue with mock data
- No backend project exists
- No database initialization
- No Docker setup

**Constraints:**
- Backend-first strategy (validate architecture before building UI)
- Docker Compose required for one-click startup
- Focus on MVP core features only
- Follow PRD specifications exactly

**Stakeholders:**
- Users: need functional platform for browsing AI content
- Admin: need backend to manage users and content
- Developers: maintainable, scalable architecture

## Goals / Non-Goals

**Goals:**
- Build complete Spring Boot 3 backend with all MVP modules
- Create complete Vue 3 frontend framework and all MVP pages
- Set up Docker environment (MySQL 8.0, Redis 7, Nginx)
- Implement user authentication (JWT) and authorization (RBAC)
- Enable content browsing, search, and detail views
- Support basic user interactions (like, favorite, comment, rating)
- Provide admin dashboard for content and user management
- Ensure responsive design for frontend

**Non-Goals:**
- Intelligent agent collaboration system (complex feature for later iteration)
- Payment system (manual grant only for now)
- Real-time WebSocket messaging
- Advanced recommendation algorithms
- Elasticsearch integration (use MySQL full-text search for MVP)
- OAuth social login
- Email verification (simplified for MVP)

## Decisions

### 1. Backend Architecture: Spring Boot 3 Layered Pattern

**Decision:** Use standard 3-layer architecture (Controller → Service → Repository) with Spring Security

**Rationale:**
- Well-documented, battle-tested pattern
- Clean separation of concerns
- Easy to test and maintain
- Spring Security provides JWT support out of the box

**Alternatives Considered:**
- Hexagonal Architecture: More complex, overkill for MVP
- Microservices: Too much infrastructure for MVP
- JAX-RS: Less ecosystem support in Java world

### 2. Frontend Stack: Vue 3 + TypeScript + Vite + Element Plus

**Decision:** Vue 3 Composition API with TypeScript

**Rationale:**
- Composition API provides better code organization
- TypeScript catches errors early
- Vite offers fast HMR and build times
- Element Plus provides enterprise-grade components
- UnoCSS for atomic CSS without runtime overhead

**Alternatives Considered:**
- React + Next.js: Good choice, but team prefers Vue ecosystem
- Options API: Vue 3 Composition API is future-forward
- Tailwind CSS: Requires larger bundle, UnoCSS is lighter

### 3. Database: MySQL 8.0 + Redis 7

**Decision:** MySQL for persistence, Redis for caching and sessions

**Rationale:**
- MySQL: Reliable, well-supported, full-text search support
- Redis: Fast in-memory storage for sessions, cache, rate limiting
- Follows PRD specifications exactly

**Alternatives Considered:**
- PostgreSQL: Good option, but PRD specifies MySQL
- MongoDB: Not relational, doesn't fit content structure well
- Memcached: Redis has more features (persistence, data structures)

### 4. Authentication: JWT with Redis Storage

**Decision:** JWT tokens stored in Redis as blacklist/whitelist

**Rationale:**
- Stateless authentication fits REST API
- Redis allows token revocation (logout, password change)
- No session management overhead

**Flow:**
```
Login → Generate JWT → Store in Redis → Return to client
      → Client stores in localStorage → Sends in Authorization header
```

**Alternatives Considered:**
- Session-based: Requires server-side session state
- JWT without Redis: Can't revoke tokens efficiently

### 5. Password Security: bcrypt

**Decision:** Use BCrypt for password hashing

**Rationale:**
- Industry standard with built-in salting
- Adjustable work factor for future-proofing
- Spring Security integrates with BCrypt

### 6. Docker Setup: Docker Compose

**Decision:** Single docker-compose.yml with all services

**Rationale:**
- One command to start everything
- Reproducible development environment
- Easy for deployment

**Services:**
- MySQL 8.0
- Redis 7
- Spring Boot API
- Vue 3 Web (Nginx-optimized)
- Nginx (reverse proxy)

### 7. API Design: RESTful with JSON

**Decision:** Standard REST API with JSON responses

**Rationale:**
- Stateless, cacheable
- Easy to consume from frontend
- Industry standard

**Response Format:**
```json
{
  "code": 200,
  "message": "Success",
  "data": {},
  "timestamp": 1648000000000
}
```

### 8. File Storage: Local File System (MVP)

**Decision:** Store uploads in local filesystem

**Rationale:**
- Simpler for MVP
- No external service dependency
- Easy to migrate to OSS later

**Path Structure:**
```
/uploads/
  /avatars/      # User avatars
  /covers/       # Content cover images
  /content/      # Content embedded images
```

**Alternatives Considered:**
- AWS S3/Alibaba OSS: Overkill for MVP, adds external dependency
- Base64 in database: Inefficient, bloats database

## Risks / Trade-offs

### [Risk] Development Time Overrun

**Risk:** Full MVP implementation is large, may exceed estimated time

**Mitigation:**
- Focus on MVP features only, defer advanced features
- Use code generation for boilerplate where appropriate
- Implement in priority order: auth → content → interactions → admin

### [Risk] Docker Environment Setup Issues

**Risk:** Local Docker may have network or permission issues

**Mitigation:**
- Provide clear setup instructions
- Test on multiple OS (Linux, macOS, Windows WSL2)
- Fallback to manual setup instructions

### [Trade-off] Search Performance vs Complexity

**Trade-off:** Using MySQL full-text search instead of Elasticsearch

**Decision:** Accept MySQL full-text for MVP, plan Elasticsearch migration

**Impact:**
- Slower search performance for large datasets
- Limited search features (no fuzzy matching, no ranking)

### [Trade-off] Real-time vs Manual Data Updates

**Trade-off:** No automatic data crawling in MVP

**Decision:** Manual content entry by editors/admins

**Impact:**
- Slower content updates
- Less comprehensive coverage initially

### [Risk] Frontend-Backend Integration Complexity

**Risk:** API contract changes may break frontend

**Mitigation:**
- Follow PRD API specs exactly
- Use TypeScript interfaces for API contracts
- Document any deviations from PRD

## Open Questions

1. Should we implement email verification for registration, or skip for MVP?
2. What initial seed data should be included (sample categories, tags, demo content)?
3. Should we create admin backend as separate Vue app or part of main app?
