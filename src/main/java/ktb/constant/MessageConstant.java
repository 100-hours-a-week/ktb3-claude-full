package ktb.constant;

public final class MessageConstant {
    public static final class Success {
        public static final String LOGIN = "login_success";
        public static final String SIGNUP = "user_register_success";
        public static final String RETRIEVAL_USER = "user_retrieval_success";
        public static final String RETRIEVAL_POST = "post_retrieval_success";
    }

    public static final class Password {
        public static final String REQUIRED = "비밀번호를 입력해주세요.";
        public static final String PATTERN = "비밀번호는 8자 이상, 20자 이하이며, 대문자, 소문자, 숫자, 특수문자를 각각 최소 1개 포함해야 합니다.";
        public static final String CONFIRMATION_REQUIRED = "비밀번호를 한번 더 입력해주세요";
        public static final String CONFIRMATION_MISMATCH = "비밀번호와 다릅니다.";
    }

    public static final class Email {
        public static final String REQUIRED = "이메일을 입력하세요.";
        public static final String PATTERN = "올바른 이메일 주소 형식을 입력해주세요. (예: example@example.com)";
        public static final String DUPLICATE = "중복된 이메일 입니다.";
    }

    public static final class Nickname {
        public static final String REQUIRED = "닉네임을 입력해주세요.";
        public static final String LENGTH_EXCEEDED = "닉네임은 최대 10자까지 작성 가능합니다.";
        public static final String DUPLICATE = "중복된 닉네임 입니다.";
    }

    public static final class UserImage {
        public static final String REQUIRED = "프로필 사진을 추가해주세요.";
    }

    public static final class Common {
        public static final String NO_WHITESPACE_ALLOWED = "띄어쓰기를 제거해주세요.";
    }

    public static final class User {
        public static final String LOGIN_FAILED = "아이디 또는 비밀번호를 확인해주세요.";
        public static final String AUTHENTICATION_FAILED = "로그인 인증에 실패했습니다.";
        public static final String ACCESS_DENIED = "해당 요청에 대한 접근 권한이 없습니다.";
        public static final String NON_EXIST = "유저 정보를 찾을 수 없습니다.";
        public static final String DELETED = "이미 삭제된 유저 입니다.";
    }

    public static final class ArticleMessage {
        public static final String NON_EXIST = "게시글이 존재하지 않습니다.";
        public static final String DUPLICATE = "이미 존재하는 게시글입니다.";
    }

    public static final class CommentMessage {
        public static final String NON_EXIST = "존재하지 않는 댓글입니다.";
        public static final String EMPTY_CONTENT = "댓글 내용을 입력해주세요.";
    }
}
