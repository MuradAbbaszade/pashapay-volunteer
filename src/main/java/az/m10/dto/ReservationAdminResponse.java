package az.m10.dto;

import az.m10.domain.Reservation;
import lombok.*;

@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ReservationAdminResponse {
    private Long reservationId;

    private Long locationId;

    private String startTime;

    private String endTime;

    private Long volunteerId;

    public ReservationAdminResponse(Reservation reservation) {
        this.reservationId = reservation.getId();
        this.locationId = reservation.getLocation().getId();
        this.startTime = reservation.getStartTime().toString();
        this.endTime = reservation.getEndTime().toString();
        this.volunteerId = reservation.getVolunteer().getId();
    }
}
