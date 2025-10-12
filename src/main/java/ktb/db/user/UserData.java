package ktb.db.user;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import ktb.constant.MessageConstant.Email;
import ktb.constant.MessageConstant.Nickname;
import ktb.constant.MessageConstant.User;
import ktb.domain.UserAccount;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserData {
    private static final Map<Long, UserAccount> store = new ConcurrentHashMap<>();
    private static final Map<String, UserAccount> emailIndex = new ConcurrentHashMap<>();
    private static final Map<String, UserAccount> nickNameIndex = new ConcurrentHashMap<>();

    // ✅ ID 자동 증가
    private static final AtomicLong idGenerator = new AtomicLong(0);

    private static final ReadWriteLock lock = new ReentrantReadWriteLock();
    private static final Lock readLock = lock.readLock();
    private static final Lock writeLock = lock.writeLock();

    // ==============================
    // ✅ 조회 계열 (ReadLock)
    // ==============================
    public static Optional<UserAccount> findById(Long id) {
        readLock.lock();
        try {
            return Optional.ofNullable(store.get(id));
        } finally {
            readLock.unlock();
        }
    }

    public static Optional<UserAccount> findByEmail(String email) {
        readLock.lock();
        try {
            return Optional.ofNullable(emailIndex.get(email));
        } finally {
            readLock.unlock();
        }
    }

    public static Optional<UserAccount> findByNickName(String nickName) {
        readLock.lock();
        try {
            return Optional.ofNullable(nickNameIndex.get(nickName));
        } finally {
            readLock.unlock();
        }
    }

    // ==============================
    // ✅ 저장 계열 (WriteLock)
    // ==============================
    public static void save(UserAccount user) {
        writeLock.lock();
        try {
            //유효성 검사
            validateDuplicate(user);

            // ID 자동 생성
            Long newId = idGenerator.incrementAndGet();
            user.initId(newId);

            // 저장 및 인덱스 생성
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
                throw new NoSuchElementException(User.NON_EXIST);
            }
        } finally {
            readLock.unlock();
        }

        writeLock.lock();
        try {
            // 유효성 검사
            validateDuplicate(updateUser);

            // 인덱스 정리 (기존 값 제거)
            removeIndex(originUser);

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

    private static void validateDuplicate(UserAccount user) throws IllegalArgumentException{
        // 이미 등록된 이메일 방지
        if (emailIndex.containsKey(user.getEmail())) {
            throw new IllegalArgumentException(Email.DUPLICATE);
        }

        // 이미 등록된 닉네임 방지
        if(nickNameIndex.containsKey(user.getNickName())) {
            throw new IllegalArgumentException(Nickname.DUPLICATE);
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
}
