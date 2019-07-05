package uk.ac.ebi.subs.biostudies;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.ApplicationPidFileWriter;

@SpringBootApplication
public class BioStudiesAgentApp {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication( BioStudiesAgentApp.class);
        springApplication.run(args);
    }
}
