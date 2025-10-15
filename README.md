# Spring Boot Article Management REST API

### 주요 기능
- 사용자 인증 및 회원가입 
- 게시글 CRUD (생성, 조회, 수정, 삭제)
- 댓글 CRUD
- 커서 기반 페이지네이션
- 게시글 조회수 및 좋아요 기능
- Thread-safe 동시성 처리

---

## 아키텍처

### 계층 구조

```
┌─────────────────────────────────────┐
│     Controller Layer (REST API)     │
│   ArticleController, AuthController │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│         Service Layer               │
│  ArticleService, UserService, etc.  │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│       Repository Layer              │
│  ArticleRepository, UserRepository  │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│    Data Store Layer (Static)        │
│   ArticleData (ConcurrentSkipListMap)│
│   UserData (ConcurrentHashMap)      │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│         Domain Layer                │
│  Article, UserAccount, Comment      │
└─────────────────────────────────────┘
```

### 패키지 구조

```
ktb
├── controller          # REST API 엔드포인트
│   ├── ArticleController
│   ├── CommentController
│   ├── UserController
│   └── AuthController
├── service            # 비즈니스 로직
│   ├── ArticleService
│   ├── CommentService
│   ├── UserService
│   └── AuthService
├── repository         # 데이터 접근 인터페이스
│   ├── ArticleRepository
│   ├── UserRepository
│   └── impl/          # 구현체
├── db                 # Static In-Memory 데이터 저장소
│   ├── article/
│   │   └── ArticleData
│   └── user/
│       └── UserData
├── domain             # 도메인 엔티티
│   ├── Article
│   ├── ArticleComment
│   ├── ArticleMeta
│   └── UserAccount
├── dto                # 데이터 전송 객체
│   ├── request/       # API 요청 값, Request에서 데이터 검증 진행
│   └── response/      # API 응답 값
├── exception          # 예외 처리
│   └── GlobalExceptionHandler
├── common             # 공통 유틸리티
│   └── pagination/
│       ├── Slice
│       └── PageInfo
├── constant           # 상수 관리
│   ├── MessageConstant
│   └── RegexpConstant
└── config             # 설정
    └── TestDataInitializer
```

---

## Class Diagrams

### Article Flow Diagram

