package uk.ac.ebi.subs.biostudies.client;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.subs.biostudies.BioStudiesAgentApp;
import uk.ac.ebi.subs.biostudies.TestUtil;
import uk.ac.ebi.subs.biostudies.model.BioStudiesSubmission;

import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = BioStudiesAgentApp.class)
public class BioStudiesClientTest {

    @Autowired
    private BioStudiesConfig config;
    private BioStudiesSubmission bioStudiesSubmission;

    @Before
    public void buildup() {

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
        BioStudiesConfig badConfig = new BioStudiesConfig();
        badConfig.setServer(config.getServer());

        badConfig.getAuth().setLogin(UUID.randomUUID().toString());
        badConfig.getAuth().setPassword(UUID.randomUUID().toString());

        BioStudiesClient client = new BioStudiesClient(badConfig);
        BioStudiesSession session = client.initialiseSession();
    }

    @Test
    public void submitGood() {
        BioStudiesClient client = new BioStudiesClient(config);
        BioStudiesSession session = client.initialiseSession();

        SubmissionReport response = session.submit(bioStudiesSubmission);

        Assert.assertEquals("OK", response.getStatus());
        Assert.assertNotNull(response.findAccession());
        Assert.assertTrue(response.findAccession().startsWith("SUBSPRJ"));

        System.out.println(response.findAccession());

    }

}
