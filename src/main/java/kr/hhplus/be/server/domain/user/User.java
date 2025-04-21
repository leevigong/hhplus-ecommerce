package kr.hhplus.be.server.domain.user;

import jakarta.persistence.*;
import kr.hhplus.be.server.global.entity.BaseEntity;
import kr.hhplus.be.server.global.exception.ApiErrorCode;
import kr.hhplus.be.server.global.exception.ApiException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "`user`")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nickname;

    @Builder
    private User(Long id, String nickname) {
        validateNickname(nickname);

        this.id = id;
        this.nickname = nickname;
    }

    public static User create(String nickname) {
        return User.builder()
                .nickname(nickname)
                .build();
    }

    public void validateNickname(String nickname) {
        if (nickname == null || nickname.isBlank()) {
            throw new ApiException(ApiErrorCode.INVALID_NICKNAME);
        }
    }
}
