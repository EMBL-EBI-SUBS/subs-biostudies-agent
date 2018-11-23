package uk.ac.ebi.subs.biostudies.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Base model interface for get and set accession ID for a BioStudies entity.
 */
public interface BioStudiesAccessioned {
    String getAccno();

    void setAccno(String accno);

    @JsonIgnore
    default boolean isAccessioned() {
        return getAccno() != null && !getAccno().isEmpty();
    }

}
