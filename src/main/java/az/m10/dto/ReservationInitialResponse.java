package az.m10.dto;

import lombok.*;

@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ReservationInitialResponse {
    private Long reservationId;

    private String market;

    private String description;

    private String target;

    private String startTime;

    private String endTime;

    private Boolean minute15;

    private Boolean minute30;
}
