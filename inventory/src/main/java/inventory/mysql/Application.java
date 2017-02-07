package inventory.mysql;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;


@SpringBootApplication
@EnableCircuitBreaker
public class Application {

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(Application.class, args);
        System.out.println("Inventory microservice is ready for business...");

        Config config = new Config();

        String connection = config.es_url;
        String message_hub_api_key = config.mh_api_key;

        if (connection == null || connection.equals("")) {
            System.out.println("Seems we don't have a connection string for Elasticsearch!");
            System.out.println("Please provide Elasticsearch connection string in src/main/resources/application.yml OR");
            System.out.println("Provide a value for environment variable \"elasticsearch_connection_string\"");
            System.exit(-1);
        }

        if (message_hub_api_key == null || message_hub_api_key.equals("")) {
            System.out.println("Seems we don't have Message Hub Credentials!");
            System.out.println("Please provide Message Hub parameters in src/main/resources/application.yml");
            System.exit(-1);
        }

        // Create Message HUB consumer and subscribe to topic, which initiates the polling
        MHConsumer consumer = new MHConsumer();
        consumer.subscribe();
    }
}
