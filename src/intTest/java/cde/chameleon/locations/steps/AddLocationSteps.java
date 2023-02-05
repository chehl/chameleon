package cde.chameleon.locations.steps;

import cde.chameleon.locations.actor.LocationActor;
import cde.chameleon.locations.api.AddLocationDto;
import cde.chameleon.locations.api.LocationDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.truth.Truth;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.http.HttpStatus;

import java.io.IOException;

import static cde.chameleon.locations.domain.LocationRandom.*;

public class AddLocationSteps {

    private final LocationActor locationActor;

    private AddLocationDto addLocationDto;

    public AddLocationSteps(LocationActor locationActor) {
        this.locationActor = locationActor;
    }

    @When("I add a location")
    public void addingLocation() throws IOException, InterruptedException {
        addLocationDto = new AddLocationDto();
        addLocationDto.setName(randomName());
        addLocationDto.setAddress(randomAddress());
        addLocationDto.setLatitude(randomLatitude());
        addLocationDto.setLongitude(randomLongitude());
        locationActor.addLocation(addLocationDto);
    }

    @Then("the location has been added successfully")
    public void verifyLocationAdded() throws JsonProcessingException {
        Truth.assertThat(locationActor.getHttpResponseStatus()).isEqualTo(HttpStatus.CREATED);

        LocationDto locationDto = locationActor.getHttpResponseBodyAs(LocationDto.class);
        Truth.assertThat(locationDto.getId()).isNotNull();
        Truth.assertThat(locationDto.getName()).isEqualTo(addLocationDto.getName());
        Truth.assertThat(locationDto.getAddress()).isEqualTo(addLocationDto.getAddress());
        Truth.assertThat(locationDto.getLatitude()).isEqualTo(addLocationDto.getLatitude());
        Truth.assertThat(locationDto.getLongitude()).isEqualTo(addLocationDto.getLongitude());
    }

    @When("I add a second location with the same name as the test location")
    public void addingSecondLocationWithSameNameAsDefaultTestLocation() throws IOException, InterruptedException {
        locationActor.getLocationById(locationActor.getDefaultTestLocationId());
        String nameOfDefaultTestLocation = locationActor.getHttpResponseBodyAs(LocationDto.class).getName();

        addLocationDto = new AddLocationDto();
        addLocationDto.setName(nameOfDefaultTestLocation);
        addLocationDto.setAddress(randomAddress());
        addLocationDto.setLatitude(randomLatitude());
        addLocationDto.setLongitude(randomLongitude());
        locationActor.addLocation(addLocationDto);
    }
}
