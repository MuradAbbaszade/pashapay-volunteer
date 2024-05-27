package az.m10.dto;

import az.m10.domain.*;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Optional;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocationDTO extends BaseDTO<Location> {
    private Long id;

    @NotNull
    private Integer capacity;

    @NotEmpty
    @Size(max = 200)
    private String desc;

    @NotEmpty
    @Size(max = 200)
    private String target;

    @NotEmpty
    @Size(max = 200)
    private String district;

    @NotEmpty
    @Size(max = 200)
    private String market;

    @NotEmpty
    @Size(max = 200)
    private String subway;

    @Override
    public Location toEntity(Optional<Location> existingEntity) {
        Location entity = existingEntity.orElseGet(Location::new);
        entity.setDesc(this.getDesc());
        entity.setTarget(this.getTarget());
        entity.setCapacity(this.getCapacity());
        entity.setMarket(this.getMarket());
        entity.setDistrict(this.getDistrict());
        entity.setSubway(this.getSubway());
        return entity;
    }
}
