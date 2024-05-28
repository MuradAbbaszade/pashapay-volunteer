package az.m10.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationStatusMessage {
    private Long reservationId;

    private String expired;
}
