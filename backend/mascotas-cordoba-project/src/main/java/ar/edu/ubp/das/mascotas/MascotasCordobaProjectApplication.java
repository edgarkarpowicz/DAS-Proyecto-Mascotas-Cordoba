package ar.edu.ubp.das.mascotas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MascotasCordobaProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(MascotasCordobaProjectApplication.class, args);
    }

}