```mermaid
classDiagram
    %% Controller
    class ArticleController {
        -ArticleService articleService
        +getAll(request) ArticlesResponse
        +insertArticle(request) ResponseEntity
        +getOne(id) CommonResponse
        +patchArticle(id, request) ResponseEntity
        +deleteArticle(id) ResponseEntity
    }

    %% Service
    class ArticleService {
        -ArticleRepository articleRepository
        -UserService userService
        +findAll(pageInfo) Slice~ArticleSimpleDto~
        +findById(articleId) ArticleDto
        +findByIdDetail(articleId) ArticleDetailDto
        +save(updated) void
        +delete(id) void
    }

    %% Repository
    class ArticleRepository {
        <<interface>>
        +findById(id) Optional~Article~
        +findByTitle(title) Optional~Article~
        +findAll(cursorId, size) Slice~Article~
        +getNextCursor(lastId) Optional~Long~
        +like(id) void
        +save(article) void
        +deleteById(id) void
        +addComment(articleId, content, user) ArticleComment
        +updateComment(articleId, commentId, content) void
        +deleteComment(articleId, commentId) void
    }

    class ArticleRepositoryImpl {
        +findById(id) Optional~Article~
        +findByTitle(title) Optional~Article~
        +findAll(cursorId, size) Slice~Article~
        +save(article) void
        +deleteById(id) void
        +addComment(articleId, content, user) ArticleComment
    }

    %% Data Store
    class ArticleData {
        -NavigableMap~Long, Article~ store$
        -Map~String, Article~ titleIndex$
        -AtomicLong articleSeq$
        -ReadWriteLock lock$
        +findById(id)$ Optional~Article~
        +findByTitle(title)$ Optional~Article~
        +findAll(cursorId, size)$ Slice~Article~
        +save(article)$ void
        +deleteById(id)$ void
        +addComment(articleId, content, user)$ ArticleComment
        +updateComment(articleId, commentId, content)$ void
        +deleteComment(articleId, commentId)$ void
    }

    %% Domain
    class Article {
        -Long id
        -String title
        -String content
        -UserAccount createBy
        -ArticleMeta meta
        -AtomicLong commentSeq
        -ConcurrentLinkedDeque~ArticleComment~ comments
        -String imagePath
        +create(id, title, content, user, imagePath)$ Article
        +addComment(content, user) ArticleComment
        +updateComment(contentId, content) void
        +deleteComment(commentId) void
        +incrementLike() void
        +incrementView() void
        +update(title, content) void
        +getAllComments() List~ArticleComment~
    }

    class ArticleMeta {
        -Long articleId
        -AtomicInteger likeCnt
        -AtomicInteger viewCnt
        -AtomicInteger commentCnt
        -LocalDateTime createAt
        -LocalDateTime updateAt
        +init(articleId)$ ArticleMeta
        +incrementViewCnt() void
        +incrementLikeCnt() void
        +incrementCommentCnt() void
        +decrementCommentCnt() void
        +updateTimestamp() void
    }

    class UserAccount {
        -Long id
        -String email
        -String nickName
        -String password
        -String profileImagePath
    }

    %% Common
    class Slice~T~ {
        -List~T~ content
        -boolean hasNext
        -Long nextCursor
        +of(content, hasNext, nextCursor)$ Slice~T~
        +getData() List~T~
        +getPageInfo() PageInfo
    }

    class PageInfo {
        -Long cursor
        -boolean hasNext
        +of(cursor, hasNext)$ PageInfo
    }

    %% Exception Handler
    class GlobalExceptionHandler {
        +handleMethodArgumentNotValid(ex) ResponseEntity
        +handleAuthenticateException(ex, request) ResponseEntity
        +handleAuthorizationException(ex, request) ResponseEntity
        +handleConflictDuplicationException(ex) ResponseEntity
        +handleNonExistUserException(ex) ResponseEntity
    }

    %% Relationships
    ArticleController --> ArticleService
    ArticleService --> ArticleRepository
    ArticleRepository <|.. ArticleRepositoryImpl : implements
    ArticleRepositoryImpl --> ArticleData : delegates to
    ArticleData ..> Article : manages
    Article *-- ArticleMeta : contains
    Article o-- UserAccount : created by
    ArticleService ..> Slice : returns
    ArticleRepository ..> Slice : returns
```

### Comment Flow Diagram

```mermaid
classDiagram
    %% Controller
    class CommentController {
        -CommentService commentService
        +addComment(articleId, request) ResponseEntity
        +updateComment(articleId, commentId, request) ResponseEntity
        +deleteComment(articleId, commentId, request) ResponseEntity
    }

    %% Service
    class CommentService {
        -ArticleRepository articleRepository
        -UserService userService
        +addComment(articleId, dto) void
        +updateComment(articleId, commentId, dto) void
        +deleteComment(articleId, commentId, dto) void
    }

    %% Repository (Article Repository handles comments)
    class ArticleRepository {
        <<interface>>
        +findById(id) Optional~Article~
        +addComment(articleId, content, user) ArticleComment
        +updateComment(articleId, commentId, content) void
        +deleteComment(articleId, commentId) void
    }

    class ArticleRepositoryImpl {
        +findById(id) Optional~Article~
        +addComment(articleId, content, user) ArticleComment
        +updateComment(articleId, commentId, content) void
        +deleteComment(articleId, commentId) void
    }

    %% Data Store
    class ArticleData {
        -NavigableMap~Long, Article~ store$
        -ReadWriteLock lock$
        +findById(id)$ Optional~Article~
        +addComment(articleId, content, user)$ ArticleComment
        +updateComment(articleId, commentId, content)$ void
        +deleteComment(articleId, commentId)$ void
    }

    %% Domain
    class Article {
        -Long id
        -AtomicLong commentSeq
        -ConcurrentLinkedDeque~ArticleComment~ comments
        -ArticleMeta meta
        +addComment(content, user) ArticleComment
        +updateComment(contentId, content) void
        +deleteComment(commentId) void
        +getAllComments() List~ArticleComment~
    }

    class ArticleComment {
        -Long id
        -Long articleId
        -String content
        -UserAccount createBy
        -LocalDateTime createAt
        -LocalDateTime updateAt
        +init(articleId, commentId, content, user)$ ArticleComment
        +update(newContent) void
    }

    class ArticleMeta {
        -AtomicInteger commentCnt
        +incrementCommentCnt() void
        +decrementCommentCnt() void
    }

    class UserAccount {
        -Long id
        -String email
        -String nickName
    }

    %% Exception Handler
    class GlobalExceptionHandler {
        +handleMethodArgumentNotValid(ex) ResponseEntity
        +handleAuthenticateException(ex, request) ResponseEntity
        +handleAuthorizationException(ex, request) ResponseEntity
    }

    %% Relationships
    CommentController --> CommentService
    CommentService --> ArticleRepository
    ArticleRepository <|.. ArticleRepositoryImpl : implements
    ArticleRepositoryImpl --> ArticleData : delegates to
    ArticleData ..> Article : manages
    Article *-- ArticleComment : contains many
    Article *-- ArticleMeta : contains
    ArticleComment o-- UserAccount : created by
```

