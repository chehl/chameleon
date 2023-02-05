package cde.chameleon.locations.domain;

import java.math.BigDecimal;

public class Locations {

    private Locations() {
    }

    public static final Location TOKYO = new Location(
            "Tokyo", "Tokyo Address", BigDecimal.valueOf(35.70), BigDecimal.valueOf(139.77));

    public static final Location BERLIN = new Location(
            "Berlin", "Berlin Address", BigDecimal.valueOf(52.52), BigDecimal.valueOf(13.41));

    public static final Location HAMBURG = new Location(
                "Hamburg", "Hamburg Address", BigDecimal.valueOf(53.55), BigDecimal.valueOf(9.99));

    public static final Location MUNICH = new Location(
                "Munich", "Munich Address", BigDecimal.valueOf(48.14), BigDecimal.valueOf(11.58));


    public static final double DISTANCE_BERLIN_TOKYO = 8940;
    public static final double DISTANCE_HAMBURG_MUNICH = 612;
}
