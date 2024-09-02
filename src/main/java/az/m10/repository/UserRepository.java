package az.m10.repository;

import az.m10.domain.User;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface UserRepository extends BaseJpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
