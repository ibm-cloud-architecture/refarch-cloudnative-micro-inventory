package catalog;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

import catalog.models.Item;
import catalog.models.ItemService;

@Path("items")
@Produces(MediaType.APPLICATION_JSON)
@Health
@ApplicationScoped
public class CatalogService implements HealthCheck {

	@Inject
	ItemService itemsRepo;

    /**
     * @return all items in inventory
     */
	@GET
    public List<Item> getInventory() {
        return itemsRepo.findAll();
    }

    /**
     * @return item by id
     */
	@GET
	@Path("{id}")
    public Response getById(@PathParam("id") long id) {
        final Item item = itemsRepo.findById(id);
        if (item == null) {
        		return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(item, MediaType.APPLICATION_JSON).build();
    }

    /**
     * @return item(s) containing name
     */
	@GET
	@Path("name/{name}")
    public List<Item> getByName(@PathParam("name") String name) {
        return itemsRepo.findByNameContaining(name);
    }

    @Override
    public HealthCheckResponse call() {
	    return HealthCheckResponse.named(CatalogService.class.getSimpleName()).up().build();
	}

}
