package az.m10.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuickReservationRequest {
    @NotNull
    private Long locationId;

    @NotNull
    private Integer range;
}
