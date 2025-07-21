package cde.chameleon.locations.api;

import cde.chameleon.ChameleonDisplayNameGenerator;
import cde.chameleon.api.ApiErrorDto;
import cde.chameleon.locations.domain.Location;
import cde.chameleon.locations.domain.LocationDomainService;
import cde.chameleon.locations.domain.LocationRepository;
import cde.chameleon.locations.domain.Locations;
import cde.chameleon.locations.domain.exception.NameNotUniqueLocationException;
import cde.chameleon.locations.domain.exception.NotFoundLocationException;
import com.google.common.truth.Truth;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static cde.chameleon.locations.domain.LocationRandom.*;

@DisplayNameGeneration(ChameleonDisplayNameGenerator.class)
@ExtendWith(MockitoExtension.class)
class LocationControllerTest {

    @Mock
    private LocationRepository locationRepository;

    private void assertSameData(LocationDto locationDto, Location location) {
        Truth.assertThat(locationDto.getId()).isEqualTo(location.getId().toString());
        Truth.assertThat(locationDto.getName()).isEqualTo(location.getName());
        Truth.assertThat(locationDto.getAddress()).isEqualTo(location.getAddress());
        Truth.assertThat(locationDto.getLatitude()).isEqualTo(location.getLatitude());
        Truth.assertThat(locationDto.getLongitude()).isEqualTo(location.getLongitude());
    }

    @Test
    void givenEmptyRepository_whenGettingLocations_thenReturnsEmptyList() {
        // given
        LocationController locationController = new LocationController(new ModelMapper(), new LocationDomainService(locationRepository));
        Mockito.when(locationRepository.findAll())
                .thenReturn(Collections.emptyList());

        // when
        List<LocationDto> locationDtos = locationController.getLocations();

        // then
        Truth.assertThat(locationDtos.isEmpty()).isTrue();
    }

    @Test
    void givenRepositoryWithLocations_whenGettingLocations_thenReturnsListWithCorrespondingDtos() {
        // given
        LocationController locationController = new LocationController(new ModelMapper(), new LocationDomainService(locationRepository));
        Location expectedLocation1 = randomLocation();
        Location expectedLocation2 = randomLocation();
        Mockito.when(locationRepository.findAll())
                .thenReturn(List.of(expectedLocation1, expectedLocation2));

        // when
        List<LocationDto> locationDtos = locationController.getLocations();

        // then
        Truth.assertThat(locationDtos.size()).isEqualTo(2);
        assertSameData(locationDtos.get(0), expectedLocation1);
        assertSameData(locationDtos.get(1), expectedLocation2);
        Truth.assertThat(locationDtos.get(0).getETag()).isNotNull();
        Truth.assertThat(locationDtos.get(1).getETag()).isNotNull();
    }

    @Test
    void givenRepositoryWithLocation_whenGettingLocationById_thenReturnsCorrespondingDto() {
        // given
        LocationController locationController = new LocationController(new ModelMapper(), new LocationDomainService(locationRepository));
        Location expectedLocation = randomLocation();
        Mockito.when(locationRepository.findById(expectedLocation.getId()))
                .thenReturn(expectedLocation);

        // when
        LocationDto locationDto = locationController.getLocationById(null, expectedLocation.getId().toString());

        // then
        assertSameData(locationDto, expectedLocation);
        Truth.assertThat(locationDto.getETag()).isNotNull();
    }

    @Test
    void givenLocationData_whenAddingLocation_thenSavesAndReturnsNewLocation() {
        // given
        LocationController locationController = new LocationController(new ModelMapper(), new LocationDomainService(locationRepository));
        AddLocationDto addLocationDto = new AddLocationDto();
        addLocationDto.setName(randomName());
        addLocationDto.setAddress(randomAddress());
        addLocationDto.setLatitude(randomLatitude());
        addLocationDto.setLongitude(randomLongitude());
        Mockito.doNothing()
                .when(locationRepository).save(Mockito.any());

        // when
        LocationDto returnedLocation = locationController.addLocation(addLocationDto);

        // then
        ArgumentCaptor<Location> savedLocationArgumentCaptor = ArgumentCaptor.forClass(Location.class);
        Mockito.verify(locationRepository, Mockito.times(1))
                .save(savedLocationArgumentCaptor.capture());

        Location savedLocation = savedLocationArgumentCaptor.getValue();
        Truth.assertThat(savedLocation.getId()).isNotNull();
        Truth.assertThat(savedLocation.getName()).isEqualTo(addLocationDto.getName());
        Truth.assertThat(savedLocation.getAddress()).isEqualTo(addLocationDto.getAddress());
        Truth.assertThat(savedLocation.getLatitude()).isEqualTo(addLocationDto.getLatitude());
        Truth.assertThat(savedLocation.getLongitude()).isEqualTo(addLocationDto.getLongitude());

        Truth.assertThat(returnedLocation.getId()).isEqualTo(savedLocation.getId().toString());
        Truth.assertThat(returnedLocation.getName()).isEqualTo(addLocationDto.getName());
        Truth.assertThat(returnedLocation.getAddress()).isEqualTo(addLocationDto.getAddress());
        Truth.assertThat(returnedLocation.getLongitude()).isEqualTo(addLocationDto.getLongitude());
        Truth.assertThat(returnedLocation.getLatitude()).isEqualTo(addLocationDto.getLatitude());
        Truth.assertThat(returnedLocation.getETag()).isNotNull();
    }

