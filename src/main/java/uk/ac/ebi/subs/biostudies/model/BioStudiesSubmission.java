package uk.ac.ebi.subs.biostudies.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Data
public class BioStudiesSubmission implements BioStudiesAccessioned {
    private String accno;
    private List<BioStudiesAttribute> attributes = new ArrayList<>();
    private BioStudiesSection section;
    private String type;

    @Override
    public Stream<BioStudiesAccessioned> accessionedChildEntities() {

        if (section == null){
            Collection<BioStudiesAccessioned> coll = Collections.emptyList();
            return coll.stream();
        }

        return Stream.concat(
          Stream.of(section),
          section.accessionedChildEntities()
        );
    }
}
