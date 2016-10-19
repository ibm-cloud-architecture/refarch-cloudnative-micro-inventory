package inventory.mysql;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;


@SpringBootApplication
@EnableDiscoveryClient
@EnableCircuitBreaker
public class Application {

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(Application.class, args);
        System.out.println("Inventory microservice is ready for business...");
    }
}
