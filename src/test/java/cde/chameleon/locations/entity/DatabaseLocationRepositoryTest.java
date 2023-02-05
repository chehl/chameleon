package cde.chameleon.locations.entity;

import cde.chameleon.ChameleonDisplayNameGenerator;
import cde.chameleon.locations.domain.Location;
import cde.chameleon.locations.domain.exception.NameNotUniqueLocationException;
import cde.chameleon.locations.domain.exception.NotFoundLocationException;
import com.google.common.truth.Truth;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static cde.chameleon.locations.domain.LocationRandom.*;

@DisplayNameGeneration(ChameleonDisplayNameGenerator.class)
@ExtendWith(MockitoExtension.class)
class DatabaseLocationRepositoryTest {

    @Mock
    private LocationJpaRepository locationJpaRepository;

    private static LocationEntity randomLocationEntity() {
        return randomLocationEntity(UUID.randomUUID(), randomName());
    }

    private static LocationEntity randomLocationEntity(UUID id, String name) {
        return new LocationEntity(id, randomOptLock(), name, randomAddress(), randomLatitude(), randomLongitude());
    }

    private static LocationEntity toLocationEntity(Location location) {
        return new LocationEntity(
                location.getId(), randomOptLock(), location.getName(), location.getAddress(),
                location.getLatitude(), location.getLongitude());
    }

    private static void assertSameData(Location location, LocationEntity locationEntity) {
        Truth.assertThat(location.getId()).isEqualTo(locationEntity.getId());
        Truth.assertThat(location.getName()).isEqualTo(locationEntity.getName());
        Truth.assertThat(location.getAddress()).isEqualTo(locationEntity.getAddress());
        Truth.assertThat(location.getLatitude()).isEqualTo(locationEntity.getLatitude());
        Truth.assertThat(location.getLongitude()).isEqualTo(locationEntity.getLongitude());
    }

    @Nested
    class FindAll {

        @Test
        void givenEmptyRepository_whenFindingAll_thenReturnsEmptyList() {
            // given
            DatabaseLocationRepository databaseLocationRepository = new DatabaseLocationRepository(
                    new ModelMapper(), locationJpaRepository);
            Mockito.when(locationJpaRepository.findAll())
                    .thenReturn(Collections.emptyList());

            // when
            List<Location> locations = databaseLocationRepository.findAll();

            // then
            Truth.assertThat(locations.isEmpty()).isTrue();
        }

        @Test
        void givenRepositoryWithEntities_whenFindingAll_thenReturnsListWithCorrespondingLocations() {
            // given
            DatabaseLocationRepository databaseLocationRepository = new DatabaseLocationRepository(
                    new ModelMapper(), locationJpaRepository);
            LocationEntity expectedLocationEntity1 = randomLocationEntity();
            LocationEntity expectedLocationEntity2 = randomLocationEntity();
            Mockito.when(locationJpaRepository.findAll())
                    .thenReturn(List.of(expectedLocationEntity1, expectedLocationEntity2));

            // when
            List<Location> locations = databaseLocationRepository.findAll();

            // then
            Truth.assertThat(locations.size()).isEqualTo(2);
            assertSameData(locations.get(0), expectedLocationEntity1);
            assertSameData(locations.get(1), expectedLocationEntity2);
        }
    }

    @Nested
    class FindById {

        @Test
        void givenRepositoryWithoutEntityWithGivenId_whenFindingById_thenThrowsNotFoundLocationException() {
            // given
            DatabaseLocationRepository databaseLocationRepository = new DatabaseLocationRepository(
                    new ModelMapper(), locationJpaRepository);
            UUID expectedId = UUID.randomUUID();
            Mockito.when(locationJpaRepository.findById(expectedId))
                    .thenReturn(Optional.empty());

            // when
            NotFoundLocationException e = Assertions.assertThrows(
                    NotFoundLocationException.class, () -> databaseLocationRepository.findById(expectedId));

            // then
            Truth.assertThat(e.getId()).isEqualTo(expectedId);
        }

        @Test
        void givenRepositoryThatContainsEntityWithGivenId_whenFindingById_thenReturnsCorrespondingLocation() {
            // given
            DatabaseLocationRepository databaseLocationRepository = new DatabaseLocationRepository(
                    new ModelMapper(), locationJpaRepository);
            LocationEntity expectedLocationEntity = randomLocationEntity();
            UUID expectedId = expectedLocationEntity.getId();
            Mockito.when(locationJpaRepository.findById(expectedId))
                    .thenReturn(Optional.of(expectedLocationEntity));

            // when
            Location location = databaseLocationRepository.findById(expectedId);

            // then
            assertSameData(location, expectedLocationEntity);
        }

    }