### User Flow Diagram

```mermaid
classDiagram
    %% Controllers
    class UserController {
        -UserService userService
        +updateNickname(request) ResponseEntity
        +updatePassword(request) ResponseEntity
        +deleteUser(request) ResponseEntity
    }

    class AuthController {
        -AuthService authService
        +login(request) ResponseEntity
        +signup(request) ResponseEntity
    }

    %% Services
    class UserService {
        -UserRepository userRepository
        +getUserInfo(userId) UserAccountDto
        +save(user) void
        +delete(userId) void
        +updateNickname(userId, nickname) void
        +updatePassword(userId, password) void
    }

    class AuthService {
        -UserRepository userRepository
        +authenticate(email, password) UserAccountDto
        +register(signupDto) void
    }

    %% Repository
    class UserRepository {
        <<interface>>
        +findById(id) Optional~UserAccount~
        +findByEmail(email) Optional~UserAccount~
        +save(user) void
        +deleteById(id) void
    }

    class UserRepositoryImpl {
        +findById(id) Optional~UserAccount~
        +findByEmail(email) Optional~UserAccount~
        +save(user) void
        +deleteById(id) void
    }

    %% Data Store
    class UserData {
        -Map~Long, UserAccount~ store$
        -Map~String, UserAccount~ emailIndex$
        -AtomicLong userSeq$
        -ReadWriteLock lock$
        +findById(id)$ Optional~UserAccount~
        +findByEmail(email)$ Optional~UserAccount~
        +save(user)$ void
        +deleteById(id)$ void
    }

    %% Domain
    class UserAccount {
        -Long id
        -String email
        -String nickName
        -String password
        -String profileImagePath
        +initId(id) void
        +changeNickName(nickName) void
        +changePassword(password) void
    }

    %% Exception Handler
    class GlobalExceptionHandler {
        +handleMethodArgumentNotValid(ex) ResponseEntity
        +handleAuthenticateException(ex, request) ResponseEntity
        +handleAuthorizationException(ex, request) ResponseEntity
        +handleConflictDuplicationException(ex) ResponseEntity
        +handleNonExistUserException(ex) ResponseEntity
    }

    %% Relationships
    UserController --> UserService
    AuthController --> AuthService
    UserService --> UserRepository
    AuthService --> UserRepository
    UserRepository <|.. UserRepositoryImpl : implements
    UserRepositoryImpl --> UserData : delegates to
    UserData ..> UserAccount : manages
```

---

## 핵심 설계 특징

### 1. In-Memory 동시성 데이터 저장소

데이터베이스 대신 정적 자료구조를 사용하여 데이터를 관리합니다:

- **ConcurrentSkipListMap**: 정렬된 순서를 유지하면서 동시성 접근 지원
- **ReadWriteLock**: 다중 읽기 또는 단일 쓰기 락 패턴 적용
- **AtomicLong**: ID 자동 생성 및 카운터 관리
- **보조 인덱스**: 제목 기반 조회를 위한 `titleIndex` 관리

**예시: ArticleData.java**
```java
private static final NavigableMap<Long, Article> store = new ConcurrentSkipListMap<>();
private static final Map<String, Article> titleIndex = new ConcurrentHashMap<>();
private static final AtomicLong articleSeq = new AtomicLong(0);
private static final ReadWriteLock lock = new ReentrantReadWriteLock();
```

