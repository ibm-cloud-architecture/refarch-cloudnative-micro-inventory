package application.rest;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import utils.InventoryDAOImpl;


@Path("/inv")
public class InventoryService {
	
	private final static String QUEUE_NAME = "hello";
	
	@GET
	 @Path("/check")
	 @Produces("application/json")
	 public String check() throws IOException, TimeoutException {
		
		ConnectionFactory factory = new ConnectionFactory();
	    factory.setHost("localhost");
	    Connection connection = factory.newConnection();
	    Channel channel = connection.createChannel();

	    channel.queueDeclare(QUEUE_NAME, false, false, false, null);
	    System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
	    
	    DefaultConsumer consumer = new DefaultConsumer(channel) {
	        @Override
	        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
	            throws IOException {
	          String message = new String(body, "UTF-8");
	          System.out.println(" [x] Received '" + message + "'");
	        }
	      };
	      channel.basicConsume(QUEUE_NAME, true, consumer);
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



