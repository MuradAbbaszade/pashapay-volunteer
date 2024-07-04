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
            ReservationResponse reservationResponse = new ReservationResponse(reservation);
            setMinute15(reservationResponse, reservation);
            setMinute30(reservationResponse, reservation);
            return reservationResponse;
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
        reservation.setStartTime(LocalTime.parse(reservationTime));
        reservation.setEndTime(LocalTime.parse(reservationTime).plusHours(dto.getRange()));
        reservation.setStatus(ReservationStatus.APPROVED);
        if (isAvailable) {
            reservationRepository.save(reservation);
            ReservationResponse reservationResponse = new ReservationResponse(reservation);
            setMinute15(reservationResponse, reservation);
            setMinute30(reservationResponse, reservation);
            return reservationResponse;
        }
        return null;
    }

    public boolean approveReservation(Long reservationId, User user) {
        volunteerRepository.findByUser(user).orElseThrow(
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
        volunteerRepository.findByUser(user).orElseThrow(
                () -> new CustomNotFoundException("Volunteer not found")
        );
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(
                () -> new CustomNotFoundException("Reservation not found")
        );
        boolean isAvailable = locationService.checkLocationIsAvailable(
                new LocationRequestDTO(null, null, null, 1, reservation.getEndTime().toString()), reservation.getLocation().getId()
        );
        reservation.setEndTime(reservation.getEndTime().plusHours(1));
        if (isAvailable) {
            reservationRepository.save(reservation);
            ReservationResponse reservationResponse = new ReservationResponse(reservation);
            setMinute15(reservationResponse, reservation);
            setMinute30(reservationResponse, reservation);
            return reservationResponse;
        }
        return null;
    }

    public List<ReservationResponseDTO> findAllByUser(User user) {
        ZoneId azerbaijanZone = ZoneId.of("Asia/Baku");
        ZonedDateTime azerbaijanTime = ZonedDateTime.now(azerbaijanZone);
        LocalDate azerbaijanTimeLocalDate = azerbaijanTime.toLocalDate();

        Volunteer volunteer = volunteerRepository.findByUser(user).orElseThrow(
                () -> new CustomNotFoundException("Volunteer not found")
        );
        List<ReservationResponseDTO> reservations = new ArrayList<>();
        for (Reservation reservation : reservationRepository.findAllByVolunteer(volunteer)) {
            if (reservation.getCreatedAt().equals(azerbaijanTimeLocalDate) && reservation.getEndTime().isAfter(azerbaijanTime.toLocalTime())) {
                reservations.add(new ReservationResponseDTO(reservation));
            }
        }
        return reservations;
    }

    public List<ReservationAdminResponse> findAll() {
        List<ReservationAdminResponse> reservations = new ArrayList<>();
        for (Reservation reservation : reservationRepository.findAll()) {
            reservations.add(new ReservationAdminResponse(reservation));
        }
        return reservations;
    }

    public ReservationResponse findById(Long id) {
        Reservation reservation = reservationRepository.findById(id).orElseThrow(
                () -> new CustomNotFoundException("Reservation not found")
        );
        ReservationResponse reservationResponse = new ReservationResponse(reservation);
        setMinute15(reservationResponse, reservation);
        setMinute30(reservationResponse, reservation);
        return reservationResponse;
    }

    public boolean volunteerHasActiveReservation(User user, LocalTime reservationTime) {
        ZoneId azerbaijanZone = ZoneId.of("Asia/Baku");
        ZonedDateTime azerbaijanTime = ZonedDateTime.now(azerbaijanZone);
        LocalDate azerbaijanTimeLocalDate = azerbaijanTime.toLocalDate();

        Volunteer volunteer = volunteerRepository.findByUser(user).orElseThrow(
                () -> new CustomNotFoundException("Volunteer not found")
        );
        List<Reservation> reservations = reservationRepository.findAllByVolunteer(volunteer);
        for (Reservation reservation : reservations) {
            if (reservation.getCreatedAt().isEqual(azerbaijanTimeLocalDate) && reservation.getEndTime().isAfter(reservationTime)
                    && reservation.getStatus() != ReservationStatus.DECLINED) {
                return true;
            }
        }
        return false;
    }

    public List<ReservationResponse> initialFindAll(User user) {
        ZoneId azerbaijanZone = ZoneId.of("Asia/Baku");
        ZonedDateTime azerbaijanTime = ZonedDateTime.now(azerbaijanZone);
        LocalDate azerbaijanTimeLocalDate = azerbaijanTime.toLocalDate();
        Volunteer volunteer = volunteerRepository.findByUser(user).orElseThrow(
                () -> new CustomNotFoundException("Volunteer not found")
        );
        List<ReservationResponse> reservationResponses = new ArrayList<>();
        for (Reservation reservation : reservationRepository.findAllByVolunteer(volunteer)) {
            if (reservation.getCreatedAt().equals(azerbaijanTimeLocalDate) && reservation.getEndTime().isAfter(azerbaijanTime.toLocalTime())
                    && (reservation.getStatus().equals(ReservationStatus.WAITING_FOR_APPROVE) || reservation.getStatus().equals(ReservationStatus.APPROVED))) {
                ReservationResponse reservationResponse = new ReservationResponse();
                reservationResponse.setReservationId(reservation.getId());
                reservationResponse.setDescription(reservation.getLocation().getDesc());
                reservationResponse.setMarket(reservation.getLocation().getMarket());
                reservationResponse.setTarget(reservation.getLocation().getTarget());
                reservationResponse.setEndTime(reservation.getEndTime().toString());
                reservationResponse.setStartTime(reservation.getStartTime().toString());
                setMinute15(reservationResponse, reservation);
                setMinute30(reservationResponse, reservation);
                reservationResponses.add(reservationResponse);
            }
        }
        return reservationResponses;
    }

    public ReservationResponse setMinute15(ReservationResponse reservationResponse, Reservation reservation) {
        LocalTime startTime = reservation.getStartTime();
        ZoneId azerbaijanZone = ZoneId.of("Asia/Baku");
        ZonedDateTime azerbaijanTime = ZonedDateTime.now(azerbaijanZone);
        LocalTime now = azerbaijanTime.toLocalTime();
        if (reservation.getStatus() == ReservationStatus.WAITING_FOR_APPROVE &&
                now.isAfter(startTime) && now.isBefore(startTime.plusMinutes(15))) {
            reservationResponse.setMinute15(true);
        } else reservationResponse.setMinute15(false);
        return reservationResponse;
    }

    public ReservationResponse setMinute30(ReservationResponse reservationResponse, Reservation reservation) {
        ZoneId azerbaijanZone = ZoneId.of("Asia/Baku");
        ZonedDateTime azerbaijanTime = ZonedDateTime.now(azerbaijanZone);
        LocalTime now = azerbaijanTime.toLocalTime();
        LocalTime endTime = reservation.getEndTime();
        if (reservation.getStatus() == ReservationStatus.APPROVED &&
                now.isAfter(endTime.minusMinutes(30)) && now.isBefore(endTime)) {
            reservationResponse.setMinute30(true);
        } else reservationResponse.setMinute30(false);
        return reservationResponse;
    }
}
