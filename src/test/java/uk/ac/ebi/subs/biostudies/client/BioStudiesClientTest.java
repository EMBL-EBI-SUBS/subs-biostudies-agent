package uk.ac.ebi.subs.biostudies.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.subs.biostudies.BioStudiesApiDependentTest;
import uk.ac.ebi.subs.biostudies.TestUtil;
import uk.ac.ebi.subs.biostudies.model.BioStudiesSubmission;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {
        BioStudiesConfig.class,
        BioStudiesClientTestContextConfiguration.class
})
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

        assertEquals("OK", session.getBioStudiesLoginResponse().getStatus());
        assertNotNull(session.getBioStudiesLoginResponse().getSessid());
        System.out.println(session);
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

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
        assertNotNull(session);
    }

    @Test
    public void createGood() throws JsonProcessingException {
        BioStudiesClient client = new BioStudiesClient(config);
        BioStudiesSession session = client.initialiseSession();

        SubmissionReport response = session.create(bioStudiesSubmission);

        assertEquals("OK", response.getStatus());
        assertNotNull(response.findAccession());
        assertTrue(response.findAccession().startsWith("SUBSPRJ"));

        System.out.println(response.findAccession());

    }

    @Test
    public void updateGood() {
        BioStudiesClient client = new BioStudiesClient(config);
        BioStudiesSession session = client.initialiseSession();

        bioStudiesSubmission.setAccno("SUBSPRJ1");

        SubmissionReport response = session.update(bioStudiesSubmission);
        System.out.println(response);

        assertEquals("OK", response.getStatus());
        assertNotNull(response.findAccession());
        assertEquals("SUBSPRJ1",response.findAccession());

        System.out.println(response.findAccession());
    }

}
