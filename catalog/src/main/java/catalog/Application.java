package catalog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private static final Logger logger = LoggerFactory.getLogger(ElasticSearch.class);
	
	@Autowired
	private InventoryRefreshTask refreshTask;
	
    public static void main(String[] args) {
        final ApplicationContext ctx = SpringApplication.run(Application.class, args);
        
        logger.info("Catalog microservice is ready for business...");
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
				logger.info("Starting Inventory Refresh background task ...");
				executor.execute(refreshTask);
				
			}
		};
    	
    }
}
