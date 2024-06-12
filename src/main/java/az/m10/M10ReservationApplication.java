package az.m10;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@RequiredArgsConstructor
@EnableScheduling
public class M10ReservationApplication {

    public static void main(String[] args) {
        SpringApplication.run(M10ReservationApplication.class, args);
    }

}
