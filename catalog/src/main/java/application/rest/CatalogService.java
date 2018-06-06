package application.rest;

import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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

    @Timeout(value = 2, unit = ChronoUnit.SECONDS)
    @Retry(maxRetries = 2, maxDuration= 2000)
    @Fallback(fallbackMethod = "fallbackInventory")
    @GET
    public List<Item> getInventory() {
    	List<Item> items = null;
    	try {
            Thread.sleep(10000);
            items = itemsRepo.findAll();
            return items;
        } catch (InterruptedException e) {
            System.out.println("serviceA interrupted");
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
