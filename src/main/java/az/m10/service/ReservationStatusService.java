package az.m10.service;

import az.m10.domain.Reservation;
import az.m10.dto.ReservationStatusMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationStatusService {
    private final ReservationService reservationService;
    private final SimpMessagingTemplate messagingTemplate;

    @Scheduled(fixedRate = 60000)
    public void checkExpiredReservations() {
        List<Reservation> expiredReservations = reservationService.findExpiredReservations();
        if (!expiredReservations.isEmpty()) {
            for (Reservation reservation : expiredReservations) {
                messagingTemplate.convertAndSend("/topic/status",
                        new ReservationStatusMessage(reservation.getId(), "expired"));
            }
        }
    }
}
