package uk.ac.ebi.subs.biostudies.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BioStudiesSubmissionWrapper {

    private List<BioStudiesSubmission> submissions = new ArrayList<>();

}



