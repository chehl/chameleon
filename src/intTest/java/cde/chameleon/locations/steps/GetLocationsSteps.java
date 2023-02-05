package cde.chameleon.locations.steps;

import cde.chameleon.locations.actor.LocationActor;
import cde.chameleon.locations.api.LocationDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.truth.Truth;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.io.IOException;
import java.util.List;

public class GetLocationsSteps {

    private final LocationActor locationActor;

    public GetLocationsSteps(LocationActor locationActor) {
        this.locationActor = locationActor;
    }

    @When("I get the locations")
    public void gettingLocations() throws IOException, InterruptedException {
        locationActor.getLocations();
    }

    @Then("the list of locations contain the added location")
    public void verifyLocations() throws JsonProcessingException {
        Truth.assertThat(locationActor.getHttpResponseBodyAs(new TypeReference<List<LocationDto>>(){})
                        .stream()
                        .map(LocationDto::getId)
                        .filter(id -> id.equals(locationActor.getDefaultTestLocationId()))
                        .count())
                .isEqualTo(1);
    }
}
