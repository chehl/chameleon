package cde.chameleon.locations.entity;

import cde.chameleon.ChameleonDisplayNameGenerator;
import com.google.common.truth.Truth;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static cde.chameleon.locations.domain.LocationRandom.*;

@DisplayNameGeneration(ChameleonDisplayNameGenerator.class)
class LocationEntityTest {

    @Test
    void givenEntityWithLatitudeWithTrailingZeros_whenGettingLatitude_thenReturnsLatitudeWithoutTrailingZeros() {
        // given
        BigDecimal trailingZeros = new BigDecimal("43.2100");
        BigDecimal noTrailingZeros = new BigDecimal("43.21");
        LocationEntity locationEntity = new LocationEntity(
                UUID.randomUUID(), 0L, randomName(), randomAddress(), randomLatitude(), randomLongitude());
        locationEntity.setLatitude(trailingZeros);

        // when
        BigDecimal actualLatitude = locationEntity.getLatitude();

        // then
        Truth.assertThat(actualLatitude.toString()).isEqualTo(noTrailingZeros.toString());
    }

    @Test
    void givenEntityWithLongitudeWithTrailingZeros_whenGettingLongitude_thenReturnsLongitudeWithoutTrailingZeros() {
        // given
        BigDecimal trailingZeros = new BigDecimal("43.2100");
        BigDecimal noTrailingZeros = new BigDecimal("43.21");
        LocationEntity locationEntity = new LocationEntity(
                UUID.randomUUID(), randomOptLock(), randomName(), randomAddress(), randomLongitude(), randomLongitude());
        locationEntity.setLongitude(trailingZeros);

        // when
        BigDecimal actualLongitude = locationEntity.getLongitude();

        // then
        Truth.assertThat(actualLongitude.toString()).isEqualTo(noTrailingZeros.toString());
    }

    @Test
    void givenTwoEntitiesWithSameId_whenHashing_thenReturnsSameHashCode() {
        // given
        LocationEntity locationEntity1 = new LocationEntity(
                UUID.randomUUID(), randomOptLock(), randomName(), randomAddress(), randomLongitude(), randomLongitude());
        LocationEntity locationEntity2 = new LocationEntity(
                locationEntity1.getId(), randomOptLock(), randomName(), randomAddress(), randomLongitude(), randomLongitude());

        // when
        int hashCode1 = locationEntity1.hashCode();
        int hashCode2 = locationEntity2.hashCode();

        // then
        Truth.assertThat(hashCode1).isEqualTo(hashCode2);
    }
}
