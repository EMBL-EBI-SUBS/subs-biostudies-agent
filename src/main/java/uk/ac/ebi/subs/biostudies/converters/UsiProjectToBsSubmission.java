package uk.ac.ebi.subs.biostudies.converters;

import lombok.Data;
import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import uk.ac.ebi.subs.biostudies.client.BioStudiesConfig;
import uk.ac.ebi.subs.biostudies.model.BioStudiesAttribute;
import uk.ac.ebi.subs.biostudies.model.BioStudiesSubmission;
import uk.ac.ebi.subs.biostudies.model.BioStudiesSubsection;
import uk.ac.ebi.subs.data.submittable.Project;

/**
 * This component responsible for converting USI {@link Project} entity
 * to BioStudies {@link BioStudiesSubmission} entity.
 */
@Component
@Data
public class UsiProjectToBsSubmission implements Converter<Project,BioStudiesSubmission> {

    private static final String SECTION_INTERNAL_ACCESSION = "PROJECT";
    private static final String SUBSECTION_INTERNAL_ACCESSION_PREFIX = "SECT";

    @NonNull
    private UsiProjectToBsSection usiProjectToBsSection;

    @NonNull
    private final BioStudiesConfig bioStudiesConfig;

    @Override
    public BioStudiesSubmission convert(Project source) {
        BioStudiesSubmission submission = new BioStudiesSubmission();

        submission.getAttributes().add(
                BioStudiesAttribute.builder().name("Title").value( source.getTitle()).build()
        );
        submission.getAttributes().add(
                BioStudiesAttribute.builder().name("ReleaseDate").value( source.getReleaseDate().toString()).build()
        );
        submission.getAttributes().add(
                BioStudiesAttribute.builder().name("DataSource").value( "USI").build()
        );
        submission.setType("submission");

        submission.setSection(usiProjectToBsSection.convert(source));

        int sectCounter = 0;

        submission.setAccno("!{"+ bioStudiesConfig.getAccessionPrefix() +"}");
        submission.getSection().setAccno(SECTION_INTERNAL_ACCESSION);
        for (BioStudiesSubsection subsection : submission.getSection().getSubsections()) {
            if (!subsection.isAccessioned()) {
                sectCounter++;
                subsection.setAccno(SUBSECTION_INTERNAL_ACCESSION_PREFIX + sectCounter);
            }
        }

        return submission;
    }
}
