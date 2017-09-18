package uk.ac.ebi.subs.biostudies.converters;

import org.springframework.core.convert.converter.Converter;
import uk.ac.ebi.subs.biostudies.model.BioStudiesAttribute;
import uk.ac.ebi.subs.biostudies.model.BioStudiesSubsection;
import uk.ac.ebi.subs.data.component.Publication;
import uk.ac.ebi.subs.data.component.Publications;

import java.util.List;
import java.util.stream.Collectors;

public class UsiPublicationsToBsSubsections implements Converter<Publications, List<BioStudiesSubsection>> {
    @Override
    public List<BioStudiesSubsection> convert(Publications source) {


        List<BioStudiesSubsection> subsections = source.getPublications().stream()
                .map(
                        publication -> publicationToSubsection(publication)
                )
                .collect(Collectors.toList());


        return subsections;
    }

    private BioStudiesSubsection publicationToSubsection(Publication publication) {
        BioStudiesSubsection subsection = new BioStudiesSubsection();

        subsection.setType("Publication");

        if (publication.getPubmedId() != null) {
            subsection.getAttributes().add(
                    BioStudiesAttribute.of("pubmedId", publication.getPubmedId())
            );
        }

        if (publication.getDoi() != null) {
            subsection.getAttributes().add(
                    BioStudiesAttribute.of("doi", publication.getDoi())
            );
        }



        return subsection;
    }
}
