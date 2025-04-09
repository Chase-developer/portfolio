package com.chase.portfolio;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class CssAccessFilter extends OncePerRequestFilter {

    private static final String ALLOWED_REFERER = PortfolioApplication.isInProject() ? "http://localhost" : "https://chase-developer.com"; // Change this to your domain
    private static final AntPathRequestMatcher CSSMatcher = new AntPathRequestMatcher("/css/**");
    private static final AntPathRequestMatcher JSMatcher = new AntPathRequestMatcher("/js/**");
    private static final AntPathRequestMatcher FONTMatcher = new AntPathRequestMatcher("/font/**");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (CSSMatcher.matches(request) || JSMatcher.matches(request) || FONTMatcher.matches(request)) {
            String referer = request.getHeader("Referer");

            if (referer == null || !referer.startsWith(ALLOWED_REFERER)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
