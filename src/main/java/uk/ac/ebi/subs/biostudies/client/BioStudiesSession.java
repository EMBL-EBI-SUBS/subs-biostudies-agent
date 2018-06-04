package uk.ac.ebi.subs.biostudies.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;
import uk.ac.ebi.subs.biostudies.model.BioStudiesSubmission;
import uk.ac.ebi.subs.biostudies.model.BioStudiesSubmissionWrapper;
import uk.ac.ebi.subs.biostudies.model.DataOwner;

import java.io.UnsupportedEncodingException;
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

    public SubmissionReport store(DataOwner dataOwner, BioStudiesSubmission bioStudiesSubmission) {
        BioStudiesSubmissionWrapper wrapper = new BioStudiesSubmissionWrapper();
        wrapper.getSubmissions().add(bioStudiesSubmission);

        logSubmission(wrapper);

        HttpEntity<SubmissionReport> response;
        try {
            response = restTemplate.postForEntity(
                    this.commandUri(dataOwner, false),
                    wrapper,
                    SubmissionReport.class
            );
        } catch (HttpClientErrorException e) {
            logger.error("Http error during createupdate");
            logger.error("Response code: {}", e.getRawStatusCode());
            logger.error("Response body: {}", e.getResponseBodyAsString());
            throw e;
        }

        logSubmissionResponse(response);


        return response.getBody();
    }

    private void logSubmissionResponse(HttpEntity<SubmissionReport> response) {
        ObjectMapper om = new ObjectMapper();
        String submissionReport = null;
        try {
            submissionReport = om.writeValueAsString(response.getBody());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        logger.debug("submission response:");
        logger.debug(submissionReport);
    }

    private void logSubmission(BioStudiesSubmissionWrapper wrapper) {
        ObjectMapper om = new ObjectMapper();
        try {
            String jsonSubmission = om.writeValueAsString(wrapper);
            logger.debug("Submission as json:");
            logger.debug(jsonSubmission);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }


    private URI commandUri(DataOwner dataOwner, boolean validateOnly) {
        StringBuilder queryParams = new StringBuilder("?" + SESSION_PARAM_NAME + "=" + bioStudiesLoginResponse.getSessid());

        queryParams.append("&sse=true"); //TODO find out what this does

        if (validateOnly) {
            queryParams.append("&validateOnly=true");
        }
        try {
            if (dataOwner.getEmail() != null) {
                queryParams.append("&onBehalf=");
                queryParams.append(UriUtils.encodeQueryParam(dataOwner.getEmail(), "UTF-8"));
            }
            if (dataOwner.getName() != null) {
                queryParams.append("&name=");
                queryParams.append(UriUtils.encodeQueryParam(dataOwner.getName(), "UTF-8"));
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        return URI.create(
                bioStudiesConfig.getServer()
                        + "/submit/createupdate"
                        + queryParams

        );

    }

}