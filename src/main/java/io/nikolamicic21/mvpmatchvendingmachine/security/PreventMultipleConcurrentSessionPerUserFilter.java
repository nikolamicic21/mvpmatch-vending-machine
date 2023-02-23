package io.nikolamicic21.mvpmatchvendingmachine.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

import static io.nikolamicic21.mvpmatchvendingmachine.config.ApplicationProperties.LOGOUT_ALL_ENDPOINT;

public class PreventMultipleConcurrentSessionPerUserFilter extends OncePerRequestFilter {
    private final SessionRegistry sessionRegistry;
    private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder
            .getContextHolderStrategy();

    public PreventMultipleConcurrentSessionPerUserFilter(SessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    private static String getJSessionIDFromCookie(Cookie[] cookies) {
        return Arrays.stream(cookies).filter(cookie -> cookie.getName().equals("JSESSIONID")).findFirst().map(Cookie::getValue).orElse(null);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!LOGOUT_ALL_ENDPOINT.equals(request.getRequestURI()) &&
                securityContextHolderStrategy.getContext().getAuthentication() != null &&
                securityContextHolderStrategy.getContext().getAuthentication().getPrincipal() instanceof User principal
        ) {
            final var sessions = sessionRegistry.getAllSessions(principal, true);
            if (request.getCookies() != null && request.getCookies().length != 0 &&
                    !request.getSession(false).getId().equals(getJSessionIDFromCookie(request.getCookies())
                    )) {
                sessionRegistry.removeSessionInformation(request.getSession(false).getId());
                request.getSession(false).invalidate();
                securityContextHolderStrategy.clearContext();
                response.sendError(
                        HttpStatus.UNAUTHORIZED.value(), "Session is invalid"
                );
                return;
            }
            if (sessions.isEmpty()) {
                request.getSession(false).invalidate();
                securityContextHolderStrategy.clearContext();
                response.sendError(
                        HttpStatus.UNAUTHORIZED.value(), "Session is invalid"
                );
                return;
            }
            if (sessions.size() > 1) {
                sessionRegistry.removeSessionInformation(request.getSession(false).getId());
                request.getSession(false).invalidate();
                securityContextHolderStrategy.clearContext();
                response.sendError(
                        HttpStatus.UNAUTHORIZED.value(),
                        String.format("Multiple sessions detected for the user with username '%s'", principal.getUsername())
                );
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
