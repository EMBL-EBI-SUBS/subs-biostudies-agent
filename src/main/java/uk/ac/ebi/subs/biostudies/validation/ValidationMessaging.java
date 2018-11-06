package uk.ac.ebi.subs.biostudies.validation;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import uk.ac.ebi.subs.messaging.ExchangeConfig;
import uk.ac.ebi.subs.messaging.Queues;

/**
 * RabbitMQ related messaging configuration for the BioStudies queue(s) and binding(s).
 */
@Configuration
@ComponentScan(basePackageClasses = ExchangeConfig.class)
public class ValidationMessaging {

    public static final String BIOSTUDIES_PROJECT_VALIDATION = "biostudies-project-validation";
    public static final String EVENT_VALIDATION_SUCCESS = "validation.success";
    public static final String EVENT_VALIDATION_ERROR = "validation.error";

    public static final String EVENT_BIOSTUDIES_PROJECT_VALIDATION = "biostudies.project.validation";

    /**
     * Instantiate a {@link Queue} for validate samples related to BioStudies.
     *
     * @return an instance of a {@link Queue} for validate samples related to BioSamples.
     */
    @Bean
    public Queue biotudiesProjectValidationQueue() {
        return Queues.buildQueueWithDlx(BIOSTUDIES_PROJECT_VALIDATION);
    }

    /**
     * Create a {@link Binding} between the validation exchange and BioStudies project validation queue
     * using the routing key of projects
     *
     * @param biotudiesProjectValidationQueue {@link Queue} for validating BioStudies projects
     * @param submissionExchange {@link TopicExchange} for validation
     * @return a {@link Binding} between the validation exchange and BioStudies project validation queue
     * using the routing key of created samples related to BioSamples.
     */
    @Bean
    public Binding validationForCreatedBiosamplesSampleBinding(Queue biotudiesProjectValidationQueue, TopicExchange submissionExchange) {
        return BindingBuilder.bind(biotudiesProjectValidationQueue).to(submissionExchange)
                .with(EVENT_BIOSTUDIES_PROJECT_VALIDATION);
    }

}
