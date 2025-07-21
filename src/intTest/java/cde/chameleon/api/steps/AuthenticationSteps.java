package cde.chameleon.api.steps;

import cde.chameleon.api.actor.AuthenticationActor;
import io.cucumber.java.en.Given;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthenticationSteps {

    final AuthenticationActor authenticationActor;

    @Given("I am logged out")
    public void logOut() {
        authenticationActor.logOut();
    }
}
