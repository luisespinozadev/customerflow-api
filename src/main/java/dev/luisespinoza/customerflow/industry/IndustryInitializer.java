package dev.luisespinoza.customerflow.industry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IndustryInitializer implements CommandLineRunner {

    @Autowired
    private IndustryRepository repository;

    @Override
    public void run(String... args) throws Exception {

        if (repository.findByName("Automotive").isEmpty()) {
            repository.save(Industry.builder().name("Automotive").build());
        }

        if (repository.findByName("Education").isEmpty()) {
            repository.save(Industry.builder().name("Education").build());
        }

        if (repository.findByName("Fashion").isEmpty()) {
            repository.save(Industry.builder().name("Fashion").build());
        }

        if (repository.findByName("Software Development").isEmpty()) {
            repository.save(Industry.builder().name("Software Development").build());
        }

    }
}
