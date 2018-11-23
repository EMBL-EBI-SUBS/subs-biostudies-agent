package uk.ac.ebi.subs.biostudies.converters;

import lombok.Data;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import uk.ac.ebi.subs.biostudies.model.BioStudiesAttribute;
import uk.ac.ebi.subs.biostudies.model.BioStudiesSubsection;
import uk.ac.ebi.subs.data.component.Contact;
import uk.ac.ebi.subs.data.component.Contacts;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This component responsible for converting USI {@link Contact}s entiti(es)
 * to BioStudies {@link BioStudiesSubsection} entiti(es).
 */
@Component
@Data
public class UsiContactsToBsSubSections implements Converter<Contacts, List<BioStudiesSubsection>> {

    @Override
    public List<BioStudiesSubsection> convert(Contacts source) {
        Map<String, String> affiliationsToRefNames = affiliationRefs(source.getContacts());

        List<BioStudiesSubsection> subsections = source.getContacts().stream()
                .map(contact -> contactToSubsection(contact, affiliationsToRefNames))
                .collect(Collectors.toList());

        subsections.addAll(
                affiliationsToRefNames.entrySet().stream()
                        .map(this::affiliationRefToSubsection)
                        .collect(Collectors.toList())
        );

        return subsections;
    }

    private BioStudiesSubsection affiliationRefToSubsection(Map.Entry<String, String> affiliationRefName) {
        BioStudiesSubsection subsection = new BioStudiesSubsection();

        subsection.setType("Organisation");
        subsection.setAccno(affiliationRefName.getValue());
        subsection.getAttributes().add(
                BioStudiesAttribute.builder()
                        .name("Name")
                        .value(affiliationRefName.getKey())
                        .build());

        return subsection;
    }

    private BioStudiesSubsection contactToSubsection(Contact contact, Map<String, String> affiliationRefNames) {
        BioStudiesSubsection subsection = new BioStudiesSubsection();
        subsection.setType("Author");

        if (contactName(contact) != null) {
            subsection.getAttributes().add(BioStudiesAttribute.builder().name("Name").value(contactName(contact)).build());
        }

        if (contact.getFirstName() != null) {
            subsection.getAttributes().add(BioStudiesAttribute.builder().name("firstName").value(contact.getFirstName()).build());
        }
        if (contact.getMiddleInitials() != null) {
            subsection.getAttributes().add(BioStudiesAttribute.builder().name("middleInitials").value(contact.getMiddleInitials()).build());
        }
        if (contact.getLastName() != null) {
            subsection.getAttributes().add(BioStudiesAttribute.builder().name("lastName").value(contact.getLastName()).build());
        }
        if (contact.getEmail() != null) {
            subsection.getAttributes().add(BioStudiesAttribute.builder().name("E-mail").value(contact.getEmail()).build());
        }
        if (contact.getAddress() != null) {
            subsection.getAttributes().add(BioStudiesAttribute.builder().name("address").value(contact.getAddress()).build());
        }
        if (contact.getPhone() != null) {
            subsection.getAttributes().add(BioStudiesAttribute.builder().name("phone").value(contact.getPhone()).build());
        }
        if (contact.getRoles() != null && !contact.getRoles().isEmpty()) {
            subsection.getAttributes().add(BioStudiesAttribute.builder().name("role").value(String.join(", ", contact.getRoles())).build());
        }
        if (contact.getAffiliation() != null && affiliationRefNames.containsKey(contact.getAffiliation())) {
            String affiliationRef = affiliationRefNames.get(contact.getAffiliation());

            subsection.getAttributes().add(BioStudiesAttribute.builder().name("Organisation").value(affiliationRef).isReference(true).build());
        }

        return subsection;
    }

    private String contactName(Contact contact) {
        List<String> nameElements = new ArrayList<>();

        if (contact.getFirstName() != null) {
            nameElements.add(contact.getFirstName());
        }
        if (contact.getMiddleInitials() != null) {
            nameElements.add(contact.getMiddleInitials());
        }
        if (contact.getLastName() != null) {
            nameElements.add(contact.getLastName());
        }

        String name = String.join(" ", nameElements);
        if (name.isEmpty()) {
            return null;
        }

        return name;
    }

    private Map<String, String> affiliationRefs(List<Contact> contacts) {
        List<String> distinctAffiliations = contacts.stream()
                .map(Contact::getAffiliation)
                .filter(Objects::nonNull)
                .filter(affiliation -> !affiliation.isEmpty())
                .distinct()
                .collect(Collectors.toList());

        Map<String, String> affiliationRefs = new LinkedHashMap<>();

        int affiliationCounter = 1;

        for (String affiliation : distinctAffiliations) {
            String ref = "o" + affiliationCounter;
            affiliationCounter++;
            affiliationRefs.put(affiliation, ref);
        }

        return Collections.unmodifiableMap(affiliationRefs);
    }
}
