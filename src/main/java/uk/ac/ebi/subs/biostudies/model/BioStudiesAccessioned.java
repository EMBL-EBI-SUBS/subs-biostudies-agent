package uk.ac.ebi.subs.biostudies.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.stream.Stream;

public interface BioStudiesAccessioned {
    String getAccno();
    void setAccno(String accno);

    Stream<BioStudiesAccessioned> accessionedChildEntities();

    @JsonIgnore
    default boolean isAccessioned(){
        return getAccno() != null && !getAccno().isEmpty();
    }

}
