package app;

import config.ClientNumberConfiguration;
import core.ClientNumber;
import db.ClientNumberDao;
import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import resource.ClientNumberResource;



public class ClientNumberApplication extends Application<ClientNumberConfiguration> {

	public static void main(String[] args) throws Exception {
		new ClientNumberApplication().run(args);
	}

	@Override
	public String getName() {
		return "hello-world";
	}

	private final HibernateBundle<ClientNumberConfiguration> hibernateBundle = new HibernateBundle<ClientNumberConfiguration>(
			ClientNumber.class) {
		@Override
		public DataSourceFactory getDataSourceFactory(ClientNumberConfiguration clientNumberConfiguration) {
			return clientNumberConfiguration.getDataSourceFactory();
		}
	};

	@Override
	public void initialize(final Bootstrap<ClientNumberConfiguration> bootstrap) {
		bootstrap.addBundle(hibernateBundle);
	}

	@Override
	public void run(ClientNumberConfiguration configuration, Environment environment) {
		//
		final ClientNumberDao clientNumberDao = new ClientNumberDao(hibernateBundle.getSessionFactory());
		final ClientNumberResource resource = new ClientNumberResource(clientNumberDao);
		// final ClientNumberHealthCheck healthCheck = new
		// ClientNumberHealthCheck(configuration.getTemplate());
		// environment.healthChecks().register("template", healthCheck);
		environment.jersey().register(resource);
	}

}
