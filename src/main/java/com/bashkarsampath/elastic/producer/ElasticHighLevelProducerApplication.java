package com.bashkarsampath.elastic.producer;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ComponentScan(basePackages = { "com.bashkarsampath.elastic.producer.*" })
@SpringBootApplication
@RestController
public class ElasticHighLevelProducerApplication implements CommandLineRunner {
	private long counter = 0l;

	public static void main(String[] args) {
		SpringApplication.run(ElasticHighLevelProducerApplication.class, args);
	}

	public void run(String... args) throws Exception {
		log.info("Application started");
	}

	@GetMapping("/")
	public ResponseEntity<?> respondSuccess() {
		counter += 1;
		return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
	}

	@GetMapping("/count")
	public ResponseEntity<?> respondCounter() {
		return new ResponseEntity<>(counter, HttpStatus.OK);
	}
}