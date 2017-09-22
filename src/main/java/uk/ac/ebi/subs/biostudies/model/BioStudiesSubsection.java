package uk.ac.ebi.subs.biostudies.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@Data
public class BioStudiesSubsection implements BioStudiesAccessioned{
    private String type;
    private String accno;
    private List<BioStudiesAttribute> attributes = new ArrayList<>();
}
