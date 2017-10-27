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

    private enum Commands{
        create,
        update
    }

    public SubmissionReport create(BioStudiesSubmission bioStudiesSubmission) {
        return commandBioStudies(bioStudiesSubmission,Commands.create,false);
    }

    public SubmissionReport update(BioStudiesSubmission bioStudiesSubmission) {
        return commandBioStudies(bioStudiesSubmission,Commands.update,false);
    }

    private SubmissionReport commandBioStudies(
            BioStudiesSubmission bioStudiesSubmission,
            Commands command,
            boolean validateOnly
    ){
        BioStudiesSubmissionWrapper wrapper = new BioStudiesSubmissionWrapper();
        wrapper.getSubmissions().add(bioStudiesSubmission);


        HttpEntity<SubmissionReport> response;
        try {
            response = restTemplate.postForEntity(
                    this.commandUri(command,validateOnly),
                    wrapper,
                    SubmissionReport.class
            );
        } catch (HttpClientErrorException e) {
            logger.error("Http error during create");
            logger.error("Response code: {}",e.getRawStatusCode());
            logger.error("Response body: {}",e.getResponseBodyAsString());
            throw e;
        }

        return response.getBody();
    }


    private URI commandUri(Commands command, boolean validateOnly) {
        StringBuilder queryParams = new StringBuilder("?" + SESSION_PARAM_NAME + "=" + bioStudiesLoginResponse.getSessid());

        if (validateOnly) {
            queryParams.append("&validateOnly=true");
        }


        return URI.create(
                bioStudiesConfig.getServer()
                        + "/submit/"
                        + command.name()
                        + queryParams

        );

    }

}