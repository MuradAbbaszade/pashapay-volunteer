package az.m10.repository;

import az.m10.domain.Location;
import az.m10.domain.Reservation;
import az.m10.domain.Volunteer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends BaseJpaRepository<Reservation, Long> {
    List<Reservation> findAllByLocation(Location location);

    List<Reservation> findAllByVolunteer(Volunteer volunteer);

    @Query("SELECT r FROM Reservation r WHERE r.endTime < CURRENT_TIME AND r.status = true")
    List<Reservation> findExpiredReservations();
}
