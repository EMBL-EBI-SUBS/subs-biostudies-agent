package uk.ac.ebi.subs.biostudies.model;

import lombok.Builder;
import lombok.Data;

/**
 * This section represent the data owner in the BioStudies archive.
 */
@Data
@Builder
public class DataOwner {
    private String email;
    private String name;
    private String teamName;
}
