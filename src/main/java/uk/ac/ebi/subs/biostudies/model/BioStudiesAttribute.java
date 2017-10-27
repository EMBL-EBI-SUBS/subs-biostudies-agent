package uk.ac.ebi.subs.biostudies.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class BioStudiesAttribute {
    @NonNull
    private String name;
    @NonNull
    private String value;

    @Builder.Default
    private boolean isReference = false;
}
