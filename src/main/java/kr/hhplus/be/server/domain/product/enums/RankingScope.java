package kr.hhplus.be.server.domain.product.enums;

import kr.hhplus.be.server.global.exception.ApiErrorCode;
import kr.hhplus.be.server.global.exception.ApiException;

import java.util.Arrays;

public enum RankingScope {
    THREE_DAYS,
    WEEKLY,
    ;

    public static RankingScope from(String value) {
        return Arrays.stream(RankingScope.values())
                .filter(scope -> scope.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new ApiException(ApiErrorCode.INVALID_RANKING_SCOPE));
    }
}
