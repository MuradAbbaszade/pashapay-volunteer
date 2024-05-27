package az.m10.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.NotNull;

@Setter
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class LocationRequestDTO {
    private String market;
    private String subway;
    private String district;
    @NotNull
    private Integer range;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private String reservationTime;
}
