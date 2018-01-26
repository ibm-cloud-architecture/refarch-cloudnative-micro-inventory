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

	  public List<Item> getAllItems()
	  {
		List<Item> allItems = new ArrayList<>();
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target("http://inventory:9080/inventory/rest/inv/inventory/");
		String s = target.request().get(String.class);

		Gson gson = new Gson();
		Item items[] = gson.fromJson(s, Item[].class);
		System.out.println("Generating items array"+items.toString());

		allItems = Arrays.asList(items);
		System.out.println("Generated allItems"+allItems);

		return allItems;
	  }

	  public String healthCheck(){
		  Client client = ClientBuilder.newClient();
		  WebTarget target = client.target("http://inventory:9080/inventory/rest/inv/check/");
		  String s = target.request().get(String.class);
		  return s;
	  }

}
