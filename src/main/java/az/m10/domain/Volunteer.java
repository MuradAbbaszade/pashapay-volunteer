package az.m10.domain;

import az.m10.dto.BaseDTO;
import az.m10.dto.TeamLeaderDTO;
import az.m10.dto.VolunteerDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@ToString
@Entity
@Table(name = "volunteers")
public class Volunteer extends BaseEntity<VolunteerDTO> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "surname", length = 100)
    private String surname;

    @Column(updatable = false)
    private LocalDate createdAt;

    @Column(name = "fin_code", length = 7)
    private String finCode;

    @Column(name = "phone_number", length = 50)
    private String phoneNumber;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfEmployment;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfResignation;

    @Column(name = "university", length = 100)
    private String university;

    @Column(name = "address", length = 100)
    private String address;

    private Boolean formStatus;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ToString.Exclude
    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @OneToMany(mappedBy = "volunteer", cascade = CascadeType.ALL, orphanRemoval = true,
            fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<Reservation> reservations;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "team_leader_id", nullable = false)
    private TeamLeader teamLeader;

    @PrePersist
    public void prePersist() {
        ZoneId azerbaijanZone = ZoneId.of("Asia/Baku");
        ZonedDateTime azerbaijanTime = ZonedDateTime.now(azerbaijanZone);
        this.createdAt = azerbaijanTime.toLocalDate();
    }

    @Override
    public VolunteerDTO toDto() {
        VolunteerDTO dto = new VolunteerDTO();
        dto.setId(this.getId());
        dto.setUsername(this.getUser().getUsername());
        dto.setName(this.name);
        dto.setSurname(this.surname);
        dto.setPhoneNumber(this.phoneNumber);
        dto.setFinCode(this.finCode);
        dto.setDateOfBirth(this.dateOfBirth);
        dto.setDateOfEmployment(this.dateOfEmployment);
        dto.setDateOfResignation(this.dateOfResignation);
        dto.setUniversity(this.university);
        dto.setAddress(this.address);
        dto.setFormStatus(this.formStatus);
        dto.setTeamLeaderId(this.teamLeader != null ? this.teamLeader.getId() : null);
        dto.setCreatedAt(this.createdAt);
        return dto;
    }
}
