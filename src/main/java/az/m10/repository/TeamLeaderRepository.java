package az.m10.repository;

import az.m10.domain.TeamLeader;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamLeaderRepository extends BaseJpaRepository<TeamLeader, Long>{
}
