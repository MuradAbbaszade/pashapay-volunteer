package az.m10.repository;

import az.m10.domain.Location;
import az.m10.domain.Reservation;
import az.m10.domain.Volunteer;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ReservationRepository extends BaseJpaRepository<Reservation, Long> {
    List<Reservation> findAllByLocation(Location location);

    List<Reservation> findAllByVolunteer(Volunteer volunteer);

    @Query(value = "SELECT r FROM Reservation r WHERE r.status = 'WAITING_FOR_APPROVE' AND TIMESTAMPADD(MINUTE, 15, r.startTime) < :currentTime", nativeQuery = true)
    List<Reservation> findExpiredReservations(@Param("currentTime") LocalTime currentTime);

    @Modifying
    @Transactional
    @Query("UPDATE Reservation r SET r.status = 'DECLINED' WHERE r.status = 'WAITING_FOR_APPROVE' AND ADDTIME(r.startTime, '00:15:00') < :currentTime")
    void updateReservationStatus(@Param("currentTime") LocalTime currentTime);

    @Query(value = "SELECT * FROM reservations WHERE TIMESTAMPADD(MINUTE, -30, end_time) = :currentTime AND created_at = :currentDate AND status = 'APPROVED'", nativeQuery = true)
    List<Reservation> findReservationsWith30MinuteDifference(@Param("currentTime") LocalTime currentTime, @Param(("currentDate")) LocalDate currentDate);

    @Query(value = "SELECT * FROM reservations WHERE start_time = :currentTime AND status = 'WAITING_FOR_APPROVE'", nativeQuery = true)
    List<Reservation> findStartedReservations(@Param(("currentTime")) LocalTime currentTime);

    @Query(value = "SELECT * FROM reservations WHERE end_time = :currentTime AND created_at = :currentDate AND status = 'APPROVED'", nativeQuery = true)
    List<Reservation> findEndedReservations(@Param(("currentTime")) LocalTime currentTime, @Param(("currentDate")) LocalDate currentDate);
}
