package uk.ac.ebi.subs.biostudies.converters;

import uk.ac.ebi.subs.biostudies.model.BioStudiesAttribute;

import java.util.List;

class ConverterUtils {

    static void addBioStudiesAttributeIfNotNull(List<BioStudiesAttribute> bioStudiesAttributes,
                                                       String name, String value) {
        if (value != null && !value.isEmpty()) {
            bioStudiesAttributes.add(
                    BioStudiesAttribute.builder().name(name).value(value).build()
            );
        }
    }
}
