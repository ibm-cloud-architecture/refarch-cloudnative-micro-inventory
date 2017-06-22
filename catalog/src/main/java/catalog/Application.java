package catalog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.feign.EnableFeignClients;


@SpringBootApplication
@EnableCircuitBreaker
@EnableFeignClients
public class Application {
	@Autowired
	private InventoryRefreshTask refreshTask;
	
    public static void main(String[] args) {
        final ApplicationContext ctx = SpringApplication.run(Application.class, args);
        
        System.out.println("Catalog microservice is ready for business...");
    }
    
    @Bean
	public TaskExecutor taskExecutor() {
    	return new SimpleAsyncTaskExecutor();
	}
    
    @Bean
    public CommandLineRunner schedulingRunner(TaskExecutor executor) {
    	
    	return new CommandLineRunner() {
			
			@Override
			public void run(String... args) throws Exception {
				executor.execute(refreshTask);
				
			}
		};
    	
    }
}
