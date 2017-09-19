package uk.ac.ebi.subs.biostudies.client;

import lombok.Data;

import java.util.List;

@Data
public class SubmissionReport {
    private LogNode log;
    private List<SubmissionMapping> mapping;
    private String status;

    @Data
    public static class LogNode {
        private String level;
        private String message;
        private List<LogNode> subnodes;

    }

    @Data
    public static class SubmissionMapping {
        private AccessionMapping submissionMapping = new AccessionMapping();
        private List<AccessionMapping> sectionsMapping;
    }

    @Data
    public static class AccessionMapping {
        private String origAcc;
        private String assignedAcc;
        private int[] position;
    }

}
