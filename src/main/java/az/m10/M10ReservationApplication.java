package az.m10;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootApplication
@RequiredArgsConstructor
@EnableScheduling
public class M10ReservationApplication implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(M10ReservationApplication.class, args);
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        jdbcTemplate.update("INSERT INTO authorities(authority) VALUES('ADMIN')");
        String encodedPassword = passwordEncoder.encode("YDw04#0UE8<c");
        jdbcTemplate.update("INSERT INTO users(username, password, is_enabled) VALUES('admin',?,1)",encodedPassword);
        jdbcTemplate.update("INSERT INTO user_authorities(user_id, authority_id) VALUES(1,1)");
    }

}

