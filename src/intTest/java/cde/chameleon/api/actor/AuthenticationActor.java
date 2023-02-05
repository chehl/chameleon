package cde.chameleon.api.actor;

import lombok.Getter;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.representations.AccessTokenResponse;

@Getter
public abstract class AuthenticationActor {

    private boolean loggedIn = false;
    private String userName;
    private String token;

    public void logIn(String user, String password) {
        AuthzClient authzClient = AuthzClient.create();
        AccessTokenResponse accessTokenResponse = authzClient.obtainAccessToken(user, password);
        loggedIn = true;
        userName = user;
        token = accessTokenResponse.getToken();
    }

    public void logOut() {
        loggedIn = false;
        userName = null;
        token = null;
    }
}
