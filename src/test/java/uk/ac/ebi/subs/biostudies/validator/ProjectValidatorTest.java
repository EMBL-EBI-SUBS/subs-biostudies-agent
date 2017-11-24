package uk.ac.ebi.subs.biostudies.validator;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.subs.biostudies.validation.ProjectValidator;
import uk.ac.ebi.subs.data.component.Team;
import uk.ac.ebi.subs.data.submittable.Project;
import uk.ac.ebi.subs.validator.data.SingleValidationResultsEnvelope;
import uk.ac.ebi.subs.validator.data.ValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.structures.SingleValidationResultStatus;


import java.time.LocalDate;
import java.util.UUID;
import java.util.stream.IntStream;

public class ProjectValidatorTest {

    private ProjectValidator validator;
    private String gibberishText;


    @Before
    public void buildUp() {
        validator = new ProjectValidator();
        gibberishText = makeText(100);
    }


    @Test
    public void testHappyTrail() {
        Project p = project();
        p.setReleaseDate(LocalDate.MIN);
        p.setTitle(gibberishText);
        p.setDescription(gibberishText);


        ValidationMessageEnvelope<Project> envelope = new ValidationMessageEnvelope<>();
        envelope.setEntityToValidate(p);

        SingleValidationResultsEnvelope validationResultsEnvelope = validator.validateProject(envelope);

        Assert.assertEquals(1, validationResultsEnvelope.getSingleValidationResults().size());

        Assert.assertTrue(validationResultsEnvelope.getSingleValidationResults().get(0).getValidationStatus().equals(SingleValidationResultStatus.Pass));

        Assert.assertEquals(null, validationResultsEnvelope.getSingleValidationResults().get(0).getMessage());
    }

    @Test
    public void testHorribleTrail() {
        Project p = project();
        p.setReleaseDate(null);
        p.setTitle("short");
        p.setDescription("short");


        ValidationMessageEnvelope<Project> envelope = new ValidationMessageEnvelope<>();
        envelope.setEntityToValidate(p);

        SingleValidationResultsEnvelope validationResultsEnvelope = validator.validateProject(envelope);

        Assert.assertEquals(3, validationResultsEnvelope.getSingleValidationResults().size());

        validationResultsEnvelope.getSingleValidationResults().stream()
                .forEach(result -> {

                    Assert.assertEquals(SingleValidationResultStatus.Error, result.getValidationStatus());
                    Assert.assertTrue(result.getMessage() != null);

                });

    }

    @Test
    public void testEmptyTrail() {
        Project p = project();
        p.setReleaseDate(null);
        p.setTitle(null);
        p.setDescription(null);


        ValidationMessageEnvelope<Project> envelope = new ValidationMessageEnvelope<>();
        envelope.setEntityToValidate(p);

        SingleValidationResultsEnvelope validationResultsEnvelope = validator.validateProject(envelope);

        Assert.assertEquals(3, validationResultsEnvelope.getSingleValidationResults().size());

        validationResultsEnvelope.getSingleValidationResults().stream()
                .forEach(result -> {

                    Assert.assertEquals(SingleValidationResultStatus.Error, result.getValidationStatus());
                    Assert.assertTrue(result.getMessage() != null);

                });
    }


    static Project project() {
        Project p = new Project();
        p.setTeam(Team.build("test"));
        p.setAlias("alias");

        return p;
    }

    static ValidationMessageEnvelope<Project> generateValidationMessageEnvelope(Project project) {
        return new ValidationMessageEnvelope<Project>(
                UUID.randomUUID().toString(),
                1,
                project
        );
    }

    static String makeText(int desiredLength) {
        StringBuilder buffer = new StringBuilder();

        IntStream.range(0, desiredLength)
                .map(i -> (char) i)
                .forEach(c -> buffer.append(c));


        return buffer.toString();
    }
}
