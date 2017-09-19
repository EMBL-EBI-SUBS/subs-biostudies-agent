package uk.ac.ebi.subs.biostudies.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Data
public class BioStudiesSection implements BioStudiesAccessioned {
    private String accno;
    private String type;
    private List<BioStudiesSubsection> subsections = new ArrayList<>();
    private List<BioStudiesAttribute> attributes = new ArrayList<>();

    @Override
    public Stream<BioStudiesAccessioned> accessionedChildEntities() {
        return Stream.concat(
                subsections.stream(),
                subsections.stream().flatMap(BioStudiesSubsection::accessionedChildEntities)
        );//TODO
    }
}
