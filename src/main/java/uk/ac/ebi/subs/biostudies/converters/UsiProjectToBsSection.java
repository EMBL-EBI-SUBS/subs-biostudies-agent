package uk.ac.ebi.subs.biostudies.converters;

import lombok.Data;
import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import uk.ac.ebi.subs.biostudies.model.BioStudiesAttribute;
import uk.ac.ebi.subs.biostudies.model.BioStudiesSection;
import uk.ac.ebi.subs.data.submittable.Project;

import static uk.ac.ebi.subs.biostudies.converters.ConverterUtils.addBioStudiesAttributeIfNotNull;

/**
 * This component responsible for converting USI {@link Project} entity
 * to BioStudies {@link BioStudiesSection} entity.
 */
@Component
@Data
public class UsiProjectToBsSection implements Converter<Project, BioStudiesSection> {

    @NonNull
    private UsiPublicationsToBsSubsections usiPublicationsToBsSubsections;
    @NonNull
    private UsiContactsToBsSubSections usiContactsToBsSubSections;
    @NonNull
    private UsiFundingsToBsSubSections usiFundingsToBsSubSections;

    @Override
    public BioStudiesSection convert(Project source) {
        BioStudiesSection studiesSection = new BioStudiesSection();
        studiesSection.setType("Study");

        addBioStudiesAttributeIfNotNull(studiesSection.getAttributes(), "Description", source.getDescription());
        addBioStudiesAttributeIfNotNull(studiesSection.getAttributes(), "alias", source.getAlias());

        if (source.getContacts() != null) {
            studiesSection.getSubsections().addAll(
                    usiContactsToBsSubSections.convert(source)
            );
        }

        if (source.getPublications() != null) {
            studiesSection.getSubsections().addAll(
                    usiPublicationsToBsSubsections.convert(source)
            );
        }

        if (source.getFundings() != null) {
            studiesSection.getSubsections().addAll(
                    usiFundingsToBsSubSections.convert(source)
            );
        }

        return studiesSection;
    }
}