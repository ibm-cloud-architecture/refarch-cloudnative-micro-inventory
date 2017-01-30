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

        String connection = System.getenv("es_connection_string");
        String vcap = System.getenv("VCAP_SERVICES");

        if (connection == null || connection.equals("")) {
            System.out.println("Seems we don't have a connection string for Elasticsearch!");
            System.out.println("Please provide es_connection_string environment variable. i.e. http(s)://ip:9200");
            System.out.println("You can also run 'source load_config.sh' to run locally");
            System.exit(-1);
        }

        if (vcap == null || vcap.equals("")) {
            System.out.println("Seems we don't have VCAP_SERVICES to Message Hub!");
            System.out.println("Please bind Message Hub instance or run 'source load_config.sh' to run locally");
            System.exit(-1);
        }

        // Create Message HUB consumer and subscribe to topic, which initiates the polling
        MHConsumer consumer = new MHConsumer();
        consumer.subscribe();
    }
}
