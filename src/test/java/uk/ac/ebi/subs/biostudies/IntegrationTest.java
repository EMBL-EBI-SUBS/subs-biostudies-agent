package uk.ac.ebi.subs.biostudies;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.subs.biostudies.agent.ProjectsProcessor;
import uk.ac.ebi.subs.biostudies.model.DataOwner;
import uk.ac.ebi.subs.data.component.Team;
import uk.ac.ebi.subs.data.status.ProcessingStatusEnum;
import uk.ac.ebi.subs.data.submittable.Project;
import uk.ac.ebi.subs.processing.ProcessingCertificate;

import java.time.LocalDate;

@RunWith(SpringRunner.class)
@Category(BioStudiesApiDependentTest.class)
@SpringBootTest(classes={BioStudiesAgentApp.class})
public class IntegrationTest {

    @Autowired
    ProjectsProcessor projectsProcessor;

    private Project project;
    private DataOwner dataOwner;

    @Before
    public void buildUp() {
        dataOwner = DataOwner.builder()
                .email("test@example.com")
                .name("John Doe")
                .teamName("subs.testTeam")
                .build();

        project = new Project();
        project.setAlias("pr1");
        project.setTitle("a short title");
        project.setDescription("a short description");
        project.setTeam(Team.build(dataOwner.getTeamName()));
        project.setReleaseDate(LocalDate.MIN);
    }

    @Test
    public void expect_failure(){
        ProcessingCertificate cert = projectsProcessor.processProjects(dataOwner,project);

        Assert.assertNotNull(cert);
        Assert.assertNotNull(cert.getMessage());
        Assert.assertEquals(cert.getProcessingStatus(),ProcessingStatusEnum.Error);
    }


}
