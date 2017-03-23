package db;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.WebApplicationException;

import org.hibernate.SessionFactory;

import com.google.common.base.Optional;

import antlr.collections.List;
import constants.ClientNumberConstants;
import core.ClientNumber;
import io.dropwizard.hibernate.AbstractDAO;

@SuppressWarnings("unused")
public class ClientNumberDao extends AbstractDAO<ClientNumber> {

	public ClientNumberDao(SessionFactory sessionFactory) {
		super(sessionFactory);

	}

	public ClientNumber findById(long id) {
		return get(id);
	}

	public ClientNumber findNextAvailNumber() {
		java.util.List<ClientNumber> listClientNumber = list(
				namedQuery("core.ClientNumber.findNextAvailNumber").setMaxResults(1));
		return listClientNumber.get(0);
	}

	public ClientNumber updateStatusOfNumber(ClientNumber clientNumber) {
		clientNumber.setActive(ClientNumberConstants.ACTIVE);
		return persist(clientNumber);
	}

	public ClientNumber createNumberWithId(long id) {
		if (id > ClientNumberConstants.MAX_DEFINED_NUMBER || id < ClientNumberConstants.MIN_DEFINED_NUMBER)
			throw new WebApplicationException("Please select a valid range between 1111111111-9999999999");
		ClientNumber clientNumber = new ClientNumber();
		clientNumber.setId(id);
		clientNumber.setActive(ClientNumberConstants.ACTIVE);
		return persist(clientNumber);
	}

	public ClientNumber createAndGetNextAvailNumber() {
		ClientNumber clientNumber = this.findNextAvailNumber();
		if (clientNumber == null) {
			this.createNewInactiveNumbersInDb();
			clientNumber = this.findNextAvailNumber();
		}
		if (clientNumber.getId() > ClientNumberConstants.MAX_DEFINED_NUMBER) {
			throw new BadRequestException("Sorry , any number is not available");
		}
		// System.out.println(clientNumber.getId() + clientNumber.getActive() +
		// "found");
		clientNumber = this.updateStatusOfNumber(clientNumber);
		if (clientNumber == null) {
			throw new WebApplicationException("Please try again later");
		}
		return clientNumber;
	}

	private void createNewInactiveNumbersInDb() {
		namedQuery("core.ClientNumber.createNewInactiveNumbersInDb")
				.setMaxResults(ClientNumberConstants.MAX_NEW_NUMBERS_CREATION_LIMIT);

	}

}
