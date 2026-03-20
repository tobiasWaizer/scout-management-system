package com.scout.scoutmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ScoutManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScoutManagementApplication.class, args);
	}

}

