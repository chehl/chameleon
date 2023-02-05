package cde.chameleon.locations.domain;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * The class Location represents a place of residence of a company or person
 * including name, address and GPS coordinates of the location.
 */
@Value
@AllArgsConstructor
public class Location {

    UUID id;
    String name;
    String address;
    BigDecimal latitude;
    BigDecimal longitude;

    /**
     * Creates a new location.
     * @param name name of the location
     * @param address address of the location
     * @param latitude latitude of the location (that must be between -90 and 90)
     * @param longitude longitude of the location (that must be between -180 and 180)
     */
    public Location(String name, String address, BigDecimal latitude, BigDecimal longitude) {
        this(UUID.randomUUID(), name, address, latitude, longitude);
    }

    /**
     * Calculates the distance in kilometers between this location and another given location.
     * The calculation utilizes the method found by Thaddeus Vincenty. It assumes the earth to be the WGS84 ellipsoid.
     * @param location other location
     * @return the distance in kilometers between this location and the provided location
     */
    public double distanceTo(Location location) {
        return distance(
                latitude.doubleValue(), longitude.doubleValue(),
                location.latitude.doubleValue(), location.longitude.doubleValue());
    }

    private static double distance(double latitude1, double longitude1, double latitude2, double longitude2) {
        double earthFlattening = 1.0 / 298.257_233_563; // WGS84 ellipsoid
        double earthEquatorRadius = 6378.137; // [km]

        double toRad = Math.PI / 180.0;

        double f = (latitude1 + latitude2) / 2.0 * toRad;
        double g = (latitude1 - latitude2) / 2.0 * toRad;
        double l = (longitude1 - longitude2) / 2.0 * toRad;

        double sinF = Math.sin(f);
        double cosF = Math.cos(f);
        double sinG = Math.sin(g);
        double cosG = Math.cos(g);
        double sinL = Math.sin(l);
        double cosL = Math.cos(l);

        double sinF2 = sinF * sinF;
        double cosF2 = cosF * cosF;
        double sinG2 = sinG * sinG;
        double cosG2 = cosG * cosG;
        double sinL2 = sinL * sinL;
        double cosL2 = cosL * cosL;

        double s = sinG2 * cosL2 + cosF2 * sinL2;
        double c = cosG2 * cosL2 + sinF2 * sinL2;
        double w = Math.atan(Math.sqrt(s / c));
        double d = 2 * w * earthEquatorRadius; // approximate distance

        double t = Math.sqrt(s * c) / w;
        double h1 = (3 * t - 1) / (2.0 * c);
        double h2 = (3 * t + 1) / (2.0 * s);

        return d * (1 + (earthFlattening * h1 * sinF2 * cosG2) - (earthFlattening * h2 * cosF2 * sinG2)); // distance with correction
    }
}
