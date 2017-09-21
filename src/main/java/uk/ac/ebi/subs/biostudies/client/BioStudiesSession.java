package uk.ac.ebi.subs.biostudies.client;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.subs.biostudies.model.BioStudiesSubmission;
import uk.ac.ebi.subs.biostudies.model.BioStudiesSubmissionWrapper;

import java.net.URI;

@Data
@RequiredArgsConstructor(staticName = "of")
public class BioStudiesSession {
    @NonNull
    private BioStudiesLoginResponse bioStudiesLoginResponse;
    @NonNull
    private BioStudiesConfig bioStudiesConfig;
    @NonNull
    private RestTemplate restTemplate;

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
            throw e;
        }

        return response.getBody();
    }

    public String validate(BioStudiesSubmission bioStudiesSubmission) {
        HttpEntity<String> response;
        try {
            response = restTemplate.postForEntity(
                    this.createUri(true),
                    bioStudiesSubmission,
                    String.class //TODO temporary
            );
        } catch (HttpClientErrorException e) {
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