package application.rest;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;


@ApplicationPath("/rest")
@Singleton
@Startup
public class JaxrsApplication extends Application {
	
	@PostConstruct
    public void init() {
		InventoryRefreshTask inv = new InventoryRefreshTask();
        inv.start();
    }

}
