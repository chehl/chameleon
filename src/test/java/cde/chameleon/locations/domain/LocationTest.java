package cde.chameleon.locations.domain;

import cde.chameleon.ChameleonDisplayNameGenerator;
import com.google.common.truth.Truth;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static cde.chameleon.locations.domain.LocationRandom.*;

@DisplayNameGeneration(ChameleonDisplayNameGenerator.class)
class LocationTest {

    @Test
    void givenLocationData_whenCreatingLocation_thenCreatesWithProvidedData() {
        // given
        String expectedName = randomName();
        String expectedAddress = randomAddress();
        BigDecimal expectedLatitude = randomLatitude();
        BigDecimal expectedLongitude = randomLongitude();

        // when
        Location location = new Location(expectedName, expectedAddress, expectedLatitude, expectedLongitude);

        // then
        Truth.assertThat(location.getId()).isNotNull();
        Truth.assertThat(location.getName()).isEqualTo(expectedName);
        Truth.assertThat(location.getAddress()).isEqualTo(expectedAddress);
        Truth.assertThat(location.getLatitude()).isEqualTo(expectedLatitude);
        Truth.assertThat(location.getLongitude()).isEqualTo(expectedLongitude);
    }

    @Test
    void givenLocationsOfBerlinAndTokyo_whenCalculatingDistance_thenReturnsCorrectDistance() {
        // given
        Location berlin = Locations.BERLIN;
        Location tokyo = Locations.TOKYO;

        // when
        double distance = berlin.distanceTo(tokyo);

        // then
        Truth.assertThat(distance).isWithin(1.0).of(Locations.DISTANCE_BERLIN_TOKYO);
    }

    @Test
    void givenLocationsOfMunichAndHamburg_whenCalculatingDistance_thenReturnsCorrectDistance() {
        // given
        Location munich = Locations.MUNICH;
        Location hamburg = Locations.HAMBURG;

        // when
        double distance = munich.distanceTo(hamburg);

        // then
        Truth.assertThat(distance).isWithin(1.0).of(Locations.DISTANCE_HAMBURG_MUNICH);
    }
}
