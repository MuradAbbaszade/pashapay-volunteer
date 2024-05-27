package az.m10.repository;

import az.m10.domain.Authority;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface AuthorityRepository extends BaseJpaRepository<Authority, Long> {
    Optional<Authority> findByAuthority(String authority);
}
