package uk.ac.ebi.subs.biostudies.client;

import lombok.Data;
import lombok.NonNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.net.URI;


@Data
@ConfigurationProperties("usi.biostudies")
@Component
public class BioStudiesConfig {
    private String server;
    private Auth auth = new Auth();

    @Data
    public static class Auth {
        private String login;
        private String password;
    }
}
