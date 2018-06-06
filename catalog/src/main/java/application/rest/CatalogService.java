package application.rest;

import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;

import models.Item;
import org.eclipse.microprofile.health.HealthCheckResponse;

@RequestScoped
@Path("/items")
@Produces(MediaType.APPLICATION_JSON)
public class CatalogService {

    @Inject
    ItemService itemsRepo; 
    
    private Config config = ConfigProvider.getConfig();

    private boolean ft_enabled = config.getValue("fault_tolerance_enabled", Boolean.class);

    @Timeout(value = 2, unit = ChronoUnit.SECONDS)
    @Retry(maxRetries = 2, maxDuration= 2000)
    @Fallback(fallbackMethod = "fallbackInventory")
    @GET
    public List<Item> getInventory() {
    	List<Item> items = null;
    	if(ft_enabled){
    	try {
            Thread.sleep(10000);
            items = itemsRepo.findAll();
            return items;
        } catch (InterruptedException e) {
            System.out.println("serviceA interrupted");
        }
    	}
    	else{
    		items = itemsRepo.findAll();
            return items;
    	}
		return items;
    }

    public List<Item> fallbackInventory() {
    	return Collections.emptyList();
    }

    @GET
    @Path("{id}")
    public Response getById(@PathParam("id") long id) {
        final Item item = itemsRepo.findById(id);
        if (item == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(item, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("name/{name}")
    public List<Item> getByName(@PathParam("name") String name) {
        return itemsRepo.findByNameContaining(name);
    }

    public HealthCheckResponse call() {
        return HealthCheckResponse.named(CatalogService.class.getSimpleName()).up().build();
    }

}
