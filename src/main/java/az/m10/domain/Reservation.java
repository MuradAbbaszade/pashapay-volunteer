package az.m10.domain;

import az.m10.domain.enums.ReservationStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Getter
@Setter
@ToString
@Entity
@Table(name = "reservations")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "volunteer_id", nullable = false)
    private Volunteer volunteer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "created_at", updatable = false)
    private LocalDate createdAt;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @PrePersist
    public void prePersist() {
        ZoneId azerbaijanZone = ZoneId.of("Asia/Baku");
        ZonedDateTime azerbaijanTime = ZonedDateTime.now(azerbaijanZone);
        this.createdAt = azerbaijanTime.toLocalDate();
    }
}
