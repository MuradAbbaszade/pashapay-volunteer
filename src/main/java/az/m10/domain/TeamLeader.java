package az.m10.domain;

import az.m10.dto.TeamLeaderDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@ToString
@Entity
@Table(name = "team_leaders")
public class TeamLeader extends BaseEntity<TeamLeaderDTO> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "surname", length = 100)
    private String surname;

    @Column(updatable = false)
    private LocalDate createdAt;

    @Column(name = "phone_number", length = 50)
    private String phoneNumber;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ToString.Exclude
    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @OneToMany(mappedBy = "teamLeader", fetch = FetchType.EAGER)
    @JsonIgnore
    @ToString.Exclude
    private List<Volunteer> volunteers;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDate.now();
    }

    @Override
    public TeamLeaderDTO toDto(){
        TeamLeaderDTO dto = new TeamLeaderDTO();
        dto.setId(this.id);
        dto.setName(this.name);
        dto.setSurname(this.surname);
        dto.setPhoneNumber(this.phoneNumber);
        dto.setUsername(this.user.getUsername());
        return dto;
    }
}
