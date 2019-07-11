package uk.ac.ebi.subs.biostudies.validation;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.messaging.Exchanges;
import uk.ac.ebi.subs.validator.data.ProjectValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.SingleValidationResultsEnvelope;
import uk.ac.ebi.subs.validator.data.structures.SingleValidationResultStatus;

import java.util.Optional;

import static uk.ac.ebi.subs.biostudies.validation.ValidationMessaging.BIOSTUDIES_PROJECT_VALIDATION;
import static uk.ac.ebi.subs.biostudies.validation.ValidationMessaging.EVENT_VALIDATION_ERROR;
import static uk.ac.ebi.subs.biostudies.validation.ValidationMessaging.EVENT_VALIDATION_SUCCESS;

/**
 * This listener listens on the {@code BIOSTUDIES_PROJECT_VALIDATION} queue,
 * execute the validation for the published project entity
 * and send the validation results to the validation service.
 */
@Service
@Data
public class ValidationListener {
    private static final Logger logger = LoggerFactory.getLogger(ValidationListener.class);

    private RabbitMessagingTemplate rabbitMessagingTemplate;
    private ProjectValidator validator;

    public ValidationListener(RabbitMessagingTemplate rabbitMessagingTemplate, ProjectValidator validator) {
        this.rabbitMessagingTemplate = rabbitMessagingTemplate;
        this.validator = validator;
    }

   // @RabbitListener(queues = BIOSTUDIES_PROJECT_VALIDATION)
    public void handleValidationRequest(ProjectValidationMessageEnvelope envelope) {
        logger.info("Received validation request on project with id {}", envelope.getEntityToValidate().getId());

        SingleValidationResultsEnvelope singleValidationResultsEnvelope = validator.validateProject(envelope);

        sendResults(singleValidationResultsEnvelope);
    }

    @RabbitListener(queues = BIOSTUDIES_PROJECT_VALIDATION)
    public void listen(String mesage){
        logger.info(mesage);
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
