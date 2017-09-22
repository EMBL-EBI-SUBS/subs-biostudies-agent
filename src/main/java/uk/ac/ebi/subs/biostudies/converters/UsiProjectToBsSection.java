package uk.ac.ebi.subs.biostudies.converters;

import org.springframework.core.convert.converter.Converter;
import uk.ac.ebi.subs.biostudies.model.BioStudiesAttribute;
import uk.ac.ebi.subs.biostudies.model.BioStudiesSection;
import uk.ac.ebi.subs.data.submittable.Project;

public class UsiProjectToBsSection implements Converter<Project, BioStudiesSection> {

    private UsiPublicationsToBsSubsections usiPublicationsToBsSubsections = new UsiPublicationsToBsSubsections();
    private UsiContactsToBsSubSections usiContactsToBsSubSections = new UsiContactsToBsSubSections();

    @Override
    public BioStudiesSection convert(Project source) {
        BioStudiesSection studiesSection = new BioStudiesSection();
        studiesSection.setType("Study");

        studiesSection.getAttributes().add(
                BioStudiesAttribute.of("Description", source.getDescription())
        );
        studiesSection.getAttributes().add(
                BioStudiesAttribute.of("alias", source.getAlias())
        );

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

        return studiesSection;
    }

}