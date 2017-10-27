package uk.ac.ebi.subs.biostudies.client;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.subs.BioStudiesAgentApp;
import uk.ac.ebi.subs.BioStudiesApiDependentTest;
import uk.ac.ebi.subs.biostudies.TestUtil;
import uk.ac.ebi.subs.biostudies.model.BioStudiesSubmission;

import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = BioStudiesAgentApp.class)
@Category(BioStudiesApiDependentTest.class)
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


    @Rule public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void loginFailure() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("login failed, check username and password");

        BioStudiesConfig badConfig = new BioStudiesConfig();
        badConfig.setServer(config.getServer());

        badConfig.getAuth().setLogin(UUID.randomUUID().toString());
        badConfig.getAuth().setPassword(UUID.randomUUID().toString());

        BioStudiesClient client = new BioStudiesClient(badConfig);
        BioStudiesSession session = client.initialiseSession();

        //don't expect to get here
        Assert.assertNotNull(session);
    }

    @Test
    public void createGood() {
        BioStudiesClient client = new BioStudiesClient(config);
        BioStudiesSession session = client.initialiseSession();

        SubmissionReport response = session.create(bioStudiesSubmission);

        Assert.assertEquals("OK", response.getStatus());
        Assert.assertNotNull(response.findAccession());
        Assert.assertTrue(response.findAccession().startsWith("SUBSPRJ"));

        System.out.println(response.findAccession());

    }

    @Test
    public void updateGood() {
        BioStudiesClient client = new BioStudiesClient(config);
        BioStudiesSession session = client.initialiseSession();

        bioStudiesSubmission.setAccno("SUBSPRJ1");

        SubmissionReport response = session.update(bioStudiesSubmission);

        Assert.assertEquals("OK", response.getStatus());
        Assert.assertNotNull(response.findAccession());
        Assert.assertEquals("SUBSPRJ1",response.findAccession());

        System.out.println(response.findAccession());
    }

}
