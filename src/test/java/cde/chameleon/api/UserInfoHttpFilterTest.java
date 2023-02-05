package cde.chameleon.api;

import cde.chameleon.ChameleonDisplayNameGenerator;
import com.google.common.truth.Truth;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.security.auth.Subject;
import java.util.Arrays;

@DisplayNameGeneration(ChameleonDisplayNameGenerator.class)
@ExtendWith(MockitoExtension.class)
class UserInfoHttpFilterTest {

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Mock
    private SecurityContext securityContext;

    private static class AuthenticationMock extends AbstractAuthenticationToken {

        private final String name;

        public AuthenticationMock(String name, GrantedAuthority... authorities) {
            super(Arrays.stream(authorities).toList());
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public boolean implies(Subject subject) {
            return super.implies(subject);
        }

        @Override
        public Object getCredentials() {
            return null;
        }

        @Override
        public Object getPrincipal() {
            return null;
        }
    }

    private static class UserInfoFilterChain implements FilterChain {

        private String userName;
        private String userRoles;

        @Override
        public void doFilter(ServletRequest request, ServletResponse response) {
            userName = MDC.get(UserInfoHttpFilter.USER_NAME_KEY);
            userRoles = MDC.get(UserInfoHttpFilter.USER_ROLES_KEY);
        }

        public String getUserName() {
            return userName;
        }

        public String getUserRoles() {
            return userRoles;
        }
    }

    @Test
    void givenUserInfo_whenDoingFilter_thenSetsUserInfoInMdc() throws Exception {
        // given
        UserInfoHttpFilter userInfoHttpFilter = new UserInfoHttpFilter();
        UserInfoFilterChain userInfoFilterChain = new UserInfoFilterChain();
        String expectedUserName = RandomStringUtils.randomPrint(3, 20);
        String expectedRoles = "role1, role2, role3";
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(securityContext.getAuthentication())
                .thenReturn(new AuthenticationMock(
                        expectedUserName,
                        new SimpleGrantedAuthority("ROLE_role1"),
                        new SimpleGrantedAuthority("ROLE_role2"),
                        new SimpleGrantedAuthority("ROLE_role3")));

        // when
        userInfoHttpFilter.doFilter(httpServletRequest, httpServletResponse, userInfoFilterChain);

        // then
        Truth.assertThat(userInfoFilterChain.getUserName()).isEqualTo(expectedUserName);
        Truth.assertThat(userInfoFilterChain.getUserRoles()).isEqualTo(expectedRoles);
    }
}
