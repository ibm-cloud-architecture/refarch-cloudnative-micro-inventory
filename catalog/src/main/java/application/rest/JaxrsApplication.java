package application.rest;

import client.InventoryServiceClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/rest")
@Singleton
@Startup
public class JaxrsApplication extends Application {

    @Inject
    @RestClient
    private InventoryServiceClient invClient;

    @PostConstruct
    public void init() {
        InventoryRefreshTask inv = new InventoryRefreshTask(invClient);
        inv.start();
    }

}
