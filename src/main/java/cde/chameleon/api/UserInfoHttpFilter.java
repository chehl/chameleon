package cde.chameleon.api;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Component
@Order(2)
public class UserInfoHttpFilter extends HttpFilter {

    public static final String USER_NAME_KEY = "user_name";
    public static final String USER_ROLES_KEY = "user_roles";

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        Authentication authentication = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication());

        String name = authentication.getName();
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(Objects::nonNull)
                .map(role -> role.startsWith("ROLE_") ? role.substring("ROLE_".length()) : role)
                .toList();

        MDC.put(USER_NAME_KEY, name);
        MDC.put(USER_ROLES_KEY, String.join(", ", roles));
        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove(USER_NAME_KEY);
            MDC.remove(USER_ROLES_KEY);
        }
    }
}
