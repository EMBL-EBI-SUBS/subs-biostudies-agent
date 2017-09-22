package uk.ac.ebi.subs.biostudies.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.data.Submission;
import uk.ac.ebi.subs.data.submittable.Project;
import uk.ac.ebi.subs.messaging.Exchanges;
import uk.ac.ebi.subs.messaging.Topics;
import uk.ac.ebi.subs.processing.ProcessingCertificate;
import uk.ac.ebi.subs.processing.ProcessingCertificateEnvelope;
import uk.ac.ebi.subs.processing.SubmissionEnvelope;

import java.util.List;


@Service
public class Listener {
    private static final Logger logger = LoggerFactory.getLogger(Listener.class);

    private RabbitMessagingTemplate rabbitMessagingTemplate;
    private ProjectsProcessor projectsProcessor;

    public Listener(ProjectsProcessor projectsProcessor, RabbitMessagingTemplate rabbitMessagingTemplate, MessageConverter messageConverter) {
        this.rabbitMessagingTemplate = rabbitMessagingTemplate;
        this.rabbitMessagingTemplate.setMessageConverter(messageConverter);
        this.projectsProcessor = projectsProcessor;
    }

    public void handleProjectSubmission(SubmissionEnvelope submissionEnvelope) {
        Submission submission = submissionEnvelope.getSubmission();

        logger.info("Received submission {}", submission.getId());


        List<Project> projects = submissionEnvelope.getProjects();

        List<ProcessingCertificate> certificatesCompleted = projectsProcessor.processProjects(projects);

        ProcessingCertificateEnvelope certificateEnvelopeCompleted = new ProcessingCertificateEnvelope(
                submission.getId(),
                certificatesCompleted
        );
        logger.info("Processed submission {} producing {} certificates",
                submission.getId(),
                certificatesCompleted.size()
        );

        rabbitMessagingTemplate.convertAndSend(Exchanges.SUBMISSIONS, Topics.EVENT_SUBMISSION_AGENT_RESULTS, certificateEnvelopeCompleted);
    }

}