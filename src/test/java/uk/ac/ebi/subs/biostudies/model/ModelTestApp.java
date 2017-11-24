package uk.ac.ebi.subs.biostudies.model;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ModelTestApp {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(ModelTestApp.class);
        springApplication.run(args);
    }
}