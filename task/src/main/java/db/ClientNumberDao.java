package db;

import java.util.List;

import javax.transaction.Transaction;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.WebApplicationException;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import constants.ClientNumberConstants;
import core.ClientNumber;
import io.dropwizard.hibernate.AbstractDAO;
import static java.util.Objects.requireNonNull;

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
		return listClientNumber.isEmpty() ? null : listClientNumber.get(0);
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
			System.out.println("invoking create native query");
			this.createNewInactiveNumbersInDb();
			System.out.println("invoking find next avail num  query");
			clientNumber = this.findNextAvailNumber();
			if (clientNumber == null)
				throw new WebApplicationException("could not create new numbers");
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
		Query q = namedQuery("core.ClientNumber.createNewInactiveNumbersInDb")
				.setMaxResults(ClientNumberConstants.MAX_NEW_NUMBERS_CREATION_LIMIT);

		Session session = currentSession();
		org.hibernate.Transaction tx = session.beginTransaction();
		int count = 0;
		List<Long> ids = requireNonNull(q).list();

		// creating InactiveClientNumbers in batches of
		// 20 each time if any Inactive Number is
		// unavailable in the system

		for (Long id : ids) {
			ClientNumber clientNumber = new ClientNumber(id);
			if (id <= ClientNumberConstants.MAX_DEFINED_NUMBER) {
				session.save(clientNumber);
			}
			if (count % 20 == 0) {
				session.flush();
				session.clear();
			}
			count++;
		}

		tx.commit();
		// session.close();
	}

}
