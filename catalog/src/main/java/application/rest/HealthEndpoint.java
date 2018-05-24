package application.rest;

import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Health
@ApplicationScoped

public class HealthEndpoint implements HealthCheck {
	
	private Config config = ConfigProvider.getConfig();
	
	private String es_url = config.getValue("elasticsearch_url", String.class);
	private String inv_url = config.getValue("inventory_health", String.class);
	
	
	private OkHttpClient client = new OkHttpClient();
	
    public boolean isElasticsearchReady(){
    	
    	//Checking if Elasticsearch is UP
    	
		Request.Builder builder = new Request.Builder().url(es_url);
		Request request = builder.build();
		
		try {
			Response response = client.newCall(request).execute();
			boolean status = response.isSuccessful();
			response.close();
			if(status)
				return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
		return false;
	}

	public boolean isInventoryReady(){
		
		//Checking if Inventory is UP
		
		Request.Builder builder = new Request.Builder().url(inv_url);
		Request request = builder.build();
		
		try {
			Response response = client.newCall(request).execute();
			boolean status = response.isSuccessful();
			response.close();
			if(status)
				return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
		return false;
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
