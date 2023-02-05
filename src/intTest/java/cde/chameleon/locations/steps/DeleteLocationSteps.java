package cde.chameleon.locations.steps;

import cde.chameleon.locations.actor.LocationActor;
import com.google.common.truth.Truth;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.UUID;

public class DeleteLocationSteps {

    private final LocationActor locationActor;

    public DeleteLocationSteps(LocationActor locationActor) {
        this.locationActor = locationActor;
    }

    @When("I delete the location")
    public void deletingLocation() throws IOException, InterruptedException {
        locationActor.deleteLocation(locationActor.getDefaultTestLocationId());
    }

    @Then("the location has been deleted successfully")
    public void verifyLocationDeleted() throws IOException, InterruptedException {
        Truth.assertThat(locationActor.getHttpResponseStatus()).isEqualTo(HttpStatus.NO_CONTENT);

        locationActor.getLocationById(locationActor.getDefaultTestLocationId());
        Truth.assertThat(locationActor.getHttpResponseStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @When("I delete a location by a non-existing id")
    public void deletingLocationByNonExistingId() throws IOException, InterruptedException {
        locationActor.deleteLocation(UUID.randomUUID().toString());
    }
}
