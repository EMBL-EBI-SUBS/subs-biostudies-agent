package uk.ac.ebi.subs.biostudies.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import uk.ac.ebi.subs.biostudies.model.BioStudiesAttribute;
import uk.ac.ebi.subs.biostudies.model.BioStudiesSubsection;
import uk.ac.ebi.subs.data.component.Funding;
import uk.ac.ebi.subs.data.component.Fundings;

import java.util.List;
import java.util.stream.Collectors;

import static uk.ac.ebi.subs.biostudies.converters.ConverterUtils.addBioStudiesAttributeIfNotNull;

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

        addBioStudiesAttributeIfNotNull(subsection.getAttributes(), "grant_id", funding.getGrantId());
        addBioStudiesAttributeIfNotNull(subsection.getAttributes(), "grant_title", funding.getGrantTitle());
        addBioStudiesAttributeIfNotNull(subsection.getAttributes(), "Agency", funding.getOrganization());

        return subsection;
    }
}
