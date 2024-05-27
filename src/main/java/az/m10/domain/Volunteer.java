package az.m10.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString
@Entity
@Table(name = "volunteers")
public class Volunteer extends BaseEntity {
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

    private boolean formStatus;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ToString.Exclude
    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @OneToMany(mappedBy = "volunteer", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Reservation> reservations;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDate.now();
    }
}
