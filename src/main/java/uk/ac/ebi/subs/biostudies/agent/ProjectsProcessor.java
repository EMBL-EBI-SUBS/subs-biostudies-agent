package uk.ac.ebi.subs.biostudies.agent;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.biostudies.client.BioStudiesClient;
import uk.ac.ebi.subs.biostudies.client.BioStudiesSession;
import uk.ac.ebi.subs.biostudies.client.SubmissionReport;
import uk.ac.ebi.subs.biostudies.converters.UsiProjectToBsSubmission;
import uk.ac.ebi.subs.biostudies.model.BioStudiesAttribute;
import uk.ac.ebi.subs.biostudies.model.BioStudiesLink;
import uk.ac.ebi.subs.biostudies.model.BioStudiesSubmission;
import uk.ac.ebi.subs.biostudies.model.BioStudiesSubsection;
import uk.ac.ebi.subs.biostudies.model.DataOwner;
import uk.ac.ebi.subs.data.component.Archive;
import uk.ac.ebi.subs.data.status.ProcessingStatusEnum;
import uk.ac.ebi.subs.data.submittable.Project;
import uk.ac.ebi.subs.processing.ProcessingCertificate;

/**
 * This service sends a list of projects (related to the submitted submission) to the BioStudies archive.
 */
@Service
@RequiredArgsConstructor
public class ProjectsProcessor {

    @NonNull private final BioStudiesClient bioStudiesClient;
    @NonNull private UsiProjectToBsSubmission converter;

    public ProcessingCertificate processProjects(DataOwner dataOwner, Project project) {
        BioStudiesSession bioStudiesSession = bioStudiesClient.getBioStudiesSession();

        return processProject(dataOwner, project, bioStudiesSession);
    }

    private ProcessingCertificate processProject(DataOwner dataOwner, Project project, BioStudiesSession bioStudiesSession) {

        BioStudiesSubmission bioStudiesSubmission = converter.convert(project);

        //TODO has this alias+team combo been used already

        if (project.isAccessioned()) {
            bioStudiesSubmission.setAccno(project.getAccession());
        }

        SubmissionReport report = bioStudiesSession.store(dataOwner, bioStudiesSubmission);

        String status = report.getStatus();
        String accession = report.findAccession();

        ProcessingStatusEnum outcome = ProcessingStatusEnum.Completed;
        String message = null;

        if (!status.equals("OK")){
            outcome = ProcessingStatusEnum.Error;
            message = String.join("; ",report.findMessages("ERROR"));
        }

        ProcessingCertificate cert = new ProcessingCertificate(
                project,
                Archive.BioStudies,
                outcome
        );
        if (accession != null){
            cert.setAccession(accession);
        }
        if (message != null){
            cert.setMessage(message);
        }

        return cert;
    }

    public void processUpdate(String submissionId, List<String> samples) {
        BioStudiesSession session = bioStudiesClient.getBioStudiesSession();
        BioStudiesSubmission submission = session.getSubmission(submissionId);
        submission.getSection().setLinks(samples.stream().map(this::createLink).collect(toList()));
        session.update(submission);
    }

    private BioStudiesLink createLink(String sampleId){
        BioStudiesLink link = new BioStudiesLink();
        link.setAttributes(singletonList(BioStudiesAttribute.builder().name("Type").value("BioSample").build()));
        link.setUrl(sampleId);
        return link;
    }
}
