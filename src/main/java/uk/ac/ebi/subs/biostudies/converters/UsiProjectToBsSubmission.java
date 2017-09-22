package uk.ac.ebi.subs.biostudies.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import uk.ac.ebi.subs.biostudies.model.BioStudiesAttribute;
import uk.ac.ebi.subs.biostudies.model.BioStudiesSection;
import uk.ac.ebi.subs.biostudies.model.BioStudiesSubmission;
import uk.ac.ebi.subs.biostudies.model.BioStudiesSubsection;
import uk.ac.ebi.subs.data.submittable.Project;

import java.text.SimpleDateFormat;

@Component
public class UsiProjectToBsSubmission implements Converter<Project,BioStudiesSubmission> {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd");
    private final UsiProjectToBsSection usiProjectToBsSection = new UsiProjectToBsSection();
    private final String projectAccessionPrefix = "SUBSPRJ";
    private final String sectionInternalAccession = "PROJECT";
    private final String subsectionInternalAccessionPrefix = "SECT";

    @Override
    public BioStudiesSubmission convert(Project source) {
        BioStudiesSubmission submission = new BioStudiesSubmission();

        submission.getAttributes().add(
                BioStudiesAttribute.of("Title", source.getTitle())
        );
        submission.getAttributes().add(
                BioStudiesAttribute.of("ReleaseDate", dateFormat.format(source.getReleaseDate()))
        );
        submission.getAttributes().add(
                BioStudiesAttribute.of("DataSource", "USI")
        );
        submission.setType("submission");

        submission.setSection(usiProjectToBsSection.convert(source));

        int sectCounter = 0;

        submission.setAccno("!{"+projectAccessionPrefix+"}");
        submission.getSection().setAccno(sectionInternalAccession);
        for (BioStudiesSubsection subsection : submission.getSection().getSubsections()) {
            if (!subsection.isAccessioned()) {
                sectCounter++;
                subsection.setAccno(subsectionInternalAccessionPrefix + sectCounter);
            }
        }


        return submission;
    }

}
