package uk.ac.ebi.subs.biostudies.client;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Data
public class SubmissionReport {
    private LogNode log;
    private String status;

    private static final String SUBMISSION_ACCESSIONED_MESSAGE_PREFIX = "Submission generated accNo: ";

    public String findAccession(){
        if (log == null){
            return null;
        }

        Optional<String> optionalString = log.nodeStream()
                .filter(node -> "INFO".equals(node.getLevel()))
                .map(node -> node.getMessage())
                .filter(message -> message.startsWith(SUBMISSION_ACCESSIONED_MESSAGE_PREFIX))
                .findAny();

        if (!optionalString.isPresent()){
            return null;
        }

        String accessionMessage = optionalString.get();
        String accession = accessionMessage.replace(SUBMISSION_ACCESSIONED_MESSAGE_PREFIX,"").trim();

        return accession;
    }

    @Data
    public static class LogNode {
        private String level;
        private String message;
        private List<LogNode> subnodes = new ArrayList<>();

        public Stream<LogNode> nodeStream() {
            if (this.subnodes == null){
                return Stream.of(this);
            }

            return Stream.concat(
              Stream.of(this),
              this.subnodes.stream().flatMap(LogNode::nodeStream)
            );
        }

    }


}
