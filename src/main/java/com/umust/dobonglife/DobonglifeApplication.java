package com.umust.dobonglife;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
public class DobonglifeApplication {

	public static void main(String[] args) {
		SpringApplication.run(DobonglifeApplication.class, args);
	}

}
