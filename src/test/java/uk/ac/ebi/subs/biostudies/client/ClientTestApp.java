package uk.ac.ebi.subs.biostudies.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ClientTestApp {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(uk.ac.ebi.subs.biostudies.client.ClientTestApp.class);
        springApplication.run(args);
    }
}