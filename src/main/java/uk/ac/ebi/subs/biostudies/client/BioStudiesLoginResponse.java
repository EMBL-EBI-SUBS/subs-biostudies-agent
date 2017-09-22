package uk.ac.ebi.subs.biostudies.client;

import lombok.Data;

@Data
public class BioStudiesLoginResponse {
    private String dropbox;
    private String email;
    private Boolean superuser;
    private String ssotoken;
    private String status;
    private String sessid;
    private String username;
}
