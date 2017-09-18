package uk.ac.ebi.subs.biostudies.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BioStudiesSubsection {
    private String type;
    private String accno;
    private List<BioStudiesAttribute> attributes = new ArrayList<>();
}
