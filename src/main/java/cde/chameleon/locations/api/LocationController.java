package cde.chameleon.locations.api;

import cde.chameleon.api.ApiErrorDto;
import cde.chameleon.api.IsUUID;
import cde.chameleon.api.etag.NotModifiedETagException;
import cde.chameleon.api.etag.OutdatedETagException;
import cde.chameleon.locations.domain.Location;
import cde.chameleon.locations.domain.LocationDomainService;
import cde.chameleon.locations.domain.exception.NameNotUniqueLocationException;
import cde.chameleon.locations.domain.exception.NotFoundLocationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Tag(name = "Location", description = "Location API")
@Transactional(readOnly = true)
@RestController
@RequestMapping("api/v1/locations")
@Slf4j
public class LocationController {
    private static final String LOCATION_ID_KEY = "location_id";
    private static final String OTHER_LOCATION_KEY = "other_location_id";
    private static final String LOCATION_NAME_KEY = "location_name";
    private static final String LOCATION_COUNT_KEY = "location_count";
    private static final String DISTANCE_KEY = "distance";

    private final ModelMapper modelMapper;
    private final LocationDomainService locationDomainService;

    @Autowired
    public LocationController(ModelMapper modelMapper, LocationDomainService locationDomainService) {
        this.modelMapper = modelMapper;
        this.locationDomainService = locationDomainService;
    }

    private LocationDto map(Location location) {
        LocationDto locationDto = modelMapper.map(location, LocationDto.class);
        locationDto.updateETag();
        return locationDto;
    }

    private List<LocationDto> map(List<Location> locations) {
        List<LocationDto> locationDtos = modelMapper.map(locations, new TypeToken<List<LocationDto>>() {}.getType());
        locationDtos.forEach(LocationDto::updateETag);
        return locationDtos;
    }

