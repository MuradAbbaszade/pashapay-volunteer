package az.m10.controller;

import az.m10.domain.Location;
import az.m10.domain.Reservation;
import az.m10.domain.enums.ReservationStatus;
import az.m10.dto.LocationDTO;
import az.m10.dto.LocationRequestDTO;
import az.m10.dto.LocationResponseDTO;
import az.m10.dto.ReservationTimeResponse;
import az.m10.service.GenericService;
import az.m10.service.LocationService;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/location")
@CrossOrigin(origins = "*")
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
            @RequestParam Integer range) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime currentTime = LocalTime.now();
        String reservationTime = currentTime.format(formatter);d
        LocationRequestDTO locationRequestDTO = new LocationRequestDTO(market, subway, district, range, reservationTime);

        List<Location> locations = locationService.findBySubwayDistrictMarket(
                locationRequestDTO.getSubway(),
                locationRequestDTO.getMarket(),
                locationRequestDTO.getDistrict()
        );

        List<LocationResponseDTO> locationResponseDTOList = new ArrayList<>();
        for (Location location : locations) {
            List<ReservationTimeResponse> reservationTimeResponses = new ArrayList<>();
            for (Reservation reservation : location.getReservations()) {
                if ((reservation.getStatus().equals(ReservationStatus.WAITING_FOR_APPROVE)
                        || reservation.getStatus().equals(ReservationStatus.APPROVED)) &&
                        reservation.getCreatedAt().equals(LocalDate.now()) &&
                        (reservation.getEndTime().isAfter(LocalTime.parse(reservationTime))
                                && reservation.getStartTime().isBefore(LocalTime.parse(reservationTime).plusHours(range)))) {
                    ReservationTimeResponse reservationTimeResponse = new ReservationTimeResponse();
                    reservationTimeResponse.setStartTime(reservation.getStartTime().toString());
                    reservationTimeResponse.setEndTime(reservation.getEndTime().toString());
                    reservationTimeResponses.add(reservationTimeResponse);
                }
            }
            LocationResponseDTO locationResponseDTO = new LocationResponseDTO(location);
            locationResponseDTO.setReservationTimeResponses(reservationTimeResponses);
            locationResponseDTO.setIsValid(locationService.checkLocationIsAvailable(locationRequestDTO, location.getId()));
            locationResponseDTOList.add(locationResponseDTO);
        }
        return ResponseEntity.ok(locationResponseDTOList);
    }
}

