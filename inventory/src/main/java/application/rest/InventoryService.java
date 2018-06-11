package application.rest;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

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

    private final static String QUEUE_NAME = "stock";

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

    // Order service uses this API to update stock
    @GET
    @Path("/stock")
    @Produces("application/json")
    public String stock() throws IOException, TimeoutException {
        consumer();
        return "Stock updated";
    }

    public void consumer() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        Config config = ConfigProvider.getConfig();
        String rabbit_host = config.getValue("rabbit", String.class);
        factory.setHost(rabbit_host);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println(" Waiting ... Waiting ... Waiting for the messages");
        System.out.println(". To exit press CTRL+C");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Received the message '" + message + "'");
                String[] splited = message.split(" ");

                InventoryDAOImpl inv = new InventoryDAOImpl();

                long id = Long.parseLong(splited[0]);
                int stock = Integer.parseInt(splited[1]);

                inv.updateStock(stock, id);

            }
        };
        channel.basicConsume(QUEUE_NAME, true, consumer);
    }

}
