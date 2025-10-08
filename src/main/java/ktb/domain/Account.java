package ktb.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Account {
    private final Long userSeq;
    private final String email;
    private final String nickName;
    private final String password;
    private final String profileImagePath;
}
