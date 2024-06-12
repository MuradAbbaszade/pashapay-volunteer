package az.m10.scheduler;

import az.m10.domain.Reservation;
import az.m10.dto.NotificationRequest;
import az.m10.repository.ReservationRepository;
import az.m10.service.FCMService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
public class ReservationScheduler {

    private final ReservationRepository reservationRepository;
    private final FCMService fcmService;

    @Scheduled(fixedRate = 60000)
    public void sendNotifications() throws ExecutionException, InterruptedException {
        //Send notification to users whose reservation declined
        List<Reservation> reservations = reservationRepository.findExpiredReservations();
        for (Reservation reservation : reservations) {
            if (reservation.getVolunteer().getUser().getFcmToken() != null) {
                fcmService.sendMessageToToken(new NotificationRequest(
                        "Rezervasiya bildirişi",
                        "Rezervasiya ləğvi",
                        "Rezervasiya təsdiq olunmadığı üçün ləğv olundu",
                        reservation.getVolunteer().getUser().getFcmToken()
                ));
            }
        }
        reservationRepository.updateReservationStatus();

        //Send notification to users who can add time to their reservations
        List<Reservation> reservationList = reservationRepository.findReservationsWith30MinuteDifference();
        for (Reservation reservation : reservationList) {
            if (reservation.getVolunteer().getUser().getFcmToken() != null) {
                fcmService.sendMessageToToken(new NotificationRequest(
                        "Rezervasiya bildirişi",
                        "Rezervasiya sonlanır",
                        "Rezervasiya 30 dq sonra sonlanacaq",
                        reservation.getVolunteer().getUser().getFcmToken()
                ));
            }
        }
    }

}
