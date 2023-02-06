package cde.chameleon.api;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(3)
public class HttpMethodHttpFilter extends HttpFilter {

    public static final String HTTP_METHOD_KEY = "http_method";

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String method = request.getMethod();
        MDC.put(HTTP_METHOD_KEY, method);
        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove(HTTP_METHOD_KEY);
        }
    }
}
