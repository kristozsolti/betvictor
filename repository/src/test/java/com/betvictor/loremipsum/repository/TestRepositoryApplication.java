package com.betvictor.loremipsum.repository;

import org.springframework.boot.SpringApplication;

public class TestRepositoryApplication {

	public static void main(String[] args) {
		SpringApplication.from(RepositoryApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
