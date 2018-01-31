package application.rest;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.faulttolerance.Asynchronous;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import models.Item;

@Path("/items")
@Produces(MediaType.APPLICATION_JSON)
@Health
@ApplicationScoped
public class CatalogService {

	private static final Logger logger = LoggerFactory.getLogger(CatalogService.class);

	@Inject
	ItemService itemsRepo;

	//time in ms
	@Timeout(1000)
	@Retry(maxRetries = 10, maxDuration= 2000)
	@CircuitBreaker(successThreshold = 5, requestVolumeThreshold = 2, failureRatio=0.375,
	  delay = 1000)
	@Fallback(fallbackMethod="fallbackInventory")
	@GET
	public List<Item> getInventory() {
        logger.info("/items");
        new Thread(new InventoryRefreshTask()).start();
        return itemsRepo.findAll();
    }
	
	public List<Item> fallbackInventory() {
    	List<Item> items = null;
        return items;
    }

	@GET
	@Path("{id}")
	public Response getById(@PathParam("id") long id) {
    	logger.info("/items/" + id);
    	new Thread(new InventoryRefreshTask()).start();
        final Item item = itemsRepo.findById(id);
        if (item == null) {
        	return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(item, MediaType.APPLICATION_JSON).build();
    }

	@GET
	@Path("name/{name}")
    public List<Item> getByName(@PathParam("name") String name) {
    	logger.info("/items/name/" + name);
    	new Thread(new InventoryRefreshTask()).start();
        return itemsRepo.findByNameContaining(name);
    }

  
    public HealthCheckResponse call() {
	    return HealthCheckResponse.named(CatalogService.class.getSimpleName()).up().build();
	}

}
