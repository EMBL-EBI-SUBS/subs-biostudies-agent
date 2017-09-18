package uk.ac.ebi.subs.biostudies.converters;

import org.springframework.core.convert.converter.Converter;
import uk.ac.ebi.subs.biostudies.model.BioStudiesAttribute;
import uk.ac.ebi.subs.biostudies.model.BioStudiesSubsection;
import uk.ac.ebi.subs.data.component.Contact;
import uk.ac.ebi.subs.data.component.Contacts;

import java.util.*;
import java.util.stream.Collectors;

public class UsiContactsToBsSubSections implements Converter<Contacts, List<BioStudiesSubsection>> {

    @Override
    public List<BioStudiesSubsection> convert(Contacts source) {
        Map<String, String> affiliationsToRefNames = affiliationRefs(source.getContacts());

        List<BioStudiesSubsection> subsections = source.getContacts().stream()
                .map(contact -> contactToSubsection(contact,affiliationsToRefNames))
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

        subsection.setType("Organization");
        subsection.setAccno(affiliationRefName.getValue());
        subsection.getAttributes().add(BioStudiesAttribute.of("Name",affiliationRefName.getKey()));

        return subsection;
    }


    private BioStudiesSubsection contactToSubsection(Contact contact, Map<String,String> affiliationRefNames) {
        BioStudiesSubsection subsection = new BioStudiesSubsection();
        subsection.setType("Author");

        if (contactName(contact) != null){
            subsection.getAttributes().add(BioStudiesAttribute.of("name",contactName(contact)));
        }

        if (contact.getFirstName() != null){
            subsection.getAttributes().add(BioStudiesAttribute.of("firstName",contact.getFirstName()));
        }
        if (contact.getMiddleInitials() != null){
            subsection.getAttributes().add(BioStudiesAttribute.of("middleInitials",contact.getMiddleInitials()));
        }
        if (contact.getLastName() != null){
            subsection.getAttributes().add(BioStudiesAttribute.of("lastName",contact.getLastName()));
        }
        if (contact.getEmail() != null){
            subsection.getAttributes().add(BioStudiesAttribute.of("email",contact.getEmail()));
        }
        if (contact.getAddress() != null){
            subsection.getAttributes().add(BioStudiesAttribute.of("address",contact.getAddress()));
        }
        if (contact.getPhone() != null){
            subsection.getAttributes().add(BioStudiesAttribute.of("phone",contact.getPhone()));
        }
        if (contact.getRoles() != null && !contact.getRoles().isEmpty()){
            subsection.getAttributes().add(BioStudiesAttribute.of("role", String.join(", ",contact.getRoles())));
        }
        if (contact.getAffiliation() != null && affiliationRefNames.containsKey(contact.getAffiliation())){
            String affiliationRef = affiliationRefNames.get(contact.getAffiliation());
            BioStudiesAttribute affiliationAttribute = BioStudiesAttribute.of("affiliation", affiliationRef);
            affiliationAttribute.setIsReference(Boolean.TRUE);
            subsection.getAttributes().add(affiliationAttribute);
        }


        return subsection;
    }

    private String contactName(Contact contact) {
        List<String> nameElements = new ArrayList<>();

        if (contact.getFirstName() != null){
            nameElements.add(contact.getFirstName());
        }
        if (contact.getMiddleInitials() != null){
            nameElements.add(contact.getMiddleInitials());
        }
        if (contact.getLastName() != null){
            nameElements.add(contact.getLastName());
        }

        String name = String.join(" ",nameElements);
        if (name.isEmpty()){
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
