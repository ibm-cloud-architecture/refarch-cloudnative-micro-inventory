package inventory;

import inventory.models.Inventory;
import inventory.models.InventoryRepo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

/**
 * REST Controller to manage Inventory database
 *
 */
@RestController("inventoryController")
public class InventoryController {
	
	Logger logger =  LoggerFactory.getLogger(InventoryController.class);

	@Autowired
	@Qualifier("inventoryRepo")
	private InventoryRepo itemsRepo;

	/**
	 * check
	 */
	@RequestMapping("/check")
	@ResponseBody String check() {
		return "it works!";
	}

	/**
	 * @return all items in inventory
	 */
	@HystrixCommand(fallbackMethod="failGood")
	@RequestMapping(value = "/inventory", method = RequestMethod.GET)
	@ResponseBody Iterable<Inventory> getInventory() {
		return itemsRepo.findAll();
	}

}
