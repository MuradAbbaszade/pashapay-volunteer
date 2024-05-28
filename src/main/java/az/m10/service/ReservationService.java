package az.m10.service;

import az.m10.domain.Location;
import az.m10.domain.Reservation;
import az.m10.domain.User;
import az.m10.domain.Volunteer;
import az.m10.dto.LocationRequestDTO;
import az.m10.dto.ReservationDTO;
import az.m10.dto.ReservationResponse;
import az.m10.dto.ReservationResponseDTO;
import az.m10.exception.CustomNotFoundException;
import az.m10.repository.LocationRepository;
import az.m10.repository.ReservationRepository;
import az.m10.repository.VolunteerRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReservationService {
    private final VolunteerRepository volunteerRepository;

    private final ReservationRepository reservationRepository;

    private final LocationRepository locationRepository;
    private final LocationService locationService;

    public ReservationService(VolunteerRepository volunteerRepository, ReservationRepository reservationRepository, LocationRepository locationRepository, LocationService locationService) {
        this.volunteerRepository = volunteerRepository;
        this.reservationRepository = reservationRepository;
        this.locationRepository = locationRepository;
        this.locationService = locationService;
    }

    public ReservationResponse add(ReservationDTO dto, User user) {
        Volunteer volunteer = volunteerRepository.findByUser(user).orElseThrow(
                () -> new CustomNotFoundException("Volunteer not found")
        );
        Location location = locationRepository.findById(dto.getLocationId()).orElseThrow(
                () -> new CustomNotFoundException("Location not found")
        );
        boolean isAvailable = locationService.checkLocationIsAvailable(
                new LocationRequestDTO(null, null, null, dto.getRange(), dto.getStartTime()), dto.getLocationId()
        );
        Reservation reservation = new Reservation();
        reservation.setVolunteer(volunteer);
        reservation.setLocation(location);
        reservation.setStartTime(LocalTime.parse(dto.getStartTime()));
        reservation.setEndTime(LocalTime.parse(dto.getStartTime()).plusHours(dto.getRange()));
        reservation.setStatus(true);
        if (isAvailable) {
            reservationRepository.save(reservation);
            return new ReservationResponse(reservation);
        }
        return null;
    }

    public ReservationResponse addTimeToReservation(Long reservationId, User user) {
        Volunteer volunteer = volunteerRepository.findByUser(user).orElseThrow(
                () -> new CustomNotFoundException("Volunteer not found")
        );
        Reservation oldReservation = reservationRepository.findById(reservationId).orElseThrow(
                () -> new CustomNotFoundException("Reservation not found")
        );
        boolean isAvailable = locationService.checkLocationIsAvailable(
                new LocationRequestDTO(null, null, null, 1, oldReservation.getEndTime().toString()), oldReservation.getLocation().getId()
        );
        Reservation reservation = new Reservation();
        reservation.setVolunteer(volunteer);
        reservation.setLocation(oldReservation.getLocation());
        reservation.setStartTime(oldReservation.getEndTime());
        reservation.setEndTime(oldReservation.getEndTime().plusHours(1));
        reservation.setStatus(true);
        if (isAvailable) {
            reservationRepository.save(reservation);
            return new ReservationResponse(reservation);
        }
        return null;
    }

    public List<ReservationResponseDTO> findAll(User user) {
        Volunteer volunteer = volunteerRepository.findByUser(user).orElseThrow(
                () -> new CustomNotFoundException("Volunteer not found")
        );
        List<ReservationResponseDTO> reservations = new ArrayList<>();
        for (Reservation reservation : reservationRepository.findAllByVolunteer(volunteer)) {
            if (reservation.getCreatedAt().equals(LocalDate.now()) && reservation.getEndTime().isAfter(LocalTime.now())) {
                reservations.add(new ReservationResponseDTO(reservation));
            }
        }
        return reservations;
    }

    public Reservation findById(Long id) {
        return reservationRepository.findById(id).orElseThrow(
                () -> new CustomNotFoundException("Reservation not found")
        );
    }

    public List<Reservation> findExpiredReservations(){
        return reservationRepository.findExpiredReservations();
    }
}
