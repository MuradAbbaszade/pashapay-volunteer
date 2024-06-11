package az.m10.dto;

import az.m10.domain.TeamLeader;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Optional;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeamLeaderDTO extends BaseDTO<TeamLeader> {
    private Long id;

    @NotEmpty
    @Size(max = 100)
    private String username;

    @Size(max = 100)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NotEmpty
    @Size(max = 100)
    private String name;

    @NotEmpty
    @Size(max = 100)
    private String surname;

    @NotEmpty
    @Size(max = 50)
    private String phoneNumber;

    public TeamLeader toEntity(Optional<TeamLeader> existingEntity) {
        TeamLeader entity = existingEntity.orElseGet(TeamLeader::new);
        entity.setName(this.getName());
        entity.setSurname(this.getSurname());
        entity.setPhoneNumber(this.getPhoneNumber());
        return entity;
    }
}
