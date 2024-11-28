package com.haruki.poker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.haruki.poker.repository")
public class PokerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PokerApplication.class, args);
	}

}
