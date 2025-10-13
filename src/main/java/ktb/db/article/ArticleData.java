package ktb.db.article;

import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import ktb.common.pagination.Slice;
import ktb.constant.MessageConstant.ArticleMessage;
import ktb.domain.Article;
import ktb.domain.ArticleComment;
import ktb.domain.UserAccount;
import ktb.dto.ArticleDto;

public final class ArticleData {
    private static final NavigableMap<Long, Article> store = new ConcurrentSkipListMap<>();
    private static final Map<String, Article> titleIndex = new ConcurrentHashMap<>();

    // ✅ ID 자동 증가
    private static final AtomicLong articleSeq = new AtomicLong(0);

    private static final ReadWriteLock lock = new ReentrantReadWriteLock();
    private static final Lock readLock = lock.readLock();
    private static final Lock writeLock = lock.writeLock();

    // ==============================
    // ✅ 조회 계열 (ReadLock)
    // ==============================
    public static Optional<Article> findById(Long id) {
        readLock.lock();
        try {
            Article article = store.get(id);
            if (article == null) return Optional.empty();

            article.incrementView(); // ✅ 조회수 증가
            return Optional.of(article);
        } finally {
            readLock.unlock();
        }
    }

    public static Optional<Article> findByTitle(String title) {
        readLock.lock();
        try {
            return Optional.ofNullable(titleIndex.get(title));
        } finally {
            readLock.unlock();
        }
    }

    // ==============================
    // ✅ 커서 기반 페이지네이션
    // ==============================
    public static Slice<Article> findAll(Long cursorId, int size) {
        readLock.lock();
        try {
            NavigableMap<Long, Article> tail = (cursorId == null)
                    ? store
                    : store.tailMap(cursorId, false);

            // 다음 페이지 존재 여부를 판단하기 위해 size + 1개 조회
            List<Article> result = tail.values().stream()
                    .limit(size + 1)
                    .toList();

            boolean hasNext = result.size() > size;
            List<Article> content = hasNext ? result.subList(0, size) : result;

            Long nextCursor = hasNext
                    ? content.get(content.size() - 1).getId()
                    : null;

            return Slice.of(content, hasNext, nextCursor);
        } finally {
            readLock.unlock();
        }
    }

    // ==============================
    // ✅ 다음 커서 계산
    // ==============================
    public static Optional<Long> getNextCursor(Long lastId) {
        readLock.lock();
        try {
            return Optional.ofNullable(store.higherKey(lastId));
        } finally {
            readLock.unlock();
        }
    }

    public static void like(Long id) {
        readLock.lock();
        try {
            Article article = store.get(id);

            if (article != null) {
                article.incrementLike();
            }
        } finally {
            readLock.unlock();
        }
    }

    // ==============================
    // ✅ 저장 계열 (WriteLock)
    // ==============================
    public static void save(ArticleDto dto) {
        writeLock.lock();
        try {
            // ID 자동 생성
            Long newId = articleSeq.incrementAndGet();
            Article article = dto.toEntity(newId);

            //유효성 검사
            validateDuplicate(article);

            // 저장 및 인덱스 생성
            store.put(newId, article);
            saveIndex(article);
        } finally {
            writeLock.unlock();
        }
    }

    public static void save(Article article) {
        writeLock.lock();
        try {
            // 변경(UPDATE)
            if (article.getId() != null) {
                Long id = article.getId();
                Article origin = store.get(id);
                removeIndex(origin);

                store.put(id, article);
                saveIndex(article);

                return;
            }

            // 신규 생성(INSERT)
            Long newId = articleSeq.incrementAndGet();
            Article newArticle = Article.create(newId, article);

            store.put(newId, newArticle);
            saveIndex(newArticle);
        } finally {
            writeLock.unlock();
        }
    }

    // ==============================
    // ✅ 수정 계열 (WriteLock)
    // ==============================
    public static void update(Article originArticle, Article updateArticle) {
        readLock.lock();
        try {
            if (originArticle.getId() == null || !store.containsKey(originArticle.getId())) {
                throw new NoSuchElementException(ArticleMessage.NON_EXIST);
            }
        } finally {
            readLock.unlock();
        }

        writeLock.lock();
        try {
            // 유효성 검사
            validateDuplicate(updateArticle);

            // 인덱스 정리 (기존 값 제거)
            removeIndex(originArticle);

            // store 갱신
            store.put(updateArticle.getId(), updateArticle);

            // 새 인덱스 등록
            saveIndex(updateArticle);
        } finally {
            writeLock.unlock();
        }
    }

    // ==============================
    // ✅ 삭제 계열 (WriteLock)
    // ==============================
    public static void deleteById(Long id) {
        writeLock.lock();
        try {
            Article removed = store.remove(id);
            if (removed != null) {
                removeIndex(removed);
            }
        } finally {
            writeLock.unlock();
        }
    }

    // 제목/내용 수정
    public static void updateContent(Long id, String newTitle, String newContent) {
        readLock.lock();
        try {
            Article a = store.get(id);
            if (a == null) {
                throw new NoSuchElementException(ArticleMessage.NON_EXIST);
            }
            a.update(newTitle, newContent);
            a.updateTimestamp();
        } finally {
            readLock.unlock();
        }
    }

    // ==============================
    // ✅ 댓글 추가
    // ==============================
    public static ArticleComment addComment(Long articleId, String content, UserAccount user) {
        readLock.lock();
        try {
            Article article = store.get(articleId);
            if (article == null) {
                throw new NoSuchElementException(ArticleMessage.NON_EXIST);
            }

            return article.addComment(content, user);
        } finally {
            readLock.unlock();
        }
    }

    // ==============================
    // ✅ 댓글 수정
    // ==============================
    public static void updateComment(Long articleId, Long commentId, String newContent) {
        readLock.lock();

        try {
            Article article = store.get(articleId);
            if (article == null) {
                throw new NoSuchElementException(ArticleMessage.NON_EXIST);
            }

            article.updateComment(commentId, newContent);
        } finally {
            readLock.unlock();
        }
    }

    // ==============================
    // ✅ 댓글 삭제
    // ==============================
    public static void deleteComment(Long articleId, Long commentId) {
        readLock.lock();

        try {
            Article article = store.get(articleId);
            if (article == null) {
                return;
            }

            article.deleteComment(commentId);
        } finally {
            readLock.unlock();
        }
    }

    private static void validateDuplicate(Article article) throws IllegalArgumentException{
        if (store.containsKey(article.getId())) {
            throw new IllegalArgumentException(ArticleMessage.DUPLICATE);
        }
    }

    private static void saveIndex(Article article) {
        titleIndex.put(article.getTitle(), article);
    }

    private static void removeIndex(Article article) {
        titleIndex.remove(article.getTitle());
    }
}
