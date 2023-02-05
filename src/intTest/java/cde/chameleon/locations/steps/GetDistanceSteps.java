package cde.chameleon.locations.steps;

import cde.chameleon.locations.actor.LocationActor;
import cde.chameleon.locations.api.AddLocationDto;
import cde.chameleon.locations.api.LocationDto;
import cde.chameleon.locations.api.LocationRoles;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.truth.Truth;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.math.BigDecimal;

import static cde.chameleon.locations.domain.LocationRandom.*;

public class GetDistanceSteps {

    private final LocationActor locationActor;

    public GetDistanceSteps(LocationActor locationActor) {
        this.locationActor = locationActor;
    }

    @Given("I have added test location {string} with latitude {float} and longitude {float}")
    public void havingAddedLocation(String alias, float latitude, float longitude) throws IOException, InterruptedException {
        locationActor.logInWithRole(LocationRoles.WRITE);
        AddLocationDto addLocationDto = new AddLocationDto();
        addLocationDto.setName(randomName());
        addLocationDto.setAddress(randomAddress());
        addLocationDto.setLatitude(BigDecimal.valueOf(latitude));
        addLocationDto.setLongitude(BigDecimal.valueOf(longitude));
        locationActor.addLocation(addLocationDto);
        locationActor.addTestLocation(alias, locationActor.getHttpResponseBodyAs(LocationDto.class).getId());
        locationActor.logOut();
    }

    @When("I get the distance between locations {string} and {string}")
    public void gettingDistance(String alias1, String alias2) throws IOException, InterruptedException {
        locationActor.getDistance(locationActor.getTestLocationId(alias1), locationActor.getTestLocationId(alias2));
    }

    @Then("the distance is returned as about {float} kilometers")
    public void verifyDistance(float distance) throws JsonProcessingException {
        Truth.assertThat(locationActor.getHttpResponseStatus()).isEqualTo(HttpStatus.OK);
        Truth.assertThat(locationActor.getHttpResponseBodyAs(Float.class)).isWithin(1.0f).of(distance);
    }
}
