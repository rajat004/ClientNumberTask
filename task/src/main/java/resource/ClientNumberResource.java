package resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;

import constants.ClientNumberConstants;
import core.ClientNumber;
import db.ClientNumberDao;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.LongParam;

@Path("/number")
@Produces(value = "application/json")
public class ClientNumberResource {

	private ClientNumberDao clientNumberDao;

	public ClientNumberResource(ClientNumberDao clientNumberDao) {
		// TODO Auto-generated constructor stub
		this.clientNumberDao = clientNumberDao;
	}

	@GET
	@UnitOfWork
	public ClientNumber getNewClientNumber() {
		return clientNumberDao.createAndGetNextAvailNumber();
	}

	@GET
	@Path("/{id}")
	@UnitOfWork
	public ClientNumber findById(@PathParam("id") LongParam id) {
		if (id.get() > ClientNumberConstants.MAX_DEFINED_NUMBER
				|| id.get() < ClientNumberConstants.MIN_DEFINED_NUMBER) {
			throw new WebApplicationException("Please select a valid range between 1111111111-9999999999");
		}
		ClientNumber clientNumber = clientNumberDao.findById(id.get());
		if (clientNumber == null) {
			ClientNumber newClientNumber = clientNumberDao.createNumberWithId(id.get());
			if (newClientNumber == null)
				throw new WebApplicationException("Something went wrong,Please try again later");
			return newClientNumber;
		}
		if (clientNumber.getActive() == 0) {
			ClientNumber cn = clientNumberDao.updateStatusOfNumber(clientNumber);
			if (cn == null)
				throw new WebApplicationException("Please try again later");
			return cn;
		}
		return clientNumberDao.createAndGetNextAvailNumber();
	}

}
