package application.rest;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.media.Content;


@RequestScoped
@Path("/items")
@Produces(MediaType.APPLICATION_JSON)
@OpenAPIDefinition(
		info = @Info(
				title = "Catalog Service", 
				version = "0.0", 
				description = "Catalog APIs",
				contact = @Contact(url = "https://github.com/ibm-cloud-architecture", name = "IBM CASE"),
				license = @License(name = "License", url = "https://github.com/ibm-cloud-architecture/refarch-cloudnative-micro-inventory/blob/microprofile/catalog/LICENSE")
				)
		)
public class CatalogService {

    @Inject
    ItemService itemsRepo; 
    
    List<Item> list = null;
    
    //private Config config = ConfigProvider.getConfig();

    //private boolean ft_enabled = config.getValue("fault_tolerance_enabled", Boolean.class);

    @Timeout(value = 2, unit = ChronoUnit.SECONDS)
    @Retry(maxRetries = 2, maxDuration= 2000)
    @Fallback(fallbackMethod = "fallbackInventory")
    @GET
    @APIResponses(value = {
            @APIResponse( 
            		responseCode = "404", 
            		description = "Items Not Found", 
            		content = @Content( 
            				mediaType = "text/plain"
            				)
            		),
            @APIResponse( 
            		responseCode = "500", 
            		description = "Internal Server Error", 
            		content = @Content( 
            				mediaType = "text/plain"
            				)
            		),
            @APIResponse( 
            		responseCode = "200",
            		description = "List of items from the catalog", 
            		content = @Content( 
            				mediaType = "application/json", 
            				schema = @Schema(implementation = Item.class)
            				)
            		)
            }
    )
    @Operation( 
    		summary = "Get Inventory Items", 
    		description = "Retriving all the available items from the cache"
    		)
    public List<Item> getInventory() {
    	List<Item> items = null;
    	items = itemsRepo.findAll();
		return items;
    }

    public List<Item> fallbackInventory() {
    	
    	//Returns a default fallback list
    	List<Item> list = new ArrayList<Item>();
        Item item = new Item("Standard fallback message","Standard fallback message",0,"Standard fallback message","Catalog-fallback.jpg",0);
        list.add(item);

        return list;
    	
        //Returns emptylist
        //return Collections.emptyList();
    }

    @GET
    @Path("{id}")
    @APIResponses(value = {
            @APIResponse( 
            		responseCode = "404",
            		description = "Item Not Found", 
            		content = @Content( mediaType = "text/plain")
            		),
            @APIResponse(
            		responseCode = "500",
            		description = "Internal Server Error",
            		content = @Content( mediaType = "text/plain")
            		),
            @APIResponse( 
            		responseCode = "200", 
            		description = "Item retrieved by id", 
            		content = @Content( 
            				mediaType = "application/json", 
            				schema = @Schema(implementation = Item.class)
            				)
            		)
            }
    )
    @Operation(
    		summary = "Get Inventory Items by Id", 
    		description = "Retrieving the item from cache based on id"
    		)
    public Response getById(@Parameter(description = "The id of the item that needs to be fetched. For testing, use 13401", required = true) @PathParam("id") long id) {
        final Item item = itemsRepo.findById(id);
        if (item == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(item, MediaType.APPLICATION_JSON).build();
    }

}
