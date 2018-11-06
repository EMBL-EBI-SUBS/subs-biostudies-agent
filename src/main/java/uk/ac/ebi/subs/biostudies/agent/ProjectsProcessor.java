package uk.ac.ebi.subs.biostudies.agent;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.biostudies.client.BioStudiesClient;
import uk.ac.ebi.subs.biostudies.client.BioStudiesSession;
import uk.ac.ebi.subs.biostudies.client.SubmissionReport;
import uk.ac.ebi.subs.biostudies.converters.UsiProjectToBsSubmission;
import uk.ac.ebi.subs.biostudies.model.BioStudiesSubmission;
import uk.ac.ebi.subs.biostudies.model.DataOwner;
import uk.ac.ebi.subs.data.component.Archive;
import uk.ac.ebi.subs.data.status.ProcessingStatusEnum;
import uk.ac.ebi.subs.data.submittable.Project;
import uk.ac.ebi.subs.processing.ProcessingCertificate;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This service sends a list of projects (related to the submitted submission) to the BioStudies archive.
 */
@Service
@RequiredArgsConstructor
public class ProjectsProcessor {

    @NonNull private final BioStudiesClient bioStudiesClient;
    @NonNull private UsiProjectToBsSubmission converter;

    public List<ProcessingCertificate> processProjects(DataOwner dataOwner, List<Project> projects) {
        BioStudiesSession bioStudiesSession = bioStudiesClient.getBioStudiesSession();

        return projects
                .stream()
                .map(project -> processProject(dataOwner, project, bioStudiesSession))
                .collect(Collectors.toList());
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
}
