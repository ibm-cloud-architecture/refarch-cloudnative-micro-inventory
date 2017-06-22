package catalog.client;

import java.util.List;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name="inventory-service", url="${inventoryService.url}")
public interface InventoryServiceClient {
	@RequestMapping(method=RequestMethod.GET, value="/micro/inventory", produces={MediaType.APPLICATION_JSON_VALUE})
	List<Item> getAllItems();

	@RequestMapping(method=RequestMethod.GET, value="/micro/check")
	void healthCheck();

}
