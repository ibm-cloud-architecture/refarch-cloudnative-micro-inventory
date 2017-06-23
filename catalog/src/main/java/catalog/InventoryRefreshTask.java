package catalog;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import catalog.client.InventoryServiceClient;
import catalog.client.Item;

@Component
public class InventoryRefreshTask  implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(InventoryRefreshTask.class);
	
	private static final int INVENTORY_REFRESH_SLEEP_TIME_MS = 2500;
		
	@Autowired
	private InventoryServiceClient invClient;
		
	@Autowired
	private ElasticSearch elasticSearch;
		
	public void run() {
		while (true) {
			try {
				logger.debug("Querying Inventory Service for all items ...");
				final List<Item> allItems = invClient.getAllItems();
				
				final List<catalog.models.Item> modelItems = new ArrayList<catalog.models.Item>(allItems.size());
				
				for (final Item item : allItems) {
					modelItems.add(item.toModel());
				}
				
				elasticSearch.loadRows(modelItems);
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