package uk.ac.ebi.subs.biostudies.client;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.subs.biostudies.TestUtil;
import uk.ac.ebi.subs.biostudies.model.BioStudiesSubmission;
import uk.ac.ebi.subs.biostudies.model.BioStudiesSubsection;

import java.util.UUID;

//@RunWith(SpringJUnit4ClassRunner.class)
public class EstablishSession {

    private BioStudiesConfig config;
    private BioStudiesSubmission bioStudiesSubmission;

    @Value("${biostudiesUser}")
    private String biostudiesUser = "davidr@ebi.ac.uk";
    @Value("${biostudiesPassword}")
    private String biostudiesPassword = "hellougis";
    @Value("${biostudiesServer}")
    private String biostudiesServer = "http://biostudy-dev.ebi.ac.uk:10180/biostd-beta";


    @Before
    public void buildup() {
        config = new BioStudiesConfig();
        config.setServer(biostudiesServer);
        config.getAuth().setLogin(biostudiesUser);
        config.getAuth().setPassword(biostudiesPassword);

        bioStudiesSubmission = (BioStudiesSubmission) TestUtil.loadObjectFromJson(
                "exampleProject_biostudies.json", BioStudiesSubmission.class
        );
    }

    @Test
    public void login() {
        BioStudiesClient client = new BioStudiesClient(config);
        BioStudiesSession session = client.initialiseSession();

        Assert.assertEquals("OK", session.getBioStudiesLoginResponse().getStatus());
        Assert.assertNotNull(session.getBioStudiesLoginResponse().getSessid());
        System.out.println(session);
    }

    @Test(expected = IllegalArgumentException.class)
    public void loginFailure() {
        config.getAuth().setLogin(UUID.randomUUID().toString());
        config.getAuth().setPassword(UUID.randomUUID().toString());

        BioStudiesClient client = new BioStudiesClient(config);
        BioStudiesSession session = client.initialiseSession();
    }

    @Test
    public void submitGood() {
        BioStudiesClient client = new BioStudiesClient(config);
        BioStudiesSession session = client.initialiseSession();

        int sectCounter = 0;

        bioStudiesSubmission.setAccno("!{SUBSPRJ}");
        bioStudiesSubmission.getSection().setAccno("PROJECT");
        for (BioStudiesSubsection subsection : bioStudiesSubmission.getSection().getSubsections()){
            if (!subsection.isAccessioned()){
                sectCounter++;
                subsection.setAccno("SECT" + sectCounter);
            }
        }

        SubmissionReport response = session.submit(bioStudiesSubmission);

        Assert.assertEquals("SUCCESS",response.getStatus());
        Assert.assertNotNull(response.findAccession());
        Assert.assertTrue(response.findAccession().startsWith("SUBSPRJ"));

    }

}
