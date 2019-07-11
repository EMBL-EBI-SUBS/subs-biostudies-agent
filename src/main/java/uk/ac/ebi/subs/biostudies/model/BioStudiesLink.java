package uk.ac.ebi.subs.biostudies.model;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class BioStudiesLink {
    private String url;
    private List<BioStudiesAttribute> attributes = new ArrayList<>();
}