    @Test
    void givenLocationId_whenDeletingLocationById_thenDeletesLocationWithProvidedId() {
        // given
        LocationController locationController = new LocationController(new ModelMapper(), new LocationDomainService(locationRepository));
        Location expectedLocation = randomLocation();
        UUID expectedId = expectedLocation.getId();
        Mockito.when(locationRepository.findById(expectedLocation.getId()))
                .thenReturn(expectedLocation);
        Mockito.doNothing()
                .when(locationRepository).deleteById(expectedId);

        // when
        locationController.deleteLocationById(null, expectedId.toString());

        // then
        Mockito.verify(locationRepository, Mockito.times(1)).deleteById(expectedId);
    }

    @SuppressWarnings("unused") // used by parameterized test
    public static Stream<PatchLocationDto> givenLocationData_whenUpdatingLocation_thenUpdatesLocationWithProvidedLocationData() {
        // test update of full location data
        PatchLocationDto fullLocationData = new PatchLocationDto();
        fullLocationData.setId(UUID.randomUUID().toString());
        fullLocationData.setName(randomName());
        fullLocationData.setAddress(randomAddress());
        fullLocationData.setLatitude(randomLatitude());
        fullLocationData.setLongitude(randomLongitude());

        // test patch of name
        PatchLocationDto onlyName = new PatchLocationDto();
        onlyName.setId(UUID.randomUUID().toString());
        onlyName.setName(randomName());

        // test patch of address
        PatchLocationDto onlyAddress = new PatchLocationDto();
        onlyAddress.setId(UUID.randomUUID().toString());
        onlyAddress.setAddress(randomAddress());

        // test patch of latitude
        PatchLocationDto onlyLatitude = new PatchLocationDto();
        onlyLatitude.setId(UUID.randomUUID().toString());
        onlyLatitude.setLatitude(randomLatitude());

        // test patch of longitude
        PatchLocationDto onlyLongitude = new PatchLocationDto();
        onlyLongitude.setId(UUID.randomUUID().toString());
        onlyLongitude.setLongitude(randomLongitude());

        return Stream.of(fullLocationData, onlyName, onlyAddress, onlyLatitude, onlyLongitude);
    }

