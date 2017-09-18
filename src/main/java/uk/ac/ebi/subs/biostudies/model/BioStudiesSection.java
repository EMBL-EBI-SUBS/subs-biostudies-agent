package uk.ac.ebi.subs.biostudies.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BioStudiesSection {
    private String type;
    private List<BioStudiesSubsection> subsections = new ArrayList<>();
    private List<BioStudiesAttribute> attributes = new ArrayList<>();
}
