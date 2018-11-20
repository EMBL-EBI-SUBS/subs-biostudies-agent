package uk.ac.ebi.subs.biostudies.validation;

import org.springframework.stereotype.Component;
import uk.ac.ebi.subs.data.submittable.Project;
import uk.ac.ebi.subs.validator.data.SingleValidationResult;
import uk.ac.ebi.subs.validator.data.SingleValidationResultsEnvelope;
import uk.ac.ebi.subs.validator.data.ValidationMessageEnvelope;
import uk.ac.ebi.subs.validator.data.structures.SingleValidationResultStatus;
import uk.ac.ebi.subs.validator.data.structures.ValidationAuthor;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This component validates the {@link Project} entity sent to the BioStudies validator.
 * It validates the project's title, description and release date.
 */
@Component
public class ProjectValidator {

    public final int MINIMUM_TITLE_CHAR_LENGTH = 25;
    public final int MAXIMUM_TITLE_CHAR_LENGTH = 4000;
    public final int MINIMUM_DESCRIPTION_CHAR_LENGTH = 50;
    public final int MAXIMUM_DESCRIPTION_CHAR_LENGTH = 4000;

    public final String MISSING_FIELD_ERROR_FORMAT = "A project must have a {0}.";
    public final String STRING_LENGTH_ERROR_FORMAT = "A project {0} must be between {1} and {2} characters long.";

    public SingleValidationResultsEnvelope validateProject(ValidationMessageEnvelope<Project> envelope) {
        Project project = envelope.getEntityToValidate();
        List<SingleValidationResult> singleValidationResults = new ArrayList<>();

        singleValidationResults.add(validateTitle(project));
        singleValidationResults.add(validateDescription(project));
        singleValidationResults.add(validateReleaseDate(project));

        List errorsList = singleValidationResults.stream()
                .filter(singleValidationResult -> !singleValidationResult.getValidationStatus().equals(SingleValidationResultStatus.Pass))
                .collect(Collectors.toList());

        if (errorsList.isEmpty()) {
            return generateSingleValidationResultsEnvelope(
                    Arrays.asList(generateDefaultSingleValidationResult(project.getId())),
                    envelope
            );

        } else {
            return generateSingleValidationResultsEnvelope(errorsList, envelope);
        }
    }

    private SingleValidationResult validateTitle(Project project) {

        return validateRequiredString(
                project.getTitle(), project, "title", MINIMUM_TITLE_CHAR_LENGTH, MAXIMUM_TITLE_CHAR_LENGTH
        );
    }

    private SingleValidationResult validateDescription(Project project) {
        return validateRequiredString(
                project.getDescription(),
                project,
                "description",
                MINIMUM_DESCRIPTION_CHAR_LENGTH,
                MAXIMUM_DESCRIPTION_CHAR_LENGTH
        );
    }

    private SingleValidationResult validateReleaseDate(Project project) {
        SingleValidationResult singleValidationResult = generateDefaultSingleValidationResult(project.getId());

        if (project.getReleaseDate() == null) {
            singleValidationResult.setValidationStatus(SingleValidationResultStatus.Error);
            singleValidationResult.setMessage(
                    MessageFormat.format(MISSING_FIELD_ERROR_FORMAT, "releaseDate")
            );
        }

        return singleValidationResult;
    }

    private SingleValidationResult validateRequiredString(
            String target,
            Project project,
            String fieldName,
            int minimumCharLength,
            int maximumCharLength
    ) {
        SingleValidationResult singleValidationResult = generateDefaultSingleValidationResult(project.getId());

        if (target == null || target.isEmpty()) {
            singleValidationResult.setValidationStatus(SingleValidationResultStatus.Error);
            singleValidationResult.setMessage(
                    MessageFormat.format(MISSING_FIELD_ERROR_FORMAT, fieldName)
            );
        } else if (target.trim().length() < minimumCharLength || target.trim().length() > maximumCharLength) {
            singleValidationResult.setValidationStatus(SingleValidationResultStatus.Error);
            singleValidationResult.setMessage(
                    MessageFormat.format(
                            STRING_LENGTH_ERROR_FORMAT,
                            fieldName,
                            minimumCharLength,
                            maximumCharLength
                    )
            );
        }

        return singleValidationResult;
    }

    private SingleValidationResult generateDefaultSingleValidationResult(String sampleId) {
        SingleValidationResult result = new SingleValidationResult(ValidationAuthor.BioStudies, sampleId);
        result.setValidationStatus(SingleValidationResultStatus.Pass);
        return result;
    }

    private SingleValidationResultsEnvelope generateSingleValidationResultsEnvelope(List<SingleValidationResult> singleValidationResults, ValidationMessageEnvelope envelope) {
        return new SingleValidationResultsEnvelope(singleValidationResults, envelope.getValidationResultVersion(), envelope.getValidationResultUUID(), ValidationAuthor.BioStudies);
    }
}
