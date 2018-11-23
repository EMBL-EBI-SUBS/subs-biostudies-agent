package uk.ac.ebi.subs.biostudies.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * A model to represent a section related to BioStudies submission.
 */
@Data
public class BioStudiesSection implements BioStudiesAccessioned {
    private String accno;
    private String type;
    private List<BioStudiesSubsection> subsections = new ArrayList<>();
    private List<BioStudiesAttribute> attributes = new ArrayList<>();
}