    @Operation(
            summary = "Get all locations",
            description = "This operations returns all locations.")
    @ApiResponse(
            responseCode = "200",
            description = "Successfully returned all locations")
    @GetMapping(
            value = "",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed(LocationRoles.READ)
    public List<LocationDto> getLocations() {
        log.info("Get all locations");
        List<LocationDto> locationDtos = map(locationDomainService.getLocations());
        log.info("Found all locations with {}", kv(LOCATION_COUNT_KEY, locationDtos.size()));
        return locationDtos;
    }

    @Operation(
            summary = "Get location by id",
            description = "This operation returns a location by id.")
    @Parameter(name = "id", example = "c874656b-7906-42d5-bcb0-23a9aba6b56d")
    @Parameter(in = ParameterIn.HEADER, name = "If-None-Match", schema = @Schema(type = "string"))
    @ApiResponse(
            responseCode = "200",
            description = "Successfully returned the location with the specified id")
    @GetMapping(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed(LocationRoles.READ)
    public LocationDto getLocationById(
            @RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch,
            @Valid @IsUUID @PathVariable String id) {
        log.info("Get location with {}", kv(LOCATION_ID_KEY, id));
        LocationDto locationDto = map(locationDomainService.getLocationById(UUID.fromString(id)));
        // identify unchanged entity via ETag
        if (ifNoneMatch != null && ifNoneMatch.equals(locationDto.getETag())) {
            throw new NotModifiedETagException(ifNoneMatch);
        }
        log.info("Found location with {} and {}",
                kv(LOCATION_ID_KEY, locationDto.getId()), kv(LOCATION_NAME_KEY, locationDto.getName()));
        return locationDto;
    }

    @Operation(
            summary = "Get location by name",
            description = "This operation returns a location by name.")
    @Parameter(name = "name", example = "name of location")
    @Parameter(in = ParameterIn.HEADER, name = "If-None-Match", schema = @Schema(type = "string"))
    @ApiResponse(
            responseCode = "200",
            description = "Successfully returned the location with the specified name")
    @GetMapping(
            value = "/name/{name}", //api/v1/locations/name/MyLocationName
            produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed(LocationRoles.READ)
    public LocationDto getLocationByName(
            @RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch,
            @PathVariable String name) {
        log.info("Get location with {}", kv(LOCATION_NAME_KEY, name));

        LocationDto locationDto = map(locationDomainService.getLocationByName(name));

        // identify unchanged entity via ETag
        if (ifNoneMatch != null && ifNoneMatch.equals(locationDto.getETag())) {
            throw new NotModifiedETagException(ifNoneMatch);
        }

        log.info("Found location with {} and {}",
                kv(LOCATION_ID_KEY, locationDto.getId()), kv(LOCATION_NAME_KEY, locationDto.getName()));
        return locationDto;
    }

    @Operation(
            summary = "Add new location",
            description = "This operation adds a new location.")
    @ApiResponse(
            responseCode = "201",
            description = "Successfully added a new location")
    @Transactional
    @PostMapping(
            value = "",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @RolesAllowed(LocationRoles.WRITE)
    public LocationDto addLocation(@Valid @RequestBody AddLocationDto addLocationDto) {
        log.info("Add new location with {}", kv(LOCATION_NAME_KEY, addLocationDto.getName()));
        LocationDto newLocationDto = map(locationDomainService.addLocation(
                addLocationDto.getName(), addLocationDto.getAddress(),
                addLocationDto.getLatitude(), addLocationDto.getLongitude()));
        log.info("Created new location with {} and {}",
                kv(LOCATION_ID_KEY, newLocationDto.getId()), kv(LOCATION_NAME_KEY, newLocationDto.getName()));
        return newLocationDto;
    }

    private void checkForOutdatedETag(String expectedETag, Location existingLocation) {
        String existingETag = map(existingLocation).getETag();
        if (expectedETag != null && !expectedETag.equals(existingETag)) {
            throw new OutdatedETagException(expectedETag, existingETag);
        }
    }

    @Operation(
            summary = "Delete location by id",
            description = "This operation deletes a location by id.")
    @Parameter(name = "id", example = "c874656b-7906-42d5-bcb0-23a9aba6b56d")
    @Parameter(in = ParameterIn.HEADER, name = "If-Match", schema = @Schema(type = "string"))
    @ApiResponse(
            responseCode = "204",
            description = "Successfully deleted the location with the specified id")
    @Transactional
    @DeleteMapping(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RolesAllowed(LocationRoles.WRITE)
    public void deleteLocationById(
            @RequestHeader(value = "If-Match", required = false) String ifMatch,
            @Valid @IsUUID @PathVariable String id) {
        log.info("Delete location with {}", kv(LOCATION_ID_KEY, id));
        // optimistic concurrency control with ETags
        checkForOutdatedETag(ifMatch, locationDomainService.getLocationById(UUID.fromString(id)));
        locationDomainService.deleteLocationById(UUID.fromString(id));
        log.info("Deleted location with {}", kv(LOCATION_ID_KEY, id));
    }

    @Operation(
            summary = "Updates a location (partially or fully)",
            description = "This operation updates a location (partially or fully).")
    @Parameter(in = ParameterIn.HEADER, name = "If-Match", schema = @Schema(type = "string"))
    @ApiResponse(
            responseCode = "200",
            description = "Successfully updated the provided location (partially or fully)")
    @Transactional
    @PatchMapping(
            value = "",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed(LocationRoles.WRITE)
    public LocationDto patchLocation(
            @RequestHeader(value = "If-Match", required = false) String ifMatch,
            @Valid @RequestBody PatchLocationDto patchLocationDto) {
        if (patchLocationDto.getName() != null) {
            log.info("Patch location with {} and {}",
                    kv(LOCATION_ID_KEY, patchLocationDto.getId()), kv(LOCATION_NAME_KEY, patchLocationDto.getName()));
        } else {
            log.info("Patch location with {}", kv(LOCATION_ID_KEY, patchLocationDto.getId()));
        }
        Location existingLocation = locationDomainService.getLocationById(UUID.fromString(patchLocationDto.getId()));
        // optimistic concurrency control with ETags
        checkForOutdatedETag(ifMatch, existingLocation);
        LocationDto patchedLocationDto = map(locationDomainService.patchLocation(
                UUID.fromString(patchLocationDto.getId()), patchLocationDto.getName(),
                patchLocationDto.getAddress(), patchLocationDto.getLatitude(), patchLocationDto.getLongitude()));
        log.info("Patched location with {} and {}",
                kv(LOCATION_ID_KEY, patchedLocationDto.getId()), kv(LOCATION_NAME_KEY, patchedLocationDto.getName()));
        return patchedLocationDto;
    }

    @Operation(
            summary = "Calculates the distance in kilometers between two locations",
            description = "This operation calculates the distance in kilometers between two locations.")
    @Parameter(name = "id", example = "c874656b-7906-42d5-bcb0-23a9aba6b56d")
    @Parameter(name = "otherId", example = "c874656b-7906-42d5-bcb0-23a9aba6b56d")
    @ApiResponse(
            responseCode = "200",
            description = "Successfully calculated the distance in kilometers between two provided locations")
    @GetMapping(
            value = "/{id}/distanceto/{otherId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed(LocationRoles.READ)
    public double getDistance(@Valid @IsUUID @PathVariable String id, @Valid @IsUUID @PathVariable String otherId) {
        log.info("Calculate distance in kilometers between location with {} and other location with {}",
                kv(LOCATION_ID_KEY, id), kv(OTHER_LOCATION_KEY, otherId));
        double distance = locationDomainService.getDistance(UUID.fromString(id), UUID.fromString(otherId));
        log.info("Calculated distance in kilometers between location with {} and other location with {} as {}",
                kv(LOCATION_ID_KEY, id), kv(OTHER_LOCATION_KEY, otherId), kv(DISTANCE_KEY, distance));
        return distance;
    }

    @ApiResponse(
            responseCode = "404",
            description = "Location not found")
    @ExceptionHandler(NotFoundLocationException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorDto handleNotFoundUserException(NotFoundLocationException notFoundLocationException) {
        log.info("Not found location with {}", kv(LOCATION_ID_KEY, notFoundLocationException.getId()));
        return new ApiErrorDto(notFoundLocationException.getMessage());
    }

    @ApiResponse(
            responseCode = "422",
            description = "Name not unique")
    @ExceptionHandler(NameNotUniqueLocationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ApiErrorDto handleNameNotUniqueLocationException(
            NameNotUniqueLocationException nameNotUniqueLocationException) {
        log.info("Name not unique with {} for {}",
                kv(LOCATION_NAME_KEY, nameNotUniqueLocationException.getName()),
                kv(LOCATION_ID_KEY, nameNotUniqueLocationException.getId()));
        return new ApiErrorDto(nameNotUniqueLocationException.getMessage());
    }
}