package client;

import javax.enterprise.context.Dependent;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;


@Dependent
@RegisterRestClient
@Path("/inv")

public interface InventoryServiceClient {

    // ProcessingException is temp until further error handling is implemented

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/inventory")
    List<Item> getAllItems() throws ProcessingException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/check")
    String healthCheck() throws ProcessingException;

}
