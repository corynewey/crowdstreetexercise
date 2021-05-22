package com.newey.crowdstreetexercise;

import com.newey.crowdstreetexercise.persistence.entities.RequestEntity;
import com.newey.crowdstreetexercise.persistence.repository.RequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CrowdstreetexerciseApplication {

	private static final Logger log = LoggerFactory.getLogger(CrowdstreetexerciseApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(CrowdstreetexerciseApplication.class, args);
	}

	@Bean
	public CommandLineRunner demo(RequestRepository repository) {
		return (args) -> {
			// Save a couple Requests
			repository.save(new RequestEntity("This is body 1", RequestEntity.Status.PROCESSED, "Detail one."));
			repository.save(new RequestEntity("This is body 2", RequestEntity.Status.COMPLETED, "Detail two."));
			repository.save(new RequestEntity("This is body 3", RequestEntity.Status.ERROR, "Detail three."));
			log.info("Requests found with findAll():");
			log.info("------------------------------");
			for (RequestEntity request : repository.findAll()) {
				log.info(request.toString());
			}
			log.info("");
		};
	}
}
