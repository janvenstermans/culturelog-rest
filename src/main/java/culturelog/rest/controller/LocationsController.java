package culturelog.rest.controller;

import culturelog.rest.domain.Location;
import culturelog.rest.dto.LocationDto;
import culturelog.rest.exception.CultureLogException;
import culturelog.rest.security.CultureLogSecurityService;
import culturelog.rest.service.LocationService;
import culturelog.rest.utils.LocationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Jan Venstermans
 */
@RestController
@RequestMapping("/locations")
public class LocationsController {

    @Autowired
    private LocationService locationService;

    @Autowired
    private CultureLogSecurityService securityService;

    @RequestMapping(method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createLocation(@RequestBody(required = true) LocationDto locationDto) {
        Location location = LocationUtils.fromLocationDto(locationDto);
        if (location.getId() != null) {
            return ResponseEntity.badRequest().body("Cannot create location with id ");
        }
        location.setUserId(securityService.getLoggedInUserId());
        try {
            Location newLocation = locationService.save(location);
            return ResponseEntity.ok(LocationUtils.toLocationDto(newLocation));
        } catch (CultureLogException e) {
            return ResponseEntity.badRequest().body(e);
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getLocationsOfUser() {
        try {
            String userId = securityService.getLoggedInUserId();
            List<Location> locationList = locationService.getLocationsOfUserByUserId(userId);
            return ResponseEntity.ok(LocationUtils.toLocationDtoList(locationList));
//        } catch (CultureLogException e) {
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e);
        }
    }

    @RequestMapping(value = "/{locationId}", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getLocation(@PathVariable(value="locationId", required = true) String locationId) {
        String userId = securityService.getLoggedInUserId();
        Location location = locationService.getById(locationId);
        if (location != null && location.getUserId().equals(userId)) {
            return ResponseEntity.ok(LocationUtils.toLocationDto(location));
        }
        return ResponseEntity.badRequest().body("Cannot find location with id " + locationId);
    }

    @RequestMapping(value = "/{locationId}", method = RequestMethod.PUT)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateLocation(@PathVariable(value="locationId", required = true) String locationId,
                                              @RequestBody(required = true) LocationDto locationDto) {
        Location location = LocationUtils.fromLocationDto(locationDto);
        String userId = securityService.getLoggedInUserId();
        Location existingLocation = locationService.getById(locationId);
        if (existingLocation == null || !existingLocation.getUserId().equals(userId)) {
            return ResponseEntity.badRequest().body("Cannot update location with id " + locationId);
        }
        location.setId(locationId);
        location.setUserId(userId);
        try {
            Location updatedLocation = locationService.save(location);
            return ResponseEntity.ok(LocationUtils.toLocationDto(updatedLocation));
        } catch (CultureLogException e) {
            return ResponseEntity.badRequest().body(e);
        }
    }

    //TODO: remove location, with check if used in experience
}