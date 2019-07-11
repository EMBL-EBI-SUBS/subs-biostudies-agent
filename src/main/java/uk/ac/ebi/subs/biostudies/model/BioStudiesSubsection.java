package uk.ac.ebi.subs.biostudies.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * A model to represent a sub section related to BioStudies submission.
 */
@Data
public class BioStudiesSubsection implements BioStudiesAccessioned {
    private String type;
    private String accno;
    private List<BioStudiesAttribute> attributes = new ArrayList<>();
    private List<BioStudiesLink> links = new ArrayList<>();
}