    @ParameterizedTest
    @MethodSource
    void givenLocationData_whenUpdatingLocation_thenUpdatesLocationWithProvidedLocationData(PatchLocationDto patchLocationDto) {
        // given
        LocationController locationController = new LocationController(new ModelMapper(), new LocationDomainService(locationRepository));
        Location existingLocation = randomLocation(UUID.fromString(patchLocationDto.getId()));
        Mockito.when(locationRepository.findById(existingLocation.getId()))
                .thenReturn(existingLocation);
        Mockito.doNothing()
                .when(locationRepository).save(Mockito.any());
        Location expectedLocation = new Location(
                existingLocation.getId(),
                patchLocationDto.getName() != null ? patchLocationDto.getName() : existingLocation.getName(),
                patchLocationDto.getAddress() != null ? patchLocationDto.getAddress() : existingLocation.getAddress(),
                patchLocationDto.getLatitude() != null ? patchLocationDto.getLatitude() : existingLocation.getLatitude(),
                patchLocationDto.getLongitude() != null ? patchLocationDto.getLongitude() : existingLocation.getLongitude());

        // when
        LocationDto returnedLocation = locationController.patchLocation(null, patchLocationDto);

        // then
        Mockito.verify(locationRepository, Mockito.times(1)).save(expectedLocation);
        Truth.assertThat(returnedLocation.getId()).isEqualTo(patchLocationDto.getId());
        if (patchLocationDto.getName() != null) {
            Truth.assertThat(returnedLocation.getName()).isEqualTo(patchLocationDto.getName());
        } else {
            Truth.assertThat(returnedLocation.getName()).isEqualTo(existingLocation.getName());
        }
        if (patchLocationDto.getAddress() != null) {
            Truth.assertThat(returnedLocation.getAddress()).isEqualTo(patchLocationDto.getAddress());
        } else {
            Truth.assertThat(returnedLocation.getAddress()).isEqualTo(existingLocation.getAddress());
        }
        if (patchLocationDto.getLatitude() != null) {
            Truth.assertThat(returnedLocation.getLatitude()).isEqualTo(patchLocationDto.getLatitude());
        } else {
            Truth.assertThat(returnedLocation.getLatitude()).isEqualTo(existingLocation.getLatitude());
        }
        if (patchLocationDto.getLongitude() != null) {
            Truth.assertThat(returnedLocation.getLongitude()).isEqualTo(patchLocationDto.getLongitude());
        } else {
            Truth.assertThat(returnedLocation.getLongitude()).isEqualTo(existingLocation.getLongitude());
        }
        Truth.assertThat(returnedLocation.getETag()).isNotNull();
    }

    @Test
    void givenTwoLocationIds_whenGettingDistance_thenReturnsCorrectDistanceBetweenCorrespondingLocations() {
        // given
        LocationController locationController = new LocationController(new ModelMapper(), new LocationDomainService(locationRepository));
        Location hamburg = Locations.HAMBURG;
        Location munich = Locations.MUNICH;
        Mockito.when(locationRepository.findById(hamburg.getId()))
                .thenReturn(hamburg);
        Mockito.when(locationRepository.findById(munich.getId()))
                .thenReturn(munich);

        // when
        double actualDistance = locationController.getDistance(hamburg.getId().toString(), munich.getId().toString());

        // then
        Truth.assertThat(actualDistance).isWithin(1.0).of(Locations.DISTANCE_HAMBURG_MUNICH);
    }

    private void assertResponseStatus(String method, Class<? extends Exception> exception, HttpStatus expectedHttpStatus) {
        try {
            ResponseStatus responseStatus = LocationController.class.getDeclaredMethod(method, exception).getAnnotation(ResponseStatus.class);
            Truth.assertThat(responseStatus).isNotNull();
            Truth.assertThat(responseStatus.value()).isEqualTo(expectedHttpStatus);
        } catch (NoSuchMethodException e) {
            Assertions.fail();
        }
    }

    @Test
    void givenNotFoundLocationException_whenHandlingException_thenReturnsHttpStatusNotFound() {
        // given
        LocationController locationController = new LocationController(new ModelMapper(), new LocationDomainService(locationRepository));
        UUID expectedId = UUID.randomUUID();
        NotFoundLocationException e = new NotFoundLocationException(expectedId);

        // when
        ApiErrorDto apiErrorDto = locationController.handleNotFoundUserException(e);

        // then
        assertResponseStatus("handleNotFoundUserException", NotFoundLocationException.class,
                HttpStatus.NOT_FOUND);
        Truth.assertThat(apiErrorDto).isNotNull();
        Truth.assertThat(apiErrorDto.getMessage()).contains(expectedId.toString());
    }

    @Test
    void givenNameNotUniqueLocationException_whenHandlingException_thenReturnsHttpStatusUnprocessableEntity() {
        // given
        LocationController locationController = new LocationController(new ModelMapper(), new LocationDomainService(locationRepository));
        UUID expectedId = UUID.randomUUID();
        String expectedName = randomName();
        NameNotUniqueLocationException e = new NameNotUniqueLocationException(expectedId, expectedName);

        // when
        ApiErrorDto apiErrorDto = locationController.handleNameNotUniqueLocationException(e);

        // then
        assertResponseStatus("handleNameNotUniqueLocationException", NameNotUniqueLocationException.class,
                HttpStatus.UNPROCESSABLE_ENTITY);
        Truth.assertThat(apiErrorDto).isNotNull();
        Truth.assertThat(apiErrorDto.getMessage()).contains(expectedId.toString());
        Truth.assertThat(apiErrorDto.getMessage()).contains(expectedName);
    }
}
