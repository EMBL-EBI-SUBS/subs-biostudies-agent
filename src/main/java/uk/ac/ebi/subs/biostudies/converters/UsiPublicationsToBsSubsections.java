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

        String accno = null;

        if (publication.getDoi() != null) {
            subsection.getAttributes().add(
                    BioStudiesAttribute.builder().name("doi").value(publication.getDoi()).build()
            );
            accno = publication.getDoi();
        }

        if (publication.getPubmedId() != null) {
            subsection.getAttributes().add(
                    BioStudiesAttribute.builder().name("pubmedId").value(publication.getPubmedId()).build()
            );
            accno = pubmedIdNumericPart(publication.getPubmedId());
        }

        if (publication.getArticleTitle() != null){
            subsection.getAttributes().add(
                    BioStudiesAttribute.builder().name("articleTitle").value(publication.getArticleTitle()).build()
            );
        }
        if (publication.getJournalTitle() != null){
            subsection.getAttributes().add(
                    BioStudiesAttribute.builder().name("journalTitle").value(publication.getJournalTitle()).build()
            );
        }
        if (publication.getAuthors() != null){
            subsection.getAttributes().add(
                    BioStudiesAttribute.builder().name("authors").value(publication.getAuthors()).build()
            );
        }
        if (publication.getJournalIssn() != null){
            subsection.getAttributes().add(
                    BioStudiesAttribute.builder().name("journalIssn").value(publication.getJournalIssn()).build()
            );
        }
        if (publication.getIssue() != null){
            subsection.getAttributes().add(
                    BioStudiesAttribute.builder().name("issue").value(publication.getIssue()).build()
            );
        }
        if (publication.getYear() != null){
            subsection.getAttributes().add(
                    BioStudiesAttribute.builder().name("year").value(publication.getYear()).build()
            );
        }
        if (publication.getVolume() != null){
            subsection.getAttributes().add(
                    BioStudiesAttribute.builder().name("volume").value(publication.getVolume()).build()
            );
        }
        if (publication.getPageInfo() != null){
            subsection.getAttributes().add(
                    BioStudiesAttribute.builder().name("pageInfo").value(publication.getPageInfo()).build()
            );
        }

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
