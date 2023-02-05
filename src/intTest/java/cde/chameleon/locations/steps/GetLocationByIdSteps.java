package cde.chameleon.locations.steps;

import cde.chameleon.locations.actor.LocationActor;
import cde.chameleon.locations.api.LocationDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.truth.Truth;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.UUID;

public class GetLocationByIdSteps {

    private final LocationActor locationActor;

    public GetLocationByIdSteps(LocationActor locationActor) {
        this.locationActor = locationActor;
    }

    @When("I get the location by id")
    public void gettingLocationById() throws IOException, InterruptedException {
        locationActor.getLocationById(locationActor.getDefaultTestLocationId());
    }

    @Then("the location with the provided id is returned")
    public void verifyLocation() throws JsonProcessingException {
        Truth.assertThat(locationActor.getHttpResponseStatus()).isEqualTo(HttpStatus.OK);

        Truth.assertThat(locationActor.getHttpResponseBodyAs(LocationDto.class).getId())
                .isEqualTo(locationActor.getDefaultTestLocationId());
    }

    @When("I get a location by a non-existing id")
    public void gettingLocationByNonExistingId() throws IOException, InterruptedException {
        locationActor.getLocationById(UUID.randomUUID().toString());
    }
}
