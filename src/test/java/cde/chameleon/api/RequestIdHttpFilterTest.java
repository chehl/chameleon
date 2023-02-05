package cde.chameleon.api;

import cde.chameleon.ChameleonDisplayNameGenerator;
import com.google.common.truth.Truth;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;

@DisplayNameGeneration(ChameleonDisplayNameGenerator.class)
@ExtendWith(MockitoExtension.class)
class RequestIdHttpFilterTest {

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpServletResponse httpServletResponse;

    private static class RequestIdFilterChain implements FilterChain {

        private String requestId;

        @Override
        public void doFilter(ServletRequest request, ServletResponse response) {
            requestId = MDC.get(RequestIdHttpFilter.REQUEST_ID_LOGGING_KEY);
        }

        public String getRequestId() {
            return requestId;
        }
    }

    @Test
    void givenRequestWithRequestId_whenDoingFilter_thenSetsRequestIdInMdc() throws Exception {
        // given
        RequestIdHttpFilter requestIdHttpFilter = new RequestIdHttpFilter();
        String expectedRequestId = UUID.randomUUID().toString();
        Mockito.when(httpServletRequest.getHeader(RequestIdHttpFilter.REQUEST_ID_HEADER))
                .thenReturn(expectedRequestId);
        Mockito.doNothing()
                .when(httpServletResponse).setHeader(RequestIdHttpFilter.REQUEST_ID_HEADER, expectedRequestId);
        RequestIdFilterChain filterChain = new RequestIdFilterChain();

        // when
        requestIdHttpFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

        // then
        Truth.assertThat(filterChain.getRequestId()).isEqualTo(expectedRequestId);
        Mockito.verify(httpServletRequest, Mockito.times(1))
                .getHeader(RequestIdHttpFilter.REQUEST_ID_HEADER);
        Mockito.verify(httpServletResponse, Mockito.times(1))
                .setHeader(RequestIdHttpFilter.REQUEST_ID_HEADER, expectedRequestId);
    }

    @Test
    void givenRequestWithoutRequestId_whenDoingFilter_thenGeneratesRequestIdAndSetsRequestIdInMdc() throws Exception {
        // given
        RequestIdHttpFilter requestIdHttpFilter = new RequestIdHttpFilter();
        Mockito.when(httpServletRequest.getHeader(RequestIdHttpFilter.REQUEST_ID_HEADER))
                .thenReturn(null);
        Mockito.doNothing()
                .when(httpServletResponse).setHeader(
                        Mockito.eq(RequestIdHttpFilter.REQUEST_ID_HEADER), Mockito.anyString());
        RequestIdFilterChain filterChain = new RequestIdFilterChain();

        // when
        requestIdHttpFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

        // then
        Truth.assertThat(filterChain.getRequestId()).isNotNull();
        Mockito.verify(httpServletRequest, Mockito.times(1))
                .getHeader(RequestIdHttpFilter.REQUEST_ID_HEADER);
        Mockito.verify(httpServletResponse, Mockito.times(1))
                .setHeader(RequestIdHttpFilter.REQUEST_ID_HEADER, filterChain.getRequestId());
    }
}
