package kr.hhplus.be.server.interfaces.common;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class ApiLoggingFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/api/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        long start = System.currentTimeMillis();

        String method = request.getMethod();
        String uri = request.getRequestURI();
        String qs = request.getQueryString();
        String url = uri + (qs != null ? "?" + qs : "");

        // 요청 로그
        log.info("REQUEST: {} {}", method, url);

        // 실제 처리
        chain.doFilter(request, response);

        // 응답 로그
        long end = System.currentTimeMillis();
        long elapsed = end - start;
        int status = response.getStatus();

        log.info("RESPONSE: {} {} STATUS={} TIME={}ms", method, url, status, elapsed);
    }
}
