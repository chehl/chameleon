package cde.chameleon.api.steps;

import cde.chameleon.api.actor.ApiActor;
import com.google.common.truth.Truth;
import io.cucumber.java.en.Then;
import org.springframework.http.HttpStatus;

public class ApiSteps {

    private final ApiActor apiActor;

    public ApiSteps(ApiActor apiActor) {
        this.apiActor = apiActor;
    }

    @Then("the access is forbidden")
    public void verifyForbidden() {
        Truth.assertThat(apiActor.getHttpResponseStatus()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Then("it is not found")
    public void verifyNotFound() {
        Truth.assertThat(apiActor.getHttpResponseStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Then("it is unprocessable")
    public void verifyUnprocessableEntity() {
        Truth.assertThat(apiActor.getHttpResponseStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
