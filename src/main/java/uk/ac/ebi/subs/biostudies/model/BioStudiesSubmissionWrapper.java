package uk.ac.ebi.subs.biostudies.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * This model represent a list of {@link BioStudiesSubmission}.
 */
@Data
public class BioStudiesSubmissionWrapper {

    private List<BioStudiesSubmission> submissions = new ArrayList<>();

}



