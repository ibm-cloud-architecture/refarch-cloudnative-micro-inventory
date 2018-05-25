package client;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.enterprise.context.Dependent;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Dependent
@RegisterRestClient
public interface InventoryServiceClient {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/inventory")
    List<Item> getAllItems();

    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Path("/check")
    String healthCheck();

}
