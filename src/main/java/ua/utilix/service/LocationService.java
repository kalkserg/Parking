package ua.utilix.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.utilix.model.Device;
import ua.utilix.model.Location;
import ua.utilix.repo.DeviceRepository;
import ua.utilix.repo.LocationRepository;

import java.util.List;

@Service
public class LocationService {

    private final LocationRepository locationRepository;

    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Transactional(readOnly = true)
    public Location findById(long id) {
        return locationRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Location findBySigfoxId(String sigfoxId) {
//        System.out.println(locationRepository.findBySigfoxId(sigfoxId));
        return locationRepository.findBySigfoxId(sigfoxId);
    }

    @Transactional(readOnly = true)
    public List<Location> findAllLocations() {
        return locationRepository.findAll();
    }

    @Transactional
    public void addLocation(Location location) {
        locationRepository.save(location);
    }

    @Transactional
    public void updateLocation(Location location) {
        locationRepository.save(location);
    }

    @Transactional
    public void delLocation(Location location) {
        locationRepository.delete(location);
    }
}

