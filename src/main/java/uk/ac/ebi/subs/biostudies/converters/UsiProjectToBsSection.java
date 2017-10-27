package uk.ac.ebi.subs.biostudies.converters;

import lombok.Data;
import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import uk.ac.ebi.subs.biostudies.model.BioStudiesAttribute;
import uk.ac.ebi.subs.biostudies.model.BioStudiesSection;
import uk.ac.ebi.subs.data.submittable.Project;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Data
public class UsiProjectToBsSection implements Converter<Project, BioStudiesSection> {

    @NonNull
    private UsiPublicationsToBsSubsections usiPublicationsToBsSubsections;
    @NonNull
    private UsiContactsToBsSubSections usiContactsToBsSubSections;

    @Override
    public BioStudiesSection convert(Project source) {
        BioStudiesSection studiesSection = new BioStudiesSection();
        studiesSection.setType("Study");

        studiesSection.getAttributes().add(
                BioStudiesAttribute.builder().name("Description").value(source.getDescription()).build()
        );
        studiesSection.getAttributes().add(
                BioStudiesAttribute.builder().name("alias").value( source.getAlias()).build()
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