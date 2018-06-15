package uk.ac.ebi.subs.biostudies.converters;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import uk.ac.ebi.subs.biostudies.model.DataOwner;
import uk.ac.ebi.subs.data.Submission;
import uk.ac.ebi.subs.data.component.Submitter;

@Component
@RequiredArgsConstructor
public class UsiSubmissionToDataOwner implements Converter<Submission, DataOwner> {

    @Override
    public DataOwner convert(Submission source) {
        Submitter submitter = source.getSubmitter();

        DataOwner dataOwner = DataOwner.builder()
                .email(submitter.getEmail())
                .name(submitter.getName())
                .teamName(source.getTeam().getName())
                .build();

        return dataOwner;
    }
}
