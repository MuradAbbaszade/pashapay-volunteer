package az.m10.service;

import az.m10.domain.Location;
import az.m10.domain.Reservation;
import az.m10.domain.User;
import az.m10.domain.Volunteer;
import az.m10.domain.enums.ReservationStatus;
import az.m10.dto.*;
import az.m10.exception.CustomNotFoundException;
import az.m10.repository.LocationRepository;
import az.m10.repository.ReservationRepository;
import az.m10.repository.VolunteerRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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

    public ReservationResponse add(ReservationRequestDTO dto, User user) {
        ZoneId azerbaijanZone = ZoneId.of("Asia/Baku");
        ZonedDateTime azerbaijanTime = ZonedDateTime.now(azerbaijanZone);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String reservationTime = azerbaijanTime.plusHours(1).format(formatter);

        if (volunteerHasActiveReservation(user, LocalTime.parse(reservationTime)))
            throw new IllegalArgumentException("Volunteer has active reservation");

        Volunteer volunteer = volunteerRepository.findByUser(user).orElseThrow(
                () -> new CustomNotFoundException("Volunteer not found")
        );
        Location location = locationRepository.findById(dto.getLocationId()).orElseThrow(
                () -> new CustomNotFoundException("Location not found")
        );
        boolean isAvailable = locationService.checkLocationIsAvailable(
                new LocationRequestDTO(null, null, null, dto.getRange(), reservationTime.toString()), dto.getLocationId()
        );
        Reservation reservation = new Reservation();
        reservation.setVolunteer(volunteer);
        reservation.setLocation(location);
        reservation.setStartTime(LocalTime.parse(reservationTime));
        reservation.setEndTime(LocalTime.parse(reservationTime).plusHours(dto.getRange()));
        reservation.setStatus(ReservationStatus.WAITING_FOR_APPROVE);
        if (isAvailable) {
            reservationRepository.save(reservation);
            return new ReservationResponse(reservation);
        }
        return null;
    }

    public ReservationResponse quickReserve(ReservationRequestDTO dto, User user) {
        ZoneId azerbaijanZone = ZoneId.of("Asia/Baku");
        ZonedDateTime azerbaijanTime = ZonedDateTime.now(azerbaijanZone);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String reservationTime = azerbaijanTime.format(formatter);

        if (volunteerHasActiveReservation(user, LocalTime.parse(reservationTime)))
            throw new IllegalArgumentException("Volunteer has active reservation");

        Volunteer volunteer = volunteerRepository.findByUser(user).orElseThrow(
                () -> new CustomNotFoundException("Volunteer not found")
        );
        Location location = locationRepository.findById(dto.getLocationId()).orElseThrow(
                () -> new CustomNotFoundException("Location not found")
        );
        boolean isAvailable = locationService.checkLocationIsAvailable(
                new LocationRequestDTO(null, null, null, dto.getRange(), reservationTime), dto.getLocationId()
        );
        Reservation reservation = new Reservation();
        reservation.setVolunteer(volunteer);
        reservation.setLocation(location);
        reservation.setStartTime(LocalTime.now());
        reservation.setEndTime(LocalTime.now().plusHours(dto.getRange()));
        reservation.setStatus(ReservationStatus.APPROVED);
        if (isAvailable) {
            reservationRepository.save(reservation);
            return new ReservationResponse(reservation);
        }
        return null;
    }

    public boolean approveReservation(Long reservationId, User user) {
        Volunteer volunteer = volunteerRepository.findByUser(user).orElseThrow(
                () -> new CustomNotFoundException("Volunteer not found")
        );
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(
                () -> new CustomNotFoundException("Reservation not found")
        );
        if (reservation.getStatus().equals(ReservationStatus.WAITING_FOR_APPROVE)) {
            reservation.setStatus(ReservationStatus.APPROVED);
        }
        reservationRepository.save(reservation);
        return true;
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
        reservation.setStatus(ReservationStatus.APPROVED);
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

    public boolean volunteerHasActiveReservation(User user, LocalTime reservationTime) {
        Volunteer volunteer = volunteerRepository.findByUser(user).orElseThrow(
                () -> new CustomNotFoundException("Volunteer not found")
        );
        List<Reservation> reservations = reservationRepository.findAllByVolunteer(volunteer);
        for (Reservation reservation : reservations) {
            if (reservation.getCreatedAt().isEqual(LocalDate.now()) && reservation.getEndTime().isAfter(reservationTime)) {
                return true;
            }
        }
        return false;
    }

    public List<Reservation> findExpiredReservations() {
        return reservationRepository.findExpiredReservations();
    }
}
