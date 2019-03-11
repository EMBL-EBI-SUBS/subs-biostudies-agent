package uk.ac.ebi.subs.biostudies.model;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.subs.biostudies.TestUtil;
import uk.ac.ebi.subs.biostudies.client.BioStudiesConfig;
import uk.ac.ebi.subs.biostudies.converters.UsiContactsToBsSubSections;
import uk.ac.ebi.subs.biostudies.converters.UsiFundingsToBsSubSections;
import uk.ac.ebi.subs.biostudies.converters.UsiProjectToBsSection;
import uk.ac.ebi.subs.biostudies.converters.UsiProjectToBsSubmission;
import uk.ac.ebi.subs.biostudies.converters.UsiPublicationsToBsSubsections;
import uk.ac.ebi.subs.data.submittable.Project;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
        BioStudiesConfig.class,
        UsiProjectToBsSubmission.class,
        UsiProjectToBsSection.class,
        UsiPublicationsToBsSubsections.class,
        UsiContactsToBsSubSections.class,
        UsiFundingsToBsSubSections.class
})
@EnableAutoConfiguration
public class ConvertUsiToBioStudies {

    private Project usiProject;
    private BioStudiesSubmission bioStudiesSubmission;
    private BioStudiesSubmission actual;

    @Autowired
    private UsiProjectToBsSubmission usiProjectToBsSubmission;

    @Before
    public void buildUp() {
        usiProject = (Project) TestUtil.loadObjectFromJson(
                "exampleProject_usi.json",
                Project.class
        );
        bioStudiesSubmission = (BioStudiesSubmission) TestUtil.loadObjectFromJson(
                "exampleProject_biostudies.json",
                BioStudiesSubmission.class
        );
        actual = usiProjectToBsSubmission.convert(usiProject);
    }

    @Test
    public void testSubmissionTopLevel() {
        assertEquals(bioStudiesSubmission.getAccno(), actual.getAccno());
        assertEquals(bioStudiesSubmission.getAttributes(), actual.getAttributes());
        assertEquals(bioStudiesSubmission.getType(), actual.getType());
    }

    @Test
    public void testSection() {
        assertEquals(bioStudiesSubmission.getSection(), actual.getSection());
    }

    @Test
    public void testSubsections() {
        assertEquals(bioStudiesSubmission.getSection().getSubsections(), actual.getSection().getSubsections());
    }

    @Test
    public void testPublications() {
        testSpecifiedSubsections("Publication");
    }

    @Test
    public void testAuthors() {
        testSpecifiedSubsections("Author");
    }

    @Test
    public void testOrganisations() {
        testSpecifiedSubsections("Organisation");
    }

    @Test
    public void testEachSubsection() {
        List<BioStudiesSubsection> actualSubsections = actual.getSection().getSubsections();
        List<BioStudiesSubsection> expectedSubsections = bioStudiesSubmission.getSection().getSubsections();

        for (int i = 0; i < actualSubsections.size(); i++) {
            assertEquals(expectedSubsections.get(i), actualSubsections.get(i));
        }
    }

    private void testSpecifiedSubsections(String type) {
        assertEquals(
                fetchSubsection(bioStudiesSubmission, type),
                fetchSubsection(actual, type)
        );
    }

    private List<BioStudiesSubsection> fetchSubsection(BioStudiesSubmission submission, String type) {
        return submission.getSection().getSubsections().stream().filter(subsection -> type.equals(subsection.getType())).collect(Collectors.toList());
    }

    @Test
    public void testEntirety() {
        assertEquals(bioStudiesSubmission, actual);
    }

}
