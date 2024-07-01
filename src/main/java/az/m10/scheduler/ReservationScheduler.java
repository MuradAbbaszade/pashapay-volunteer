package az.m10.scheduler;

import az.m10.domain.Reservation;
import az.m10.dto.NotificationRequest;
import az.m10.repository.ReservationRepository;
import az.m10.service.FCMService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
public class ReservationScheduler {

    private final ReservationRepository reservationRepository;
    private final FCMService fcmService;

    @Scheduled(fixedRate = 60000)
    public void sendNotifications() throws ExecutionException, InterruptedException {
        ZoneId azerbaijanZone = ZoneId.of("Asia/Baku");
        ZonedDateTime azerbaijanTime = ZonedDateTime.now(azerbaijanZone);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String now = azerbaijanTime.format(formatter);
        //Send notification to users whose reservation started
        List<Reservation> startedReservations = reservationRepository.findStartedReservations(LocalTime.parse(now));
        for (Reservation reservation : startedReservations) {
            if (reservation.getVolunteer().getUser().getFcmToken() != null) {
                fcmService.sendMessageToToken(new NotificationRequest(
                        "Rezervasiya başlayır",
                        "Rezervasiyanın başlaması üçün 15 dəqiqə ərzində təsdiq edin",
                        "Rezervasiyanın başlaması üçün 15 dəqiqə ərzində təsdiq edin",
                        reservation.getVolunteer().getUser().getFcmToken()
                ));
            }
        }

        //Send notification to users whose reservation ended
        List<Reservation> endedReservations = reservationRepository.findEndedReservations(LocalTime.parse(now), azerbaijanTime.toLocalDate());
        for (Reservation reservation : endedReservations) {
            if (reservation.getVolunteer().getUser().getFcmToken() != null) {
                fcmService.sendMessageToToken(new NotificationRequest(
                        "Rezervasiya sonlandı",
                        "Rezervasiyanın vaxtı uzadılmadığı üçün sonlandı",
                        "Rezervasiyanın vaxtı uzadılmadığı üçün sonlandı",
                        reservation.getVolunteer().getUser().getFcmToken()
                ));
            }
        }

        //Send notification to users whose reservation declined
        List<Reservation> expiredReservations = reservationRepository.findExpiredReservations(LocalTime.parse(now));
        for (Reservation reservation : expiredReservations) {
            if (reservation.getVolunteer().getUser().getFcmToken() != null) {
                fcmService.sendMessageToToken(new NotificationRequest(
                        "Rezervasiya ləğvi",
                        "Rezervasiya təsdiq olunmadığı üçün ləğv olundu",
                        "Rezervasiya təsdiq olunmadığı üçün ləğv olundu",
                        reservation.getVolunteer().getUser().getFcmToken()
                ));
            }
        }
        reservationRepository.updateReservationStatus(azerbaijanTime.toLocalTime());

        //Send notification to users who can add time to their reservations
        List<Reservation> last30minReservations = reservationRepository.findReservationsWith30MinuteDifference(LocalTime.parse(now), azerbaijanTime.toLocalDate());
        for (Reservation reservation : last30minReservations) {
            if (reservation.getVolunteer().getUser().getFcmToken() != null) {
                fcmService.sendMessageToToken(new NotificationRequest(
                        "Rezervasiya sonlanır",
                        "Rezervasiya 30 dq sonra sonlanacaq",
                        "Rezervasiya 30 dq sonra sonlanacaq",
                        reservation.getVolunteer().getUser().getFcmToken()
                ));
            }
        }
    }

}
