package uk.ac.ebi.subs.biostudies.agent;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.ac.ebi.subs.biostudies.client.BioStudiesClient;
import uk.ac.ebi.subs.biostudies.client.BioStudiesSession;
import uk.ac.ebi.subs.biostudies.client.SubmissionReport;
import uk.ac.ebi.subs.biostudies.converters.UsiProjectToBsSubmission;
import uk.ac.ebi.subs.biostudies.model.BioStudiesSubmission;
import uk.ac.ebi.subs.data.component.Archive;
import uk.ac.ebi.subs.data.status.ProcessingStatusEnum;
import uk.ac.ebi.subs.data.submittable.Project;
import uk.ac.ebi.subs.processing.ProcessingCertificate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectsProcessor {

    @NonNull private final BioStudiesClient bioStudiesClient;
    @NonNull private UsiProjectToBsSubmission converter;

    public List<ProcessingCertificate> processProjects(List<Project> projects) {
        BioStudiesSession bioStudiesSession = bioStudiesClient.initialiseSession();

        return projects
                .stream()
                .map(project -> processProject(project,bioStudiesSession))
                .collect(Collectors.toList());
    }

    private ProcessingCertificate processProject(Project project, BioStudiesSession bioStudiesSession) {

        BioStudiesSubmission bioStudiesSubmission = converter.convert(project);

        //TODO has this alias+team combo been used already
        //TODO who owns the biostudies record - should create on behalf of submitter

        SubmissionReport report;

        if (project.isAccessioned()){
            bioStudiesSubmission.setAccno(project.getAccession());
            report = bioStudiesSession.update(bioStudiesSubmission);
        }
        else {
            report = bioStudiesSession.create(bioStudiesSubmission);
        }

        ProcessingCertificate cert = new ProcessingCertificate(
                project,
                Archive.BioStudies,
                ProcessingStatusEnum.Completed,
                report.findAccession()
        );


        return cert;
    }
}
