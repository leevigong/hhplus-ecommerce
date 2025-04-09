package kr.hhplus.be.server.domain.user;

import kr.hhplus.be.server.global.exception.ApiErrorCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserTest {

    @Test
    void 닉네임_검증_성공() {
        String nickname = "이다은";

        User user = User.of(nickname);

        assertThat(user.getNickname()).isEqualTo(nickname);
    }

    @ParameterizedTest
    @NullAndEmptySource  // null, ""
    @ValueSource(strings = {"   "})
    void null이나_공백_닉네임_검증_실패(String nickname) {

        assertThatThrownBy(() -> User.of(nickname))
                .hasMessage(ApiErrorCode.INVALID_NICKNAME.getMessage());
    }
}
