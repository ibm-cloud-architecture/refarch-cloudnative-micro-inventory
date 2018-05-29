package client;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.enterprise.context.Dependent;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Dependent
@RegisterRestClient
public interface InventoryServiceClient {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<Item> getAllItems();

}
