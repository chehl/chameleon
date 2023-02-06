package cde.chameleon.locations.entity;

import cde.chameleon.locations.domain.Location;
import cde.chameleon.locations.domain.LocationRepository;
import cde.chameleon.locations.domain.exception.NameNotUniqueLocationException;
import cde.chameleon.locations.domain.exception.NotFoundLocationException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public class DatabaseLocationRepository implements LocationRepository {

    private final ModelMapper modelMapper;
    private final LocationJpaRepository repository;

    @Autowired
    public DatabaseLocationRepository(ModelMapper modelMapper, LocationJpaRepository repository) {
        this.modelMapper = modelMapper;
        this.repository = repository;
    }

    @Transactional
    @Override
    public List<Location> findAll() {
        return repository.findAll().stream().map(
                locationEntity -> new Location(
                        locationEntity.getId(), locationEntity.getName(), locationEntity.getAddress(),
                        locationEntity.getLatitude(), locationEntity.getLongitude()))
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public Location findById(UUID id) {
        LocationEntity locationEntity = repository.findById(id)
                        .orElseThrow(() -> new NotFoundLocationException(id));
        return new Location(locationEntity.getId(), locationEntity.getName(), locationEntity.getAddress(),
                locationEntity.getLatitude(), locationEntity.getLongitude());
    }

    @Transactional(readOnly = true)
    @Override
    public Location findByName(String name) throws NotFoundLocationException {
        LocationEntity locationEntity = repository.findLocationEntityByName(name)
                .orElseThrow(() -> new NotFoundLocationException(name));
        return new Location(locationEntity.getId(), locationEntity.getName(), locationEntity.getAddress(),
                locationEntity.getLatitude(), locationEntity.getLongitude());
    }

    @Transactional
    @Override
    public void deleteById(UUID id) {
        try {
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundLocationException(id);
        }
    }

    @Transactional
    @Override
    public void save(Location location) {
        repository.findLocationEntityByName(location.getName()).ifPresent(
                locationEntity -> {
                    if (!locationEntity.getId().equals(location.getId())) {
                        throw new NameNotUniqueLocationException(location.getId(), location.getName());
                    }
                }
        );
        repository.findById(location.getId()).ifPresentOrElse(
                locationEntity -> {
                    LocationEntity locationEntityToSave = modelMapper.map(location, LocationEntity.class);
                    locationEntityToSave.setOptLock(locationEntity.getOptLock());
                    repository.save(locationEntityToSave);
                },
                () -> repository.save(modelMapper.map(location, LocationEntity.class))
        );
    }
}