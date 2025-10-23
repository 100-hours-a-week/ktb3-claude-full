package ktb.db.user;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import java.util.function.Supplier;
import ktb.constant.MessageConstant.Email;
import ktb.constant.MessageConstant.Nickname;
import ktb.constant.MessageConstant.User;
import ktb.domain.UserAccount;

import ktb.exception.ConflictDuplicationException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserData {
    private static final Map<Long, UserAccount> store = new ConcurrentHashMap<>();
    private static final Map<String, UserAccount> emailIndex = new ConcurrentHashMap<>();
    private static final Map<String, UserAccount> nickNameIndex = new ConcurrentHashMap<>();

    // ✅ ID 자동 증가
    private static final AtomicLong userSeq = new AtomicLong(0);

    private static final ReadWriteLock lock = new ReentrantReadWriteLock();
    private static final Lock readLock = lock.readLock();
    private static final Lock writeLock = lock.writeLock();

    // ==============================
    // ✅ 조회 계열 (ReadLock)
    // ==============================
    private static Optional<UserAccount> findUserBy(Supplier<UserAccount> supplier) {
        readLock.lock();
        try {
            UserAccount user = supplier.get();
            if (user == null) return Optional.empty();

            return Optional.of(user);
        } finally {
            readLock.unlock();
        }
    }
    public static Optional<UserAccount> findById(Long id) {
        return findUserBy(() -> store.get(id));
    }

    public static Optional<UserAccount> findByEmail(String email) {
        return findUserBy(() -> emailIndex.get(email));
    }

    public static Optional<UserAccount> findByNickName(String nickName) {
        return findUserBy(() -> nickNameIndex.get(nickName));
    }

    // ==============================
    // ✅ 저장 계열 (WriteLock)
    // ==============================
    public static void save(UserAccount user) {
        writeLock.lock();
        try {
            // 변경(UPDATE)
            if (user.getId() != null) {
                Long id = user.getId();
                UserAccount origin = store.get(id);

                // 변경된 필드만 선택적으로 검증
                if (!origin.getEmail().equals(user.getEmail())) {
                    validateDuplicationEmail(user);
                }

                if (!origin.getNickName().equals(user.getNickName())) {
                    validateDuplicateNickname(user);
                }

                // 기존 인덱스 제거 먼저 수행
                removeIndex(origin);

                store.put(id, user);
                saveIndex(user);

                return;
            }

            // 신규 생성(INSERT) - 모든 필드 검증 필요
            validateDuplicate(user);

            Long newId = userSeq.incrementAndGet();
            user.initId(newId);

            store.put(newId, user);
            saveIndex(user);
        } finally {
            writeLock.unlock();
        }
    }

    // ==============================
    // ✅ 수정 계열 (WriteLock)
    // ==============================
    public static void update(UserAccount originUser, UserAccount updateUser) {
        readLock.lock();
        try {
            if (originUser.getId() == null || !store.containsKey(originUser.getId())) {
                throw new NoSuchElementException(User.DELETED);
            }
        } finally {
            readLock.unlock();
        }

        writeLock.lock();
        try {
            // 인덱스 정리 (기존 값 제거) 먼저 수행
            removeIndex(originUser);

            // 변경된 필드만 선택적으로 검증
            if (!originUser.getEmail().equals(updateUser.getEmail())) {
                validateDuplicationEmail(updateUser);
            }
            if (!originUser.getNickName().equals(updateUser.getNickName())) {
                validateDuplicateNickname(updateUser);
            }

            // store 갱신
            store.put(updateUser.getId(), updateUser);

            // 새 인덱스 등록
            saveIndex(updateUser);
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
            UserAccount removed = store.remove(id);
            if (removed != null) {
                removeIndex(removed);
            }
        } finally {
            writeLock.unlock();
        }
    }

    public static void validateDuplicate(UserAccount user) throws ConflictDuplicationException {
        validateDuplicationEmail(user);
        validateDuplicateNickname(user);
    }

    public static void validateDuplicationEmail(UserAccount user) throws ConflictDuplicationException {
        // 이미 등록된 이메일 방지
        if (emailIndex.containsKey(user.getEmail())) {
            throw new ConflictDuplicationException(Email.DUPLICATE);
        }
    }

    public static void validateDuplicateNickname(UserAccount user) throws ConflictDuplicationException {
        // 이미 등록된 닉네임 방지
        if(nickNameIndex.containsKey(user.getNickName())) {
            throw new ConflictDuplicationException(Nickname.DUPLICATE);
        }
    }

    private static void saveIndex(UserAccount user) {
        emailIndex.put(user.getEmail(), user);
        nickNameIndex.put(user.getNickName(), user);
    }

    private static void removeIndex(UserAccount user) {
        emailIndex.remove(user.getEmail());
        nickNameIndex.remove(user.getNickName());
    }

    public static boolean isExistUser(Long id) {
        return store.containsKey(id);
    }
}
