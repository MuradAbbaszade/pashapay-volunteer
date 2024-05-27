package az.m10.dto;

import az.m10.domain.Reservation;
import lombok.*;

@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ReservationResponseDTO {
    private Long reservationId;

    private String description;

    private String target;

    private String market;

    public ReservationResponseDTO(Reservation reservation) {
        this.reservationId = reservation.getId();
        this.description = reservation.getLocation().getDesc();
        this.target = reservation.getLocation().getTarget();
        this.market = reservation.getLocation().getMarket();
    }
}
