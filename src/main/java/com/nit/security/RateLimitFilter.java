package com.nit.security;

import com.nit.service.RedisRateLimiter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final RedisRateLimiter rateLimiter;
    private final AuthUtil authUtil;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        List<String> rateLimitedPaths = List.of(
                "/api/analyze",
                "/api/categorize-skills",
                "/api/track-generation",
                "/api/analysis-result",
                "/api/education");

        boolean isRateLimited = rateLimitedPaths.stream()
                .anyMatch(request.getRequestURI()::contains);

        if (isRateLimited) {
            Long userId = authUtil.getCurrentUserId();
            if (!rateLimiter.allowRequest(userId)) {
                response.setStatus(429);
                response.getWriter().write("Rate limit exceeded");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

}
