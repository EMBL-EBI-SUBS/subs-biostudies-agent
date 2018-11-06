package uk.ac.ebi.subs.biostudies.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

/**
 * A model for representing a BioStudies attribute.
 */
@Data
@Builder
public class BioStudiesAttribute {
    @NonNull
    private String name;
    @NonNull
    private String value;

    @Builder.Default
    private boolean isReference = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean getIsReference() {
        return isReference;
    }

    public void setIsReference(boolean reference) {
        isReference = reference;
    }
}
