package cde.chameleon.locations.api;

import cde.chameleon.ChameleonDisplayNameGenerator;
import cde.chameleon.api.etag.NotModifiedETagException;
import cde.chameleon.api.etag.OutdatedETagException;
import cde.chameleon.locations.domain.Location;
import cde.chameleon.locations.domain.LocationDomainService;
import cde.chameleon.locations.domain.LocationRepository;
import com.google.common.truth.Truth;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.UUID;

import static cde.chameleon.locations.domain.LocationRandom.randomLocation;
import static cde.chameleon.locations.domain.LocationRandom.randomName;

@DisplayNameGeneration(ChameleonDisplayNameGenerator.class)
@ExtendWith(MockitoExtension.class)
class ETagLocationControllerTest {

    @Mock
    private LocationRepository locationRepository;

    @Test
    void givenRepositoryWithLocation_whenGettingLocationByIdWithSameEtag_thenReturnsNotModified() {
        // given
        LocationController locationController = new LocationController(new ModelMapper(), new LocationDomainService(locationRepository));
        Location expectedLocation = randomLocation();
        Mockito.when(locationRepository.findById(expectedLocation.getId()))
                .thenReturn(expectedLocation);
        LocationDto expectedLocationDto = new ModelMapper().map(expectedLocation, LocationDto.class);
        expectedLocationDto.updateETag();
        String expectedId = expectedLocationDto.getId();
        String ifNoneMatchHeader = expectedLocationDto.getETag();

        // when
        NotModifiedETagException e = Assertions.assertThrows(
                NotModifiedETagException.class, () -> locationController.getLocationById(ifNoneMatchHeader, expectedId));

        // then
        Truth.assertThat(e.getETag()).isEqualTo(ifNoneMatchHeader);
    }

    @Test
    void givenRepositoryWithLocation_whenGettingLocationByIdWithDifferentEtag_thenReturnsDto() {
        // given
        LocationController locationController = new LocationController(new ModelMapper(), new LocationDomainService(locationRepository));
        Location expectedLocation = randomLocation();
        Mockito.when(locationRepository.findById(expectedLocation.getId()))
                .thenReturn(expectedLocation);
        LocationDto expectedLocationDto = new ModelMapper().map(expectedLocation, LocationDto.class);
        expectedLocationDto.updateETag();
        String ifNoneMatchHeader = UUID.randomUUID().toString();

        // when
        LocationDto actualLocationDto = locationController.getLocationById(ifNoneMatchHeader, expectedLocationDto.getId());

        // then
        Truth.assertThat(actualLocationDto.getId()).isEqualTo(expectedLocationDto.getId());
        Truth.assertThat(actualLocationDto.getETag()).isEqualTo(expectedLocationDto.getETag());
    }

    @Test
    void givenRepositoryWithLocation_whenDeletingLocationByIdWithSameEtag_thenDeletesLocationWithProvidedId() {
        // given
        LocationController locationController = new LocationController(new ModelMapper(), new LocationDomainService(locationRepository));
        Location expectedLocation = randomLocation();
        UUID expectedId = expectedLocation.getId();
        Mockito.when(locationRepository.findById(expectedLocation.getId()))
                .thenReturn(expectedLocation);
        Mockito.doNothing()
                .when(locationRepository).deleteById(expectedId);
        LocationDto expectedLocationDto = new ModelMapper().map(expectedLocation, LocationDto.class);
        expectedLocationDto.updateETag();
        String ifMatchHeader = expectedLocationDto.getETag();

        // when
        locationController.deleteLocationById(ifMatchHeader, expectedLocationDto.getId());

        // then
        Mockito.verify(locationRepository, Mockito.times(1)).deleteById(expectedId);
    }

    @Test
    void givenRepositoryWithLocation_whenDeletingLocationByIdWithDifferentEtag_thenReturnsOutdated() {
        // given
        LocationController locationController = new LocationController(new ModelMapper(), new LocationDomainService(locationRepository));
        Location expectedLocation = randomLocation();
        Mockito.when(locationRepository.findById(expectedLocation.getId()))
                .thenReturn(expectedLocation);
        LocationDto expectedLocationDto = new ModelMapper().map(expectedLocation, LocationDto.class);
        expectedLocationDto.updateETag();
        String expectedId = expectedLocationDto.getId();
        String ifMatchHeader = UUID.randomUUID().toString();

        // when
        OutdatedETagException e = Assertions.assertThrows(
                OutdatedETagException.class, () -> locationController.deleteLocationById(ifMatchHeader, expectedId));

        // then
        Truth.assertThat(e.getIfMatch()).isEqualTo(ifMatchHeader);
        Truth.assertThat(e.getActualETag()).isEqualTo(expectedLocationDto.getETag());
    }

    @Test
    void givenRepositoryWithLocation_whenUpdatingLocationByIdWithSameEtag_thenUpdatesLocation() {
        // given
        LocationController locationController = new LocationController(new ModelMapper(), new LocationDomainService(locationRepository));
        Location existingLocation = randomLocation();
        Mockito.when(locationRepository.findById(existingLocation.getId()))
                .thenReturn(existingLocation);
        Mockito.doNothing()
                .when(locationRepository).save(Mockito.any());
        LocationDto existingLocationDto = new ModelMapper().map(existingLocation, LocationDto.class);
        existingLocationDto.updateETag();
        String ifMatchHeader = existingLocationDto.getETag();
        PatchLocationDto patchLocationDto = new PatchLocationDto();
        patchLocationDto.setId(existingLocation.getId().toString());
        patchLocationDto.setName(randomName());

        // when
        LocationDto actualLocationDto = locationController.patchLocation(ifMatchHeader, patchLocationDto);

        // then
        Truth.assertThat(actualLocationDto.getId()).isEqualTo(existingLocationDto.getId());
        Truth.assertThat(actualLocationDto.getName()).isEqualTo(patchLocationDto.getName());
        Truth.assertThat(actualLocationDto.getETag()).isNotEqualTo(existingLocationDto.getETag());
    }

    @Test
    void givenRepositoryWithLocation_whenUpdatingLocationByIdWithDifferentEtag_thenReturnsOutdated() {
        // given
        LocationController locationController = new LocationController(new ModelMapper(), new LocationDomainService(locationRepository));
        Location existingLocation = randomLocation();
        Mockito.when(locationRepository.findById(existingLocation.getId()))
                .thenReturn(existingLocation);
        LocationDto existingLocationDto = new ModelMapper().map(existingLocation, LocationDto.class);
        existingLocationDto.updateETag();
        String ifMatchHeader = UUID.randomUUID().toString();
        PatchLocationDto patchLocationDto = new PatchLocationDto();
        patchLocationDto.setId(existingLocation.getId().toString());
        patchLocationDto.setName(randomName());

        // when
        OutdatedETagException e = Assertions.assertThrows(
                OutdatedETagException.class, () -> locationController.patchLocation(ifMatchHeader, patchLocationDto));

        // then
        Truth.assertThat(e.getIfMatch()).isEqualTo(ifMatchHeader);
        Truth.assertThat(e.getActualETag()).isEqualTo(existingLocationDto.getETag());
    }
}
