package application.rest;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;

import utils.JDBCConnection;

@Health
@ApplicationScoped

public class HealthEndpoint implements HealthCheck {

    private final static String QUEUE_NAME = "health";
    String health = "health_check";

    public boolean isInventoryDbReady() {

        //Checking if the Inventory database is UP

        JDBCConnection jdbcConnection = new JDBCConnection();

        java.sql.Connection connection = jdbcConnection.getConnection();

        if (connection != null)
            return true;
        else
            return false;
    }

    public boolean isRabbitMQReady() throws IOException, TimeoutException {

        //Checking if RabbitMQ is UP

        boolean msgStatus = sendMessage();

        if (msgStatus == true) {
            return true;
        }
        return false;

    }

    public boolean sendMessage() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            Config config = ConfigProvider.getConfig();
            String rabbit_host = config.getValue("rabbit", String.class);
            String sentMsg = null;
            factory.setHost(rabbit_host);
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(QUEUE_NAME, false, false, false, null);

            channel.basicPublish("", QUEUE_NAME, null, health.getBytes());
            System.out.println("Sent the message '" + health + "'");

            boolean autoAck = true;
            GetResponse response = channel.basicGet(QUEUE_NAME, autoAck);
            if (response == null) {
                // There are no messages to retrieve
            } else {
                byte[] body = response.getBody();
                sentMsg = new String(body);
            }

            if (sentMsg.equals(health)) {
                return true;
            }

            channel.close();
            connection.close();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (TimeoutException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public HealthCheckResponse call() {
        // TODO Auto-generated method stub

        if (!isInventoryDbReady()) {
            return HealthCheckResponse.named(InventoryService.class.getSimpleName())
                    .withData("Inventory Database", "DOWN").down()
                    .build();
        }

        try {
            if (!isRabbitMQReady()) {
                return HealthCheckResponse.named(InventoryService.class.getSimpleName())
                        .withData("RabbitMQ", "DOWN").down()
                        .build();
            }
        } catch (IOException | TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return HealthCheckResponse.named(InventoryService.class.getSimpleName()).withData("Inventory Service", "UP").up().build();
    }

}
