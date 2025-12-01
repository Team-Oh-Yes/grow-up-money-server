package com.ohyes.GrowUpMoney;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GrowUpMoneyApplication {

	public static void main(String[] args) {
		SpringApplication.run(GrowUpMoneyApplication.class, args);
        System.out.println("hello ohyes!");
	}

}
