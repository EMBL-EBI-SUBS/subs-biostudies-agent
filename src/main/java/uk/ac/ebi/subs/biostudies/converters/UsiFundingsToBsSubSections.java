package uk.ac.ebi.subs.biostudies.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import uk.ac.ebi.subs.biostudies.model.BioStudiesAttribute;
import uk.ac.ebi.subs.biostudies.model.BioStudiesSubsection;
import uk.ac.ebi.subs.data.component.Funding;
import uk.ac.ebi.subs.data.component.Fundings;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UsiFundingsToBsSubSections implements Converter<Fundings, List<BioStudiesSubsection>> {

    @Override
    public List<BioStudiesSubsection> convert(Fundings source) {
        return source.getFundings().stream()
                .map(this::fundingToSubsection)
                .collect(Collectors.toList());
    }

    private BioStudiesSubsection fundingToSubsection(Funding funding) {
        BioStudiesSubsection subsection = new BioStudiesSubsection();
        subsection.setType("Funding");

        if (funding.getGrantId() != null) {
            subsection.getAttributes().add(
                    BioStudiesAttribute.builder()
                            .name("grant_id")
                            .value(funding.getGrantId())
                            .build()
            );
        }

        if (funding.getGrantTitle() != null) {
            subsection.getAttributes().add(
                    BioStudiesAttribute.builder()
                            .name("grant_title")
                            .value(funding.getGrantTitle())
                            .build()
            );
        }

        if (funding.getOrganization() != null) {
            subsection.getAttributes().add(
                    BioStudiesAttribute.builder()
                            .name("Agency")
                            .value(funding.getOrganization())
                            .build()
            );
        }

        return subsection;
    }
}
