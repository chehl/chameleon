package cde.chameleon.locations.steps;

import cde.chameleon.locations.actor.LocationActor;
import cde.chameleon.locations.api.AddLocationDto;
import cde.chameleon.locations.api.LocationDto;
import cde.chameleon.locations.api.LocationRoles;
import io.cucumber.java.en.Given;

import java.io.IOException;

import static cde.chameleon.locations.domain.LocationRandom.*;

public class LocationSteps {

    private final LocationActor locationActor;

    public LocationSteps(LocationActor locationActor) {
        this.locationActor = locationActor;
    }

    @Given("I am logged in as user with role to read locations")
    public void loggingInWithRoleRead() {
        locationActor.logInWithRole(LocationRoles.READ);
    }

    @Given("I am logged in as user with role to write locations")
    public void loggingInWithRoleWrite() {
        locationActor.logInWithRole(LocationRoles.WRITE);
    }

    @Given("I am logged in as a guest")
    public void loggingInWithoutRole() {
        locationActor.logIn("guest", "guest");
    }

    @Given("I have added a test location")
    public void havingAddedLocation() throws IOException, InterruptedException {
        havingAddedLocation(LocationActor.DEFAULT_ALIAS);
    }

    @Given("I have added a test location {string}")
    public void havingAddedLocation(String alias) throws IOException, InterruptedException {
        locationActor.logInWithRole(LocationRoles.WRITE);
        AddLocationDto addLocationDto = new AddLocationDto();
        addLocationDto.setName(randomName());
        addLocationDto.setAddress(randomAddress());
        addLocationDto.setLatitude(randomLatitude());
        addLocationDto.setLongitude(randomLongitude());
        locationActor.addLocation(addLocationDto);
        locationActor.addTestLocation(alias, locationActor.getHttpResponseBodyAs(LocationDto.class).getId());
        locationActor.logOut();
    }
}
