package uk.ac.ebi.subs.biostudies.converters;

import lombok.Data;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import uk.ac.ebi.subs.biostudies.model.BioStudiesAttribute;
import uk.ac.ebi.subs.biostudies.model.BioStudiesSubsection;
import uk.ac.ebi.subs.data.component.Publication;
import uk.ac.ebi.subs.data.component.PublicationStatus;
import uk.ac.ebi.subs.data.component.Publications;

import java.util.List;
import java.util.stream.Collectors;

import static uk.ac.ebi.subs.biostudies.converters.ConverterUtils.addBioStudiesAttributeIfNotNull;

/**
 * This component responsible for converting USI {@link Publications} entity
 * to a list of BioStudies {@link BioStudiesSubsection} entities.
 */
@Component
@Data
public class UsiPublicationsToBsSubsections implements Converter<Publications, List<BioStudiesSubsection>> {
    @Override
    public List<BioStudiesSubsection> convert(Publications source) {
        List<BioStudiesSubsection> subsections = source.getPublications().stream()
                .map(publication -> publicationToSubsection(publication))
                .collect(Collectors.toList());

        return subsections;
    }

    private BioStudiesSubsection publicationToSubsection(Publication publication) {
        BioStudiesSubsection subsection = new BioStudiesSubsection();

        subsection.setType("Publication");

        addBioStudiesAttributeIfNotNull(subsection.getAttributes(), "doi", publication.getDoi());
        String accno = publication.getDoi();

        addBioStudiesAttributeIfNotNull(subsection.getAttributes(), "pubmedId", publication.getPubmedId());
        if (publication.getPubmedId() != null) {
            accno = pubmedIdNumericPart(publication.getPubmedId());
        }

        addBioStudiesAttributeIfNotNull(subsection.getAttributes(), "articleTitle", publication.getArticleTitle());
        addBioStudiesAttributeIfNotNull(subsection.getAttributes(), "journalTitle", publication.getJournalTitle());
        addBioStudiesAttributeIfNotNull(subsection.getAttributes(), "authors", publication.getAuthors());
        addBioStudiesAttributeIfNotNull(subsection.getAttributes(), "journalIssn", publication.getJournalIssn());
        addBioStudiesAttributeIfNotNull(subsection.getAttributes(), "issue", publication.getIssue());
        addBioStudiesAttributeIfNotNull(subsection.getAttributes(), "year", publication.getYear());
        addBioStudiesAttributeIfNotNull(subsection.getAttributes(), "volume", publication.getVolume());
        addBioStudiesAttributeIfNotNull(subsection.getAttributes(), "pageInfo", publication.getPageInfo());

        subsection.setAccno(accno);

        return subsection;
    }

    private String pubmedIdNumericPart(String pubmedId) {
        final String pubmedPrefix = "PMID:";
        if (pubmedId.startsWith(pubmedPrefix)) {
            return pubmedId.replace(pubmedPrefix, "");
        }

        return null;
    }
}
