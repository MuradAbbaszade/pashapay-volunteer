package az.m10.dto;

import az.m10.domain.Location;
import lombok.*;

@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class LocationResponseDTO {
    private Long id;

    private String description;

    private String target;

    private Boolean isValid;

    private String market;

    public LocationResponseDTO(Location location){
        this.id = location.getId();
        this.description = location.getDesc();
        this.target = location.getTarget();
        this.market = location.getMarket();
    }
}
