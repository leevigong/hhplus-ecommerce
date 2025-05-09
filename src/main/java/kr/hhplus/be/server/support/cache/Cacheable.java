package kr.hhplus.be.server.support.cache;

import java.time.Duration;

/**
 * 캐시가 공통으로 가져야 하는 속성(이름, TTL)을 정의
 */
public interface Cacheable {

    // redis key
    String getCacheName();

    // 만료 시간
    Duration getTtl();

}