    @Nested
    class DeleteById {

        @Test
        void givenRepositoryWithoutEntityWithGivenId_whenDeletingById_thenThrowsNotFoundLocationException() {
            // given
            DatabaseLocationRepository databaseLocationRepository = new DatabaseLocationRepository(
                    new ModelMapper(), locationJpaRepository);
            UUID expectedId = UUID.randomUUID();
            Mockito.doThrow(EmptyResultDataAccessException.class)
                    .when(locationJpaRepository).deleteById(expectedId);

            // when
            NotFoundLocationException e = Assertions.assertThrows(
                    NotFoundLocationException.class, () -> databaseLocationRepository.deleteById(expectedId));

            // then
            Truth.assertThat(e.getId()).isEqualTo(expectedId);
        }

        @Test
        void givenRepositoryThatContainsEntityWithGivenId_whenDeletingById_thenDeletesEntity() {
            // given
            DatabaseLocationRepository databaseLocationRepository = new DatabaseLocationRepository(
                    new ModelMapper(), locationJpaRepository);
            UUID expectedId = UUID.randomUUID();
            Mockito.doNothing()
                    .when(locationJpaRepository).deleteById(expectedId);

            // when
            databaseLocationRepository.deleteById(expectedId);

            // then
            Mockito.verify(locationJpaRepository).deleteById(expectedId);
        }

    }

    @Nested
    class Save {

        @Test
        void givenExistingLocation_whenSaving_thenUpdates() {
            // given
            DatabaseLocationRepository databaseLocationRepository = new DatabaseLocationRepository(
                    new ModelMapper(), locationJpaRepository);
            Location expectedLocation = randomLocation();
            LocationEntity expectedLocationEntity = toLocationEntity(expectedLocation);
            Mockito.when(locationJpaRepository.findLocationEntityByName(expectedLocation.getName()))
                    .thenReturn(Optional.of(randomLocationEntity(expectedLocation.getId(), expectedLocation.getName())));
            Mockito.when(locationJpaRepository.findById(expectedLocation.getId()))
                    .thenReturn(Optional.of(randomLocationEntity(expectedLocation.getId(), expectedLocation.getName())));
            Mockito.when(locationJpaRepository.save(Mockito.any()))
                    .thenReturn(expectedLocationEntity);

            // when
            databaseLocationRepository.save(expectedLocation);

            // then
            Mockito.verify(locationJpaRepository).save(expectedLocationEntity);
        }

        @Test
        void givenNewLocationWithUniqueName_whenSaving_thenSaves() {
            // given
            DatabaseLocationRepository databaseLocationRepository = new DatabaseLocationRepository(
                    new ModelMapper(), locationJpaRepository);
            Location expectedLocation = randomLocation();
            LocationEntity expectedLocationEntity = toLocationEntity(expectedLocation);
            Mockito.when(locationJpaRepository.findLocationEntityByName(expectedLocation.getName()))
                    .thenReturn(Optional.empty());
            Mockito.when(locationJpaRepository.save(Mockito.any()))
                    .thenReturn(expectedLocationEntity);

            // when
            databaseLocationRepository.save(expectedLocation);

            // then
            Mockito.verify(locationJpaRepository).save(expectedLocationEntity);
        }

        @Test
        void givenNewLocationWithNameThatIsNotUnique_whenSaving_thenThrowsNameNotUniqueLocationException() {
            // given
            DatabaseLocationRepository databaseLocationRepository = new DatabaseLocationRepository(
                    new ModelMapper(), locationJpaRepository);
            Location expectedLocation = randomLocation();
            String expectedName = expectedLocation.getName();
            Mockito.when(locationJpaRepository.findLocationEntityByName(expectedName))
                    .thenReturn(Optional.of(randomLocationEntity(UUID.randomUUID(), expectedName)));

            // when
            NameNotUniqueLocationException e = Assertions.assertThrows(
                    NameNotUniqueLocationException.class, () -> databaseLocationRepository.save(expectedLocation));

            // then
            Truth.assertThat(e.getId()).isEqualTo(expectedLocation.getId());
            Truth.assertThat(e.getName()).isEqualTo(expectedName);
            Mockito.verify(locationJpaRepository, Mockito.times(0)).save(Mockito.any());
        }
    }
}
