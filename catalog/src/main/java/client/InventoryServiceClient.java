package client;

//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import javax.json.JsonArray;
//import javax.json.JsonValue;
//import javax.ws.rs.client.Client;
//import javax.ws.rs.client.ClientBuilder;
//import javax.ws.rs.client.WebTarget;
//import javax.ws.rs.core.MediaType;
//
//import org.eclipse.microprofile.config.Config;
//import org.eclipse.microprofile.config.ConfigProvider;
//
//import com.google.gson.Gson;

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

//	  Client client = ClientBuilder.newClient();
//
//	  Config config = ConfigProvider.getConfig();
//
//	  String inv_url = config.getValue("inventory_url", String.class);
//	  String inv_health = config.getValue("inventory_health", String.class);
//
//	  public List<Item> getAllItems()
//	  {
//		List<Item> allItems = new ArrayList<>();
//		Client client = ClientBuilder.newClient();
//		WebTarget target = client.target(inv_url);
//		String s = target.request().get(String.class);
//
//		Gson gson = new Gson();
//		Item items[] = gson.fromJson(s, Item[].class);
//
//		allItems = Arrays.asList(items);
//
//		return allItems;
//	  }
//
//	  public String healthCheck(){
//		  Client client = ClientBuilder.newClient();
//		  WebTarget target = client.target(inv_health);
//		  String s = target.request().get(String.class);
//		  return s;
//	  }

}
