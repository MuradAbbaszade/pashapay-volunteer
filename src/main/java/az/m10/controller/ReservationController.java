package az.m10.controller;

import az.m10.auth.UserDetailsService;
import az.m10.domain.User;
import az.m10.dto.ReservationRequestDTO;
import az.m10.dto.ReservationResponse;
import az.m10.dto.ReservationResponseDTO;
import az.m10.repository.UserRepository;
import az.m10.service.ReservationService;
import az.m10.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;


@RestController
@RequestMapping("/api/v1/reservation")
@CrossOrigin(origins = {"http://localhost:3000", "*"})
public class ReservationController {
    private ReservationService reservationService;

    private final UserRepository userRepository;
    private UserDetailsService userDetailsService;
    private JwtUtil jwtUtil;

    public ReservationController(ReservationService reservationService, UserRepository userRepository, UserDetailsService userDetailsService, JwtUtil jwtUtil) {
        this.reservationService = reservationService;
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public List<ReservationResponseDTO> findAll(Principal principal) {
        User user = (User) userDetailsService.loadUserByUsername(principal.getName());
        return reservationService.findAll(user);
    }

    @GetMapping("/initial")
    public List<ReservationResponse> initialFindAll(Principal principal) {
        User user = (User) userDetailsService.loadUserByUsername(principal.getName());
        return reservationService.initialFindAll(user);
    }

    @GetMapping("{id}")
    public ReservationResponse findById(@PathVariable Long id) {
        return reservationService.findById(id);
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> add(@RequestBody ReservationRequestDTO reservationDTO, Principal principal) {
        User user = (User) userDetailsService.loadUserByUsername(principal.getName());
        ReservationResponse reservationResponse = reservationService.add(reservationDTO, user);
        if (reservationResponse == null) throw new IllegalArgumentException("Location is not available");
        return ResponseEntity.ok(reservationResponse);
    }

    @PostMapping("add-time")
    public ResponseEntity<ReservationResponse> addTimeToReservation(
            @RequestParam(required = true) Long reservationId,
            Principal principal) {
        User user = (User) userDetailsService.loadUserByUsername(principal.getName());
        ReservationResponse reservationResponse = reservationService.addTimeToReservation(reservationId, user);
        if (reservationResponse == null) throw new IllegalArgumentException("Location is not available");
        return ResponseEntity.ok(reservationResponse);
    }

    @PostMapping("reserve")
    public ResponseEntity<ReservationResponse> quickReserve(
            @RequestBody ReservationRequestDTO reservationDTO, Principal principal) {
        User user = (User) userDetailsService.loadUserByUsername(principal.getName());
        ReservationResponse reservationResponse = reservationService.quickReserve(reservationDTO, user);
        if (reservationResponse == null) throw new IllegalArgumentException("Location is not available");
        return ResponseEntity.ok(reservationResponse);
    }

    @PostMapping("approve-reservation")
    public ResponseEntity<Boolean> approveReservation(
            @RequestParam("reservation-id") Long reservationId,
            Principal principal) {
        User user = (User) userDetailsService.loadUserByUsername(principal.getName());
        boolean approved = reservationService.approveReservation(reservationId, user);
        return ResponseEntity.ok(approved);
    }
}
