package application.rest;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;

import models.Item;

@Path("/items")
@Produces(MediaType.APPLICATION_JSON)
public class CatalogService {

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
        return itemsRepo.findAll();
    }
	
	public List<Item> fallbackInventory() {
    	List<Item> items = null;
        return items;
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

}
