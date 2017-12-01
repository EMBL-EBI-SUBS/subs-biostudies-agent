package uk.ac.ebi.subs.biostudies.client;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.test.context.TestPropertySource;

@SpringBootApplication
@TestPropertySource(value = "classpath:application.properties")
public class BioStudiesClientTestContextConfiguration {}
