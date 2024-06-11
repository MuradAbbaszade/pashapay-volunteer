package az.m10.service;

import az.m10.domain.Location;
import az.m10.domain.Reservation;
import az.m10.dto.LocationDTO;
import az.m10.dto.LocationRequestDTO;
import az.m10.exception.CustomNotFoundException;
import az.m10.repository.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Optional;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService extends GenericService<Location, LocationDTO> {
    private final BaseJpaRepository<Location, Long> repository;
    private final LocationRepository locationRepository;
    private final ReservationRepository reservationRepository;

    public LocationService(BaseJpaRepository<Location,
            Long> repository,
                           LocationRepository locationRepository, ReservationRepository reservationRepository) {
        super(repository);
        this.repository = repository;
        this.locationRepository = locationRepository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    public LocationDTO add(LocationDTO dto) {
        Location location = dto.toEntity(Optional.of(new Location()));
        return locationRepository.save(location).toDto();
    }

    public List<Location> findBySubwayDistrictMarket(String subway, String market, String district) {
        return locationRepository.findBySubwayDistrictMarket(subway, district, market);
    }

    public boolean checkLocationIsAvailable(LocationRequestDTO locationRequestDTO, Long locationId) {
        Location location = locationRepository.findById(locationId).orElseThrow(
                () -> new CustomNotFoundException("Location not found")
        );
        List<Reservation> reservations = new ArrayList<>();
        LocalTime currentReservationStartTime = LocalTime.parse(locationRequestDTO.getReservationTime());
        LocalTime currentReservationEndTime = LocalTime.parse(locationRequestDTO.getReservationTime()).
                plusHours(locationRequestDTO.getRange());
        for (Reservation reservation : reservationRepository.findAllByLocation(location)) {
            if (reservation.getCreatedAt().equals(LocalDate.now())) {
                LocalTime reservationStartTime = reservation.getStartTime();
                LocalTime reservationEndTime = reservation.getEndTime();
                if (reservationEndTime.isAfter(currentReservationStartTime) && reservationStartTime.isBefore(currentReservationEndTime)) {
                    reservations.add(reservation);
                }
            }
        }
        return location.getCapacity() > reservations.size();
    }
}
