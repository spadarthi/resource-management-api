package com.sp.poc.rsm.persistence;

import com.sp.poc.rsm.entity.Employee;
import com.sp.poc.rsm.enums.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(EmployeeRepository repository) {

        return args -> {
            log.info("Preloading " + repository.save(new Employee(1L, "Michael Jackson", "APAC", State.ADDED, 29)));
            log.info("Preloading " + repository.save(new Employee(2L, "AR Rehman", "EUROPE", State.ADDED, 45)));
            log.info("Preloading " + repository.save(new Employee(3L, "Sreeni Pad", "USA", State.ADDED, 35)));
            repository.findAll().forEach(employee -> log.info("Preloaded " + employee));
        };
    }
}
