package uk.ac.ebi.subs.biostudies.agent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import uk.ac.ebi.subs.biostudies.client.BioStudiesClient;
import uk.ac.ebi.subs.biostudies.converters.UsiProjectToBsSubmission;
import uk.ac.ebi.subs.biostudies.model.DataOwner;
import uk.ac.ebi.subs.data.submittable.Project;
import uk.ac.ebi.subs.processing.ProcessingCertificate;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProjectsProcessorTest {

    private ProjectsProcessor projectsProcessor;

    @Autowired
    private UsiProjectToBsSubmission usiProjectToBsSubmission;

    @MockBean
    private BioStudiesClient bioStudiesClient;

    private Project project;

    @Before
    public void setup() {
        project = new Project();
    }

    @Test
    public void whenBioStudiesReturnsHttpServerException_ThenAgentReturnsErrorMessageToTheUser() {
        final String expectedMessage = "Gateway Timeout message";

        when(bioStudiesClient.getBioStudiesSession()).thenThrow(
                new HttpServerErrorException(HttpStatus.GATEWAY_TIMEOUT, expectedMessage));
        projectsProcessor = new ProjectsProcessor(bioStudiesClient, usiProjectToBsSubmission);

        ProcessingCertificate processingCertificate =
                projectsProcessor.processProjects(DataOwner.builder().build(), project);

        assertThat(processingCertificate.getMessage(),
                is(equalTo(String.format(ProjectsProcessor.PROJECT_PROCESSING_HAS_FAILED_MESSAGE, expectedMessage))));
    }

    @Test
    public void whenBioStudiesReturnsIllegalStateException_ThenAgentReturnsErrorMessageToTheUser() {
        final String expectedMessage = "login failed: Username or password invalid";

        when(bioStudiesClient.getBioStudiesSession()).thenThrow(
                new IllegalStateException(expectedMessage));
        projectsProcessor = new ProjectsProcessor(bioStudiesClient, usiProjectToBsSubmission);

        ProcessingCertificate processingCertificate =
                projectsProcessor.processProjects(DataOwner.builder().build(), project);

        assertThat(processingCertificate.getMessage(),
                is(equalTo(String.format(ProjectsProcessor.PROJECT_PROCESSING_HAS_FAILED_MESSAGE, expectedMessage))));
    }

    @Test
    public void whenBioStudiesReturnsHttpClientException_ThenAgentReturnsErrorMessageToTheUser() {
        final String expectedMessage = "Not found";

        when(bioStudiesClient.getBioStudiesSession()).thenThrow(
                new HttpClientErrorException(HttpStatus.NOT_FOUND, expectedMessage));
        projectsProcessor = new ProjectsProcessor(bioStudiesClient, usiProjectToBsSubmission);

        ProcessingCertificate processingCertificate =
                projectsProcessor.processProjects(DataOwner.builder().build(), project);

        assertThat(processingCertificate.getMessage(),
                is(equalTo(String.format(ProjectsProcessor.PROJECT_PROCESSING_HAS_FAILED_MESSAGE, expectedMessage))));
    }
}
