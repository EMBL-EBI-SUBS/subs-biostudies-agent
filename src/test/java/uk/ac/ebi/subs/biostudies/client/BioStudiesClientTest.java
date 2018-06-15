package uk.ac.ebi.subs.biostudies.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.subs.biostudies.BioStudiesApiDependentTest;
import uk.ac.ebi.subs.biostudies.TestUtil;
import uk.ac.ebi.subs.biostudies.model.BioStudiesSubmission;
import uk.ac.ebi.subs.biostudies.model.DataOwner;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
        BioStudiesConfig.class,
        BioStudiesClient.class,
        BioStudiesClientTestContextConfiguration.class
})
@Category(BioStudiesApiDependentTest.class)
public class BioStudiesClientTest {

    @Autowired
    private BioStudiesConfig config;

    @Test
    public void login() {
        BioStudiesClient client = new BioStudiesClient(config);
        BioStudiesSession session = client.getBioStudiesSession();

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
        client.getBioStudiesSession();
    }



}
