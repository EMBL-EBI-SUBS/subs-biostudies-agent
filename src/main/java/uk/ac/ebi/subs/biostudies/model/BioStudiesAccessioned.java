package uk.ac.ebi.subs.biostudies.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface BioStudiesAccessioned {
    String getAccno();

    void setAccno(String accno);

    @JsonIgnore
    default boolean isAccessioned() {
        return getAccno() != null && !getAccno().isEmpty();
    }

}
