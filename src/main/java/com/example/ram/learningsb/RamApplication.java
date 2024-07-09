package com.example.ram.learningsb;

import com.example.ram.learningsb.config.ProjectConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement

public class RamApplication {
	public static void main(String[] args) {
		SpringApplication.run(RamApplication.class, args);
	}
}
