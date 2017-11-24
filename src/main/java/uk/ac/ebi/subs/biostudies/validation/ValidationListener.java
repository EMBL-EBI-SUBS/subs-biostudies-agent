package uk.ac.ebi.subs.biostudies.validation;

import lombok.Data;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.client.Project;
import uk.ac.ebi.subs.messaging.Exchanges;
import uk.ac.ebi.subs.validator.data.ProjectValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.SingleValidationResultsEnvelope;
import uk.ac.ebi.subs.validator.data.structures.SingleValidationResultStatus;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static uk.ac.ebi.subs.biostudies.validation.ValidationMessaging.*;

@Service
@Data
public class ValidationListener {

    private static final Logger logger = LoggerFactory.getLogger(ValidationListener.class);

    @NonNull
    private RabbitMessagingTemplate rabbitMessagingTemplate;

    @NonNull
    private ProjectValidator validator;


    @RabbitListener(queues = BIOSTUDIES_PROJECT_VALIDATION)
    public void handleValidationRequest(ProjectValidationMessageEnvelope envelope) {
        logger.info("Received validation request on sample with id {}", envelope.getEntityToValidate().getId());

        SingleValidationResultsEnvelope singleValidationResultsEnvelope = validator.validateProject(envelope);

        sendResults(singleValidationResultsEnvelope);
    }

    private void sendResults(SingleValidationResultsEnvelope envelope) {
        Optional<SingleValidationResult> errorResults = envelope.getSingleValidationResults().stream()
                .filter(svr -> svr.getValidationStatus().equals(SingleValidationResultStatus.Error))
                .findAny();

        if (errorResults.isPresent()) {
            rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, EVENT_VALIDATION_ERROR, envelope);
        } else {
            rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, EVENT_VALIDATION_SUCCESS, envelope);
        }
    }
}
