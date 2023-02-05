package cde.chameleon.locations.steps;

import cde.chameleon.locations.actor.LocationActor;
import cde.chameleon.locations.api.LocationDto;
import cde.chameleon.locations.api.PatchLocationDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.truth.Truth;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.http.HttpStatus;

import java.io.IOException;

import static cde.chameleon.locations.domain.LocationRandom.*;

public class UpdateLocationSteps {

    private final LocationActor locationActor;

    private PatchLocationDto patchLocationDto;

    public UpdateLocationSteps(LocationActor locationActor) {
        this.locationActor = locationActor;
    }

    @When("I update the location")
    public void updatingLocation() throws IOException, InterruptedException {
        patchLocationDto = new PatchLocationDto();
        patchLocationDto.setId(locationActor.getDefaultTestLocationId());
        patchLocationDto.setName(randomName());
        patchLocationDto.setAddress(randomAddress());
        patchLocationDto.setLatitude(randomLatitude());
        patchLocationDto.setLongitude(randomLongitude());
        locationActor.updateLocation(patchLocationDto);
    }

    @Then("the location has been updated successfully")
    public void verifyLocationUpdated() throws JsonProcessingException {
        Truth.assertThat(locationActor.getHttpResponseStatus()).isEqualTo(HttpStatus.OK);

        LocationDto locationDto = locationActor.getHttpResponseBodyAs(LocationDto.class);
        Truth.assertThat(locationDto.getId()).isEqualTo(patchLocationDto.getId());
        Truth.assertThat(locationDto.getName()).isEqualTo(patchLocationDto.getName());
        Truth.assertThat(locationDto.getAddress()).isEqualTo(patchLocationDto.getAddress());
        Truth.assertThat(locationDto.getLatitude()).isEqualTo(patchLocationDto.getLatitude());
        Truth.assertThat(locationDto.getLongitude()).isEqualTo(patchLocationDto.getLongitude());
    }

    @When("I update the name of the location {string} to the same name as the test location")
    public void updatingNameOfLocationWithAliasToSameNameAsDefaultTestLocation(String alias) throws IOException, InterruptedException {
        locationActor.getLocationById(locationActor.getDefaultTestLocationId());
        String nameOfDefaultTestLocation = locationActor.getHttpResponseBodyAs(LocationDto.class).getName();

        patchLocationDto = new PatchLocationDto();
        patchLocationDto.setId(locationActor.getTestLocationId(alias));
        patchLocationDto.setName(nameOfDefaultTestLocation);
        locationActor.updateLocation(patchLocationDto);
    }
}
