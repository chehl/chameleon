package cde.chameleon.locations.domain;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class LocationDomainService {

    private final LocationRepository locationRepository;

    @Autowired
    public LocationDomainService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    /**
     * Retrieves all locations.
     * @return list of all locations
     */
    public List<Location> getLocations() {
        return locationRepository.findAll();
    }

    /**
     * Retrieves the location with the given id.
     * @param id a UUID
     * @return location with the provided id
     */
    public Location getLocationById(UUID id) {
        return locationRepository.findById(id);
    }

    /**
     * Retrieves the location with the given name.
     * @param name name of location
     * @return location with the provided name
     */
    public Location getLocationByName(String name) {
        return locationRepository.findByName(name);
    }

    /**
     * Creates a new location.
     * @param name name of new location
     * @param address address of new location
     * @param latitude latitude of new location
     * @param longitude longitude of new location
     * @return the new location with the provided attributes
     */
    public Location addLocation(String name, String address, BigDecimal latitude, BigDecimal longitude) {
        Location newLocation = new Location(name, address, latitude, longitude);
        locationRepository.save(newLocation);
        return newLocation;
    }

    /**
     * Deletes the location with the given id.
     * @param id a UUID
     */
    public void deleteLocationById(UUID id) {
        locationRepository.deleteById(id);
    }

    /**
     * Patches the location with the given id.
     * @param id a UUID
     * @param name new name (if not null)
     * @param address new address (if not null)
     * @param latitude new latitude (if not null)
     * @param longitude new longitude (if not null)
     * @return the updated location
     */
    public Location patchLocation(UUID id, String name, String address, BigDecimal latitude, BigDecimal longitude) {
        Location existingLocation = locationRepository.findById(id);

        Location updatedLocation = new Location(existingLocation.getId(),
            Optional.ofNullable(name).orElseGet(existingLocation::getName),
            Optional.ofNullable(address).orElseGet(existingLocation::getAddress),
            Optional.ofNullable(latitude).orElseGet(existingLocation::getLatitude),
            Optional.ofNullable(longitude).orElseGet(existingLocation::getLongitude));
        locationRepository.save(updatedLocation);

        return updatedLocation;
    }

    /**
     * Calculates the distance between two locations
     * @param id UUID of a location
     * @param otherId UUID of another location
     * @return distance (in km) between the locations with the provided ids
     */
    public double getDistance(UUID id, UUID otherId) {
        return locationRepository.findById(id).distanceTo(locationRepository.findById(otherId));
    }
}
