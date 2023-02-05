package cde.chameleon.api;

import java.util.Optional;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Component
@Order(1)
public class RequestIdHttpFilter extends HttpFilter {

    public static final String REQUEST_ID_HEADER = "X-Request-ID";
    public static final String REQUEST_ID_LOGGING_KEY = "x_request_id";

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String requestId = Optional.ofNullable(request.getHeader(REQUEST_ID_HEADER))
            .orElseGet(() -> UUID.randomUUID().toString());
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        MDC.put(REQUEST_ID_LOGGING_KEY, requestId);
        try {
            chain.doFilter(request, responseWrapper);
        } finally {
            MDC.remove(REQUEST_ID_LOGGING_KEY);
        }
        responseWrapper.setHeader(REQUEST_ID_HEADER, requestId);
        responseWrapper.copyBodyToResponse();
    }
}
