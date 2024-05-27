package az.m10.dto;

import az.m10.domain.Reservation;
import lombok.*;

@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ReservationResponse {
    private Long reservationId;

    private String description;

    private String target;

    private String market;

    private String startTime;

    private String endTime;

    public ReservationResponse(Reservation reservation) {
        this.reservationId = reservation.getId();
        this.description = reservation.getLocation().getDesc();
        this.target = reservation.getLocation().getTarget();
        this.market = reservation.getLocation().getMarket();
        this.startTime = reservation.getStartTime().toString();
        this.endTime = reservation.getEndTime().toString();
    }
}
