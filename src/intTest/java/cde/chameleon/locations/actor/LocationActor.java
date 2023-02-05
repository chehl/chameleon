package cde.chameleon.locations.actor;

import cde.chameleon.api.actor.ApiActor;
import cde.chameleon.locations.api.AddLocationDto;
import cde.chameleon.locations.api.LocationRoles;
import cde.chameleon.locations.api.PatchLocationDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.spring.ScenarioScope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.util.HashMap;
import java.util.Map;

@Component
@ScenarioScope
public class LocationActor extends ApiActor {

    public static final String DEFAULT_ALIAS = "default";

    private final Map<String, String> testLocationIdsByAlias = new HashMap<>();

    public LocationActor() {
        super("api/v1/locations");
    }

    public void logInWithRole(String role) {
        switch (role) {
            case LocationRoles.WRITE -> logIn("admin", "admin");
            case LocationRoles.READ -> logIn("user", "user");
            default -> logIn("guest", "guest");
        }
    }

    public void addTestLocation(String alias, String id) {
        testLocationIdsByAlias.put(alias, id);
    }

    public String getTestLocationId(String alias) {
        return testLocationIdsByAlias.get(alias);
    }

    public String getDefaultTestLocationId() {
        return testLocationIdsByAlias.get(DEFAULT_ALIAS);
    }

    public void getLocations() throws IOException, InterruptedException {
        sendRequest(newHttpRequest("")
                .GET()
                .build());
    }

    public void getLocationById(String id) throws IOException, InterruptedException {
        sendRequest(newHttpRequest("/" + id)
                .GET()
                .build());
    }

    public void addLocation(AddLocationDto addLocationDto) throws IOException, InterruptedException {
        sendRequest(newHttpRequest("")
                .POST(HttpRequest.BodyPublishers.ofString(new ObjectMapper().writeValueAsString(addLocationDto)))
                .build());
    }

    public void deleteLocation(String id) throws IOException, InterruptedException {
        sendRequest(newHttpRequest("/" + id)
                .DELETE()
                .build());
    }

    public void updateLocation(PatchLocationDto patchLocationDto) throws IOException, InterruptedException {
        sendRequest(newHttpRequest("")
                .method("PATCH",
                        HttpRequest.BodyPublishers.ofString(new ObjectMapper().writeValueAsString(patchLocationDto)))
                .build());
    }

    public void getDistance(String id1, String id2) throws IOException, InterruptedException {
        sendRequest(newHttpRequest("/" + id1 + "/distanceto/" + id2)
                .GET()
                .build());
    }
}
