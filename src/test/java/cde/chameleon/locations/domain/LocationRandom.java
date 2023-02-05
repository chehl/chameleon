package cde.chameleon.locations.domain;

import cde.chameleon.ChameleonDisplayNameGenerator;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayNameGeneration;

import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;

@DisplayNameGeneration(ChameleonDisplayNameGenerator.class)
public class LocationRandom {
    private static final Random RANDOM = new Random();

    public static long randomOptLock() {
        return RANDOM.nextLong(0L, 1_000_000_000L);
    }

    public static String randomName() {
        return RandomStringUtils.randomPrint(1, 20);
    }

    public static String randomAddress() {
        return RandomStringUtils.randomPrint(1, 200);
    }

    public static BigDecimal randomLatitude() {
        return BigDecimal.valueOf(RANDOM.nextDouble(-90.0, 90.0));
    }

    public static BigDecimal randomLongitude() {
        return BigDecimal.valueOf(RANDOM.nextDouble(-180.0, 180.0));
    }

    public static Location randomLocation() {
        return randomLocation(UUID.randomUUID());
    }

    public static Location randomLocation(UUID id) {
        return new Location(id, randomName(), randomAddress(), randomLatitude(), randomLongitude());
    }
}
