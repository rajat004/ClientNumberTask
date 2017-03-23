package test;

//import static org.junit.Assert.*;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

//import org.junit.Rule;
//import org.junit.Test;

import ch.qos.logback.core.net.server.Client;
import io.dropwizard.testing.junit.ResourceTestRule;
import resource.ClientNumberResource;

public class TestingClientNumberApp {
/*	@Rule
	public ResourceTestRule resource = ResourceTestRule.builder().addResource(new ClientNumberResource()).build();

	@Test
	public void testGetGreeting() {
		String expected = "Hello world!";
		// Obtain client from @Rule.
		Client client = (Client) resource.client();
		// Get WebTarget from client using URI of root resource.
		WebTarget helloTarget = ((javax.ws.rs.client.Client) client).target("http://localhost:8080/hello");
		// To invoke response we use Invocation.Builder
		// and specify the media type of representation asked from resource.
		Invocation.Builder builder = helloTarget.request(MediaType.TEXT_PLAIN);
		// Obtain response.
		Response response = builder.get();
		// Do assertions.
		assertEquals(Response.Status.OK, response.getStatusInfo());
		String actual = response.readEntity(String.class);
		assertEquals(expected, actual);
	}*/
}
