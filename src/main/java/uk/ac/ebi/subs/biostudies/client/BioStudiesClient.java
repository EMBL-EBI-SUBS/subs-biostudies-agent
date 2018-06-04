package uk.ac.ebi.subs.biostudies.client;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Component
@RequiredArgsConstructor
@Data
public class BioStudiesClient {

    private static final Logger logger = LoggerFactory.getLogger(BioStudiesClient.class);

    @NonNull
    private final BioStudiesConfig config;
    private RestTemplate restTemplate = new RestTemplate();

    private static final String OK_STATUS = "OK";

    public BioStudiesSession initialiseSession() {

        BioStudiesLoginResponse loginResponse ;

        if (config.getSessionId() == null || config.getSessionId().isEmpty()){
            try {
                loginResponse = restTemplate.postForObject(
                        this.loginUri(),
                        config.getAuth(),
                        BioStudiesLoginResponse.class
                );
            }
            catch (HttpClientErrorException e){
                if (HttpStatus.FORBIDDEN.equals(e.getStatusCode())){
                    throw new IllegalArgumentException("login failed, check username and password");
                }
                logger.error("Http error during login");
                logger.error("Response code: {}",e.getRawStatusCode());
                logger.error("Response body: {}",e.getResponseBodyAsString());
                throw e;
            }

            if (!OK_STATUS.equals(loginResponse.getStatus())){
                throw new IllegalStateException("login failed: "+loginResponse);
            }
            if (loginResponse.getSessid() == null){
                throw new IllegalStateException("login did not produce session id: "+loginResponse);
            }
        }
        else {
            loginResponse = new BioStudiesLoginResponse();
            loginResponse.setSessid(config.getSessionId());
        }



        return BioStudiesSession.of(loginResponse, config, restTemplate);
    }

    private URI loginUri() {
        return URI.create(config.getServer() + "/auth/signin");
    }


}
