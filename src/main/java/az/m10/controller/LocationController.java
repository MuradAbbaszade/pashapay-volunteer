package az.m10.controller;

import az.m10.domain.Location;
import az.m10.dto.LocationDTO;
import az.m10.dto.LocationRequestDTO;
import az.m10.dto.LocationResponseDTO;
import az.m10.service.GenericService;
import az.m10.service.LocationService;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/location")
@CrossOrigin(origins = "http://localhost:3000, https://adminve3.vercel.app")
public class LocationController extends GenericController<Location, LocationDTO> {

    private LocationService locationService;

    public LocationController(GenericService<Location, LocationDTO> genericService, LocationService locationService) {
        super(genericService);
        this.locationService = locationService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<LocationResponseDTO>> searchLocations(
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String subway,
            @RequestParam(required = false) String market,
            @RequestParam Integer range,
            @RequestParam @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm") String reservationTime) {

        LocationRequestDTO locationRequestDTO = new LocationRequestDTO(market, subway, district, range, reservationTime);

        List<Location> locations = locationService.findBySubwayDistrictMarket(
                locationRequestDTO.getSubway(),
                locationRequestDTO.getMarket(),
                locationRequestDTO.getDistrict()
        );

        List<LocationResponseDTO> locationResponseDTOList = new ArrayList<>();
        for (Location location : locations) {
            LocationResponseDTO locationResponseDTO = new LocationResponseDTO(location);
            locationResponseDTO.setIsValid(locationService.checkLocationIsAvailable(locationRequestDTO, location.getId()));
            locationResponseDTOList.add(locationResponseDTO);
        }
        return ResponseEntity.ok(locationResponseDTOList);
    }
}

