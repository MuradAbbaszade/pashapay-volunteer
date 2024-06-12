package az.m10.repository;

import az.m10.domain.Location;
import az.m10.domain.Reservation;
import az.m10.domain.Volunteer;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ReservationRepository extends BaseJpaRepository<Reservation, Long> {
    List<Reservation> findAllByLocation(Location location);

    List<Reservation> findAllByVolunteer(Volunteer volunteer);

    @Query("SELECT r FROM Reservation r WHERE r.endTime < CURRENT_TIME AND r.status = 'WAITING_FOR_APPROVE'")
    List<Reservation> findExpiredReservations();

    @Modifying
    @Transactional
    @Query("UPDATE Reservation r SET r.status = 'DECLINED' WHERE r.status = 'WAITING_FOR_APPROVE' AND ADDTIME(r.endTime, '00:15:00') < CURRENT_TIMESTAMP")
    void updateReservationStatus();
}
