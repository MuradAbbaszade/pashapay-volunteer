package az.m10.dto;

import az.m10.domain.Reservation;
import lombok.*;

@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ReservationTimeResponse {

    private String startTime;

    private String endTime;

    public ReservationTimeResponse(Reservation reservation) {
        this.startTime = reservation.getStartTime().toString();
        this.endTime = reservation.getEndTime().toString();
    }
}

