package test;

import static org.mockito.Mockito.mock;

import java.io.IOException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import app.ClientNumberApplication;
import config.ClientNumberConfiguration;
import constants.ClientNumberConstants;
import core.ClientNumber;
import db.ClientNumberDao;
import io.dropwizard.testing.junit.DropwizardAppRule;
import io.dropwizard.testing.junit.ResourceTestRule;
import junit.framework.TestCase;
import resource.ClientNumberResource;

public class ClientNumberResourceTest extends TestCase {

	@ClassRule
	public static final DropwizardAppRule<ClientNumberConfiguration> RULE = new DropwizardAppRule<>(
			ClientNumberApplication.class, "config.yaml");

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		ClientNumberResourceTest.RULE.getEnvironment().getApplicationContext().stop();
		final ClientNumberDao dao1 = mock(ClientNumberDao.class);
		ClientNumberResourceTest.RULE.getEnvironment().jersey().register(new ClientNumberResource(dao1));
		ClientNumberResourceTest.RULE.getEnvironment().getApplicationContext().start();
		System.out.println("abcd");
	}

	private static final ClientNumberDao dao = mock(ClientNumberDao.class);
	@Rule
	public ResourceTestRule resources = ResourceTestRule.builder().addResource(new ClientNumberResource(dao)).build();

	@Test
	public void testClientNumberResource() throws JsonParseException, JsonMappingException, IOException {

		Client client = resources.client();
		WebTarget helloTarget = client.target("http://localhost:8080/number");
		Invocation.Builder builder = helloTarget.request(MediaType.APPLICATION_JSON);

		Response response = builder.get();
		String expected = "IN RANGE AND ACTIVE";
		assertEquals(Response.Status.OK, response.getStatusInfo());
		String actual = response.readEntity(String.class);
		ObjectMapper mapper = new ObjectMapper();
		ClientNumber cn = mapper.readValue(actual, ClientNumber.class);
		String actual1 = "NOT IN RANGE OR INACTIVE";
		Long id = cn.getId();
		if (id >= ClientNumberConstants.MIN_DEFINED_NUMBER && id <= ClientNumberConstants.MAX_DEFINED_NUMBER
				&& cn.getActive() == ClientNumberConstants.ACTIVE) {
			actual1 = "IN RANGE AND ACTIVE";
		}
		assertEquals(expected, actual1);

		// fail("Not yet implemented");
	}

}
