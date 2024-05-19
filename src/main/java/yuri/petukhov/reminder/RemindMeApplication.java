package yuri.petukhov.reminder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RemindMeApplication {

	public static void main(String[] args) {
		SpringApplication.run(RemindMeApplication.class, args);
	}

}
