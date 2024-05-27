package az.m10.repository;

import az.m10.domain.User;
import az.m10.domain.Volunteer;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface VolunteerRepository extends BaseJpaRepository<Volunteer,Long> {
    Optional<Volunteer> findByUser(User user);
}
