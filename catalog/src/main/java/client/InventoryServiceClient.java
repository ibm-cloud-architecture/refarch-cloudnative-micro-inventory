package client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.json.JsonArray;
import javax.json.JsonValue;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

import com.google.gson.Gson;



public class InventoryServiceClient {

	  Client client = ClientBuilder.newClient();

	  Config config = ConfigProvider.getConfig();
	  
	  String inv_url = config.getValue("inventory-url", String.class);
	  String inv_health = config.getValue("inventory-health", String.class);

	  public List<Item> getAllItems()
	  {
		List<Item> allItems = new ArrayList<>();
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(inv_url);
		String s = target.request().get(String.class);

		Gson gson = new Gson();
		Item items[] = gson.fromJson(s, Item[].class);

		allItems = Arrays.asList(items);

		return allItems;
	  }

	  public String healthCheck(){
		  Client client = ClientBuilder.newClient();
		  WebTarget target = client.target(inv_health);
		  String s = target.request().get(String.class);
		  return s;
	  }

}
