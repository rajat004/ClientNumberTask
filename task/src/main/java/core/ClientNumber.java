package core;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import constants.ClientNumberConstants;

@Entity
@Table(name = "number")
@NamedQueries({ @NamedQuery(name = "core.ClientNumber.findAll", query = "select cn from ClientNumber cn"),
		@NamedQuery(name = "core.ClientNumber.findNextAvailNumber", query = "select cn from ClientNumber cn "
				+ "where cn.active=0"),
		/*
		 * @NamedQuery(name="core.ClientNumber.createNewInactiveNumbersInDb",
		 * query="select cn from ClientNumber cn " + "where cn.active=0"),
		 * /* @NamedQuery(name="core.ClientNumber.createNewInactiveNumbersInDb",
		 * query="insert into ClientNumber (id) " +
		 * "Select (cn.id + 1) as cn.id  from ClientNumber cn where not exists"
		 * + "(select cn2 from ClientNumber cn2 where cn2.id = cn1.id +1)")
		 */
		@NamedQuery(name = "core.ClientNumber.createNewInactiveNumbersInDb", query = "select l.id + 1 as id "
				+ "from ClientNumber  l left outer join ClientNumber " + " r on l.id + 1 = r.id where r.id is null")

})
public class ClientNumber implements Serializable {

	@Id
	private long id;

	@Column
	private int active;

	public ClientNumber(Long id) {
		this.id = id;
		this.active = ClientNumberConstants.IN_ACTIVE;
	}

	public ClientNumber() {

	}

	public int getActive() {
		return active;
	}

	public void setActive(int active) {
		this.active = active;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}
