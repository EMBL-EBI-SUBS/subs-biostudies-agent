package uk.ac.ebi.subs.biostudies;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableRabbit
@SpringBootApplication
public class BioStudiesAgentApp {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication( BioStudiesAgentApp.class);
        springApplication.run(args);
    }
}
