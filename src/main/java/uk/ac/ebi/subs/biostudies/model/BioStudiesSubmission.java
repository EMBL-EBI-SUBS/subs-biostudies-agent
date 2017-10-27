package uk.ac.ebi.subs.biostudies.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BioStudiesSubmission implements BioStudiesAccessioned {
    private String accno;
    private List<BioStudiesAttribute> attributes = new ArrayList<>();
    private BioStudiesSection section;
    private String type;
}