### 2. 커서 기반 페이지네이션

오프셋 방식 대신 커서 기반 페이지네이션을 구현하여 안정적인 페이징을 제공합니다:

- `Slice<T>`: 페이징된 결과와 메타데이터 포함
- `PageInfo`: 커서 ID, 크기, 다음 페이지 존재 여부 관리
- 대규모 데이터셋에서도 일관된 성능 보장

**참고**: `ktb.common.pagination.Slice`, `ArticleData.java:61-84`

### 3. 전역 예외 처리

`GlobalExceptionHandler`를 통해 일관된 에러 응답 제공:

- `AuthenticateException` → 401 Unauthorized
- `AuthorizationException` → 403 Forbidden
- `ConflictDuplicationException` → 409 Conflict
- `NonExistUserException` → 404 Not Found
- 보안 관련 예외 발생 시 클라이언트 IP 및 요청 URI 로깅

**참고**: `ktb.exception.GlobalExceptionHandler`

---

## API 엔드포인트

### 게시글 관리

| Method | Endpoint        | Description           |
|--------|-----------------|-----------------------|
| GET    | `/articles`     | 게시글 목록 조회 (커서 페이지네이션) |
| POST   | `/article`      | 게시글 작성                |
| GET    | `/article/{id}` | 게시글 상세 조회             |
| PATCH  | `/article/{id}` | 게시글 수정                |
| DELETE | `/article/{id}` | 게시글 삭제                |

### 댓글 관리

| Method | Endpoint                                   | Description |
|--------|--------------------------------------------|-------------|
| POST   | `/article/{articleId}/comment`             | 댓글 작성       |
| PUT    | `/article/{articleId}/comment/{commentId}` | 댓글 수정       |
| DELETE | `/article/{articleId}/comment/{commentId}` | 댓글 삭제       |

### 인증 및 사용자

| Method | Endpoint          | Description |
|--------|-------------------|-------------|
| POST   | `/login`          | 로그인         |
| POST   | `/users/signup`   | 회원가입        |
| PATCH  | `/users/nickname` | 닉네임 변경      |
| PATCH  | `/users/password` | 비밀번호 변경     |
| DELETE | `/users/password` | 유저 삭제       |

---

## 주요 클래스 설명

### Domain Layer

**Article** (`ktb.domain.Article`)
- 게시글 엔티티
- 댓글 관리 (`ConcurrentLinkedDeque`)
- 메타데이터 관리 (조회수, 좋아요, 댓글 수)
- Thread-safe 댓글 추가/수정/삭제 메서드 제공

**ArticleMeta** (`ktb.domain.ArticleMeta`)
- 게시글 메타데이터 (조회수, 좋아요, 댓글 수)
- `AtomicLong` 사용으로 동시성 보장

**UserAccount** (`ktb.domain.UserAccount`)
- 사용자 계정 정보 관리

### Data Store Layer

**ArticleData** (`ktb.db.article.ArticleData`)
- 정적 In-Memory 게시글 저장소
- `ReadWriteLock`을 통한 동시성 제어
- 커서 기반 페이지네이션 구현
- 제목 기반 보조 인덱스 관리

**UserData** (`ktb.db.user.UserData`)
- 정적 In-Memory 사용자 저장소
- ID 및 이메일 기반 조회 지원

### Common Utilities

**Slice** (`ktb.common.pagination.Slice`)
- 커서 기반 페이지네이션 결과 래퍼
- 데이터, 다음 커서, 페이지 존재 여부 포함

---

## 동시성 처리

### Lock 전략

- **ReadLock**: 조회 작업 시 여러 스레드 동시 접근 허용
- **WriteLock**: 생성/수정/삭제 작업 시 단일 스레드만 접근

### Atomic 연산


각 엔티티의 Sequential ID 는 `AtomicLong` 사용:
 - 생성/삭제의 경우 Lock 으로 동시성 처리하여 Atomic 만 적용

```java
private static final AtomicLong articleSeq = new AtomicLong(0);
Long newId = articleSeq.incrementAndGet();
```

---