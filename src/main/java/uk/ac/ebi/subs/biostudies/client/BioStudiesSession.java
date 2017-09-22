package uk.ac.ebi.subs.biostudies.client;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.subs.biostudies.model.BioStudiesSubmission;
import uk.ac.ebi.subs.biostudies.model.BioStudiesSubmissionWrapper;

import java.net.URI;

@Data
@RequiredArgsConstructor(staticName = "of")
public class BioStudiesSession {

    private static final Logger logger = LoggerFactory.getLogger(BioStudiesSession.class);

    @NonNull
    private final BioStudiesLoginResponse bioStudiesLoginResponse;
    @NonNull
    private final BioStudiesConfig bioStudiesConfig;
    @NonNull
    private final RestTemplate restTemplate;

    private static final String SESSION_PARAM_NAME = "BIOSTDSESS";

    public SubmissionReport submit(BioStudiesSubmission bioStudiesSubmission) {
        BioStudiesSubmissionWrapper wrapper = new BioStudiesSubmissionWrapper();
        wrapper.getSubmissions().add(bioStudiesSubmission);


        HttpEntity<SubmissionReport> response;
        try {
            response = restTemplate.postForEntity(
                    this.createUri(false),
                    wrapper,
                    SubmissionReport.class
            );
        } catch (HttpClientErrorException e) {
            logger.error("Http error during submit");
            logger.error("Response code: {}",e.getRawStatusCode());
            logger.error("Response body: {}",e.getResponseBodyAsString());
            throw e;
        }

        return response.getBody();
    }

    private URI createUri(boolean validateOnly) {
        StringBuilder queryParams = new StringBuilder("?" + SESSION_PARAM_NAME + "=" + bioStudiesLoginResponse.getSessid());

        if (validateOnly) {
            queryParams.append("&validateOnly=true");
        }


        return URI.create(
                bioStudiesConfig.getServer()
                        + "/submit/create"
                        + queryParams

        );

    }

}