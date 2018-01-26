package application.rest;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import client.InventoryServiceClient;
import client.Item;

class InventoryRefreshTask implements Runnable {
	
	private static final Logger logger = LoggerFactory.getLogger(InventoryRefreshTask.class);

	private static final int INVENTORY_REFRESH_SLEEP_TIME_MS = 2500;

	private InventoryServiceClient invClient = new InventoryServiceClient();

	private ElasticSearch elasticSearch = new ElasticSearch() ;
	
	@Inject
	ItemService itemsRepo;

	public void run() {
		while (true) {
			try {
				logger.debug("Querying Inventory Service for all items ...");
				final List<Item> allItems = invClient.getAllItems();
				final List<models.Item> modelItems = new ArrayList<models.Item>(allItems.size());

				for (final Item item : allItems) {
					modelItems.add(item.toModel());
				}
                
				elasticSearch.loadRows(modelItems);
			} catch (Exception e) {
				e.printStackTrace();
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
	
	/*public void getData()
	{
		try {
			//logger.debug("Querying Inventory Service for all items ...");
            System.out.println("Querying Inventory Service for all items ...");
            //System.out.println("invClient : "+invClient.toString());
			final List<Item> allItems = invClient.getAllItems();
			//System.out.println("Getting items from inventory"+allItems);
			final List<models.Item> modelItems = new ArrayList<models.Item>(allItems.size());

			for (final Item item : allItems) {
				modelItems.add(item.toModel());
			}
            //System.out.println("loading rows"+modelItems);
			elasticSearch.loadRows(modelItems);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Caught exception, ignoring" + e);
			logger.warn("Caught exception, ignoring", e);
		}*/
		
	}

