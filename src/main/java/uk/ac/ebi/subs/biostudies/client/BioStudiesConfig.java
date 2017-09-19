package uk.ac.ebi.subs.biostudies.client;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;

@Data
@ConfigurationProperties("usi.biostudies")
public class BioStudiesConfig {
    private String server;
    private Auth auth = new Auth();


    @Data
    public static class Auth {
        public String login;
        public String password;
    }
}
