package catalog;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import catalog.client.InventoryServiceClient;
import catalog.client.Item;
import catalog.models.CatalogItemRepository;

@Component
public class InventoryRefreshTask  implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(InventoryRefreshTask.class);
	
	private static final int INVENTORY_REFRESH_SLEEP_TIME_MS = 2500;
		
	@Autowired
	private InventoryServiceClient invClient;
		
	@Autowired
	private CatalogItemRepository catalogRepo;
		
	public void run() {
		while (true) {
			try {
				logger.debug("Querying Inventory Service for all items ...");
				final List<Item> allItems = invClient.getAllItems();
				
				for (final Item item : allItems) {
					catalogRepo.save(item.toModel());
				}
			} catch (Exception e) {
				logger.warn("Caught exception, ignoring", e);
			}
			try {
				Thread.sleep(INVENTORY_REFRESH_SLEEP_TIME_MS);
			} catch (InterruptedException e) {
				logger.warn("Caught InterruptedException, quitting");
				break;
			}
		}
	}
}