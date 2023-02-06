package cde.chameleon.locations.domain;

import cde.chameleon.locations.domain.exception.NameNotUniqueLocationException;
import cde.chameleon.locations.domain.exception.NotFoundLocationException;

import java.util.List;
import java.util.UUID;

/**
 * The interface LocationRepository describes a repository for locations.
 */
public interface LocationRepository {

    /**
     * Finds all locations.
     * @return list of locations
     */
    List<Location> findAll();

    /**
     * Finds a location by a given id.
     * @param id id of a location
     * @return location with the provided id
     * @throws NotFoundLocationException if a location with the provided id has not been found
     */
    Location findById(UUID id) throws NotFoundLocationException;

    /**
     * Finds a location by a given name.
     * @param name name of location
     * @return location with the provided name
     * @throws NotFoundLocationException if a location with the provided name has not been found
     */
    Location findByName(String name) throws NotFoundLocationException;

    /**
     * Deletes a location by a given id.
     * @param id id of a location
     * @throws NotFoundLocationException if a location with the provided id has not been found
     */
    void deleteById(UUID id) throws NotFoundLocationException;

    /**
     * Saves the given location.
     * @param location a location
     * @throws NameNotUniqueLocationException if another location with the same name
     * as the name of the provided location already exists
     */
    void save(Location location) throws NameNotUniqueLocationException;
}
