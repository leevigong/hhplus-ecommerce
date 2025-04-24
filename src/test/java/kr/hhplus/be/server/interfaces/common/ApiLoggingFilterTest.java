package kr.hhplus.be.server.interfaces.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

class ApiLoggingFilterTest {

    private ApiLoggingFilter filter;

    @BeforeEach
    void setUp() {
        filter = new ApiLoggingFilter();
    }

    @Test
    void 경로가_api로_시작하지_않으면_필터를_제외() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/health");
        assertThat(filter.shouldNotFilter(request)).isTrue();
    }

    @Test
    void 경로가_api로_시작하면_필터를_적용() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/coupons/issue");
        assertThat(filter.shouldNotFilter(request)).isFalse();
    }

    @Test
    void 경로가_정확히_api이면_필터를_제외() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api");
        assertThat(filter.shouldNotFilter(request)).isTrue();
    }
}
