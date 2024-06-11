package az.m10.domain;

import az.m10.dto.LocationDTO;
import az.m10.dto.VolunteerDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@ToString
@Entity
@Table(name = "locations")
public class Location extends BaseEntity<LocationDTO> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description", length = 200)
    private String desc;

    @Column(name = "target", length = 200)
    private String target;

    @Column(name = "district", length = 200)
    private String district;

    @Column(name = "market", length = 200)
    private String market;

    @Column(name = "subway", length = 200)
    private String subway;

    private Integer capacity;

    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Reservation> reservations;

    @Override
    public LocationDTO toDto() {
        LocationDTO dto = new LocationDTO();
        dto.setId(this.id);
        dto.setCapacity(this.capacity);
        dto.setDesc(this.desc);
        dto.setTarget(this.target);
        dto.setDistrict(this.district);
        dto.setMarket(this.market);
        dto.setSubway(this.subway);
        return dto;
    }
}
