package application.rest;

import javax.enterprise.context.ApplicationScoped;


import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

@Health
@ApplicationScoped

public class HealthEndpoint implements HealthCheck {

	public boolean isInventoryDbReady(){
		//Check if db is available
		return true;
	}
	
	public boolean isRabbitMQReady() {
	    // check if rabbit is available?
		return true;
	  }
	
	@Override
	public HealthCheckResponse call() {
		// TODO Auto-generated method stub
		
		if (!isInventoryDbReady()) {
		      return HealthCheckResponse.named(InventoryService.class.getSimpleName())
		                                .withData("Inventory Database", "DOWN").down()
		                                .build();
		    }
		
		if (!isRabbitMQReady()) {
		      return HealthCheckResponse.named(InventoryService.class.getSimpleName())
		                                .withData("RabbitMQ", "DOWN").down()
		                                .build();
		    }
		return HealthCheckResponse.named(InventoryService.class.getSimpleName()).up().build();
	}

}
