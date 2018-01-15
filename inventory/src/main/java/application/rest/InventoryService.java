package application.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.google.gson.Gson;

import utils.InventoryDAOImpl;


@Path("/inv")
public class InventoryService {
	@GET
	 @Path("/check")
	 @Produces("application/json")
	 public String check() {
	 return "it works!";
	}
	
	@GET
	 @Path("/inventory")
	 @Produces("application/json")
	 public String getInvDetails() {
	 
	 String invDetails = null;
	 List invlist = null;
	 InventoryDAOImpl inv = new InventoryDAOImpl();

	 invlist = inv.getInventoryDetails();

	 Gson gson = new Gson();
	 invDetails = gson.toJson(invlist);
	 return invDetails;
	}
	
	// find all by naming like /inventory/name/{name}
	@GET
	 @Path("inventory/name/{name}")
	 @Produces("application/json")
	public String findByNameContaining(@PathParam("name") String name) {
		 
		 String invDetails = null;
		 List invlist = null;
		 InventoryDAOImpl inv = new InventoryDAOImpl();

		 invlist = inv.findByNameContaining(name);

		 Gson gson = new Gson();
		 invDetails = gson.toJson(invlist);
		 return invDetails;
		}
	
	// find all whose price is less than or equal to /inventory/price/{price}
	@GET
	 @Path("inventory/price/{price}")
	 @Produces("application/json")
	public String findByPriceLessThanEqual(@PathParam("price") double price) {
		 //System.out.println(price);
		 String invDetails = null;
		 List invlist = null;
		 InventoryDAOImpl inv = new InventoryDAOImpl();

		 invlist = inv.findByPriceLessThanEqual(price);

		 Gson gson = new Gson();
		 invDetails = gson.toJson(invlist);
		 return invDetails;
		}

}



