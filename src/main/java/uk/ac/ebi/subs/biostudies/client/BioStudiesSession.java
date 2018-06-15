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
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;
import uk.ac.ebi.subs.biostudies.model.BioStudiesSubmission;
import uk.ac.ebi.subs.biostudies.model.BioStudiesSubmissionWrapper;
import uk.ac.ebi.subs.biostudies.model.DataOwner;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        } catch (HttpServerErrorException e) {
            logger.error("Http server error during createupdate");
            logger.error("Response code: {}", e.getRawStatusCode());
            logger.error("Response body: {}", e.getResponseBodyAsString());
            throw e;

        } catch (HttpClientErrorException e) {
            logger.error("Http client error during createupdate");
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
        logger.trace("submission response:");
        logger.trace(submissionReport);
    }

    private void logSubmission(BioStudiesSubmissionWrapper wrapper) {
        ObjectMapper om = new ObjectMapper();
        try {
            String jsonSubmission = om.writeValueAsString(wrapper);
            logger.trace("Submission as json:");
            logger.trace(jsonSubmission);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }


    private URI commandUri(DataOwner dataOwner, boolean validateOnly) {
        Map<String, String> parameters = new LinkedHashMap<>();

        parameters.put(SESSION_PARAM_NAME, bioStudiesLoginResponse.getSessid());
        parameters.put("sse", "true"); //enables super user actions

        if (validateOnly) {
            parameters.put("validateOnly", "true");
        }

        parameters.put("onBehalf", dataOwner.getEmail());
        parameters.put("name", dataOwner.getName());
        parameters.put("domain", dataOwner.getTeamName());


        List<String> params = parameters.entrySet().stream()
                .map(entry -> {
                    try {
                        return entry.getKey() + "=" + UriUtils.encodeQueryParam(entry.getValue(), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());


        String queryString = "?" + String.join("&", params);

        return URI.create(
                bioStudiesConfig.getServer()
                        + "/submit/createupdate"
                        + queryString

        );

    }

}