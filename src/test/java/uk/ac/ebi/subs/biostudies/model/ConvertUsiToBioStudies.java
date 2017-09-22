package uk.ac.ebi.subs.biostudies.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.subs.biostudies.TestUtil;
import uk.ac.ebi.subs.biostudies.converters.UsiProjectToBsSubmission;
import uk.ac.ebi.subs.data.submittable.Project;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringJUnit4ClassRunner.class)
public class ConvertUsiToBioStudies {

    private Project usiProject;
    private BioStudiesSubmission bioStudiesSubmission;
    private BioStudiesSubmission actual;

    private UsiProjectToBsSubmission usiProjectToBsSubmission = new UsiProjectToBsSubmission();

    @Test
    public void testSubmissionTopLevel() {
        Assert.assertEquals(bioStudiesSubmission.getAccno(), actual.getAccno());
        Assert.assertEquals(bioStudiesSubmission.getAttributes(), actual.getAttributes());
        Assert.assertEquals(bioStudiesSubmission.getType(), actual.getType());
    }

    @Test
    public void testSection() {
        Assert.assertEquals(bioStudiesSubmission.getSection(), actual.getSection());
    }

    @Test
    public void testSubsections() {
        Assert.assertEquals(bioStudiesSubmission.getSection().getSubsections(), actual.getSection().getSubsections());
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
            Assert.assertEquals(expectedSubsections.get(i), actualSubsections.get(i));
        }
    }

    private void testSpecifiedSubsections(String type) {
        Assert.assertEquals(
                fetchSubsection(bioStudiesSubmission, type),
                fetchSubsection(actual, type)
        );
    }

    private List<BioStudiesSubsection> fetchSubsection(BioStudiesSubmission submission, String type) {
        return submission.getSection().getSubsections().stream().filter(subsection -> type.equals(subsection.getType())).collect(Collectors.toList());
    }

    @Test
    public void testEntirety() {
        Assert.assertEquals(bioStudiesSubmission, actual);
    }

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


}
