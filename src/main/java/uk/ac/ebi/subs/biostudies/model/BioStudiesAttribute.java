package uk.ac.ebi.subs.biostudies.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor(staticName = "of")
public class BioStudiesAttribute {
    @NonNull private String name;
    @NonNull private String value;
    private Boolean isReference = false;
}
