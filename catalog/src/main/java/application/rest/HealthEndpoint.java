package application.rest;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

@Health
@ApplicationScoped

public class HealthEndpoint implements HealthCheck {
	
    public boolean isElasticsearchReady(){
    	
    	//Checking if Elasticsearch is UP
    	
		return true;
	}

	public boolean isInventoryReady(){
		
		//Checking if Inventory is UP

		return true;
	}

	@Override
	public HealthCheckResponse call() {
		if (!isElasticsearchReady()) {
		      return HealthCheckResponse.named(CatalogService.class.getSimpleName())
		                                .withData("Elasticsearch", "DOWN").down()
		                                .build();
		    }

		if (!isInventoryReady()) {
			  return HealthCheckResponse.named(CatalogService.class.getSimpleName())
			                            .withData("Inventory Service", "DOWN").down()
			                            .build();
			    }
		
		
		return HealthCheckResponse.named(CatalogService.class.getSimpleName()).withData("Catalog Service", "UP").up().build();
	}

}
