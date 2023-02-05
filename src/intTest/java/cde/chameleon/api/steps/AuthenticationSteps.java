package cde.chameleon.api.steps;

import cde.chameleon.api.actor.AuthenticationActor;
import io.cucumber.java.en.Given;

public class AuthenticationSteps {

    AuthenticationActor authenticationActor;

    public AuthenticationSteps(AuthenticationActor authenticationActor) {
        this.authenticationActor = authenticationActor;
    }

    @Given("I am logged out")
    public void logOut() {
        authenticationActor.logOut();
    }
}
