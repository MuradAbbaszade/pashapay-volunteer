package az.m10.dto;

import az.m10.domain.Location;
import az.m10.domain.User;
import az.m10.domain.Volunteer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Optional;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VolunteerDTO extends BaseDTO<Volunteer> {
    private Long id;

    @NotEmpty
    @Size(max = 100)
    private String username;

    @Size(max = 100)
    private String password;

    @NotEmpty
    @Size(max = 100)
    private String name;

    @NotEmpty
    @Size(max = 100)
    private String surname;

    @JsonIgnore
    private LocalDate createdAt;

    @NotEmpty
    @Size(max = 7)
    private String finCode;

    @NotEmpty
    @Size(max = 50)
    private String phoneNumber;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateOfEmployment;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateOfResignation;

    @Size(max = 100)
    private String university;

    @NotEmpty
    @Size(max = 100)
    private String address;

    @NotNull
    private Boolean formStatus;

    @NotNull
    private Long teamLeaderId;

    @JsonIgnore
    private Long userId;

    @JsonIgnore
    private User user;

    public Volunteer toEntity(Optional<Volunteer> existingEntity) {
        Volunteer entity = existingEntity.orElseGet(Volunteer::new);
        entity.setName(this.getName());
        entity.setSurname(this.getSurname());
        entity.setPhoneNumber(this.getPhoneNumber());
        entity.setAddress(this.getAddress());
        entity.setFinCode(this.getFinCode());
        entity.setFormStatus(this.getFormStatus());
        entity.setUniversity(this.getUniversity());
        entity.setDateOfBirth(this.getDateOfBirth());
        entity.setDateOfEmployment(this.getDateOfEmployment());
        entity.setDateOfResignation(this.getDateOfResignation());
        return entity;
    }
}
