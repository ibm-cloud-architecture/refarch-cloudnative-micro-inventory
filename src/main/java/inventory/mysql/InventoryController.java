package inventory.mysql;

import inventory.mysql.models.Inventory;
import inventory.mysql.models.IInventoryRepo;

import java.util.List;
import java.net.URI;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

/**
 * REST Controller to manage Inventory database
 *
 */
@RestController
public class InventoryController {
	
	Logger logger =  LoggerFactory.getLogger(InventoryController.class);

	@Autowired
	private IInventoryRepo itemsRepo;

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

	/**
	 * @return item by id
	 */
	@RequestMapping(value = "/inventory/{id}", method = RequestMethod.GET)
	ResponseEntity<?> getById(@PathVariable long id) {
		if (!itemsRepo.exists(id)) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok(itemsRepo.findOne(id));
	}

	/**
	 * @return item(s) containing name
	 */
	@RequestMapping(value = "/inventory/name/{name}", method = RequestMethod.GET)
	@ResponseBody List<Inventory> getByName(@PathVariable String name) {
			return itemsRepo.findByNameContaining(name);
	}

	/**
	 * @return item(s) by price lte
	 */
	@RequestMapping(value = "/inventory/price/{price}", method = RequestMethod.GET)
	@ResponseBody List<Inventory> getByPrice(@PathVariable int price) {
			return itemsRepo.findByPriceLessThanEqual(price);
	}

	/**
	 * Add Item
	 * @return transaction status
	 */
	@RequestMapping(value = "/inventory", method = RequestMethod.POST, consumes = "application/json")
	ResponseEntity<?> create(@RequestBody Inventory payload) {
		try {
			
			// check if id passed in, whether it exists already
			if (itemsRepo.exists(payload.getId())) {
				return ResponseEntity.badRequest().body("Id " + payload.getId() + " already exists");
			}
			
			itemsRepo.save(payload);
		} catch (Exception ex) {
			logger.error("Error creating item: " + ex);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating item: " + ex.toString());
		}
		
		// HTTP 201 CREATED
		final URI location =  ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(payload.getId()).toUri();
        return ResponseEntity.created(location).build();
	}


	/**
	 * Update Item
	 * @return transaction status
	 */
	@RequestMapping(value = "/inventory/{id}", method = RequestMethod.PUT, consumes = "application/json")
	ResponseEntity<?> update(@PathVariable long id, @RequestBody Inventory payload) {
		try {
			if (!itemsRepo.exists(id)) {
				return ResponseEntity.notFound().build();
			}
			
			payload.setId(id);
			itemsRepo.save(payload);
		} catch (Exception ex) {
			logger.error("Error updating item: " + ex);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating item: " + ex.toString());
		}
		
		return ResponseEntity.ok().build();
	}

	/**
	 * Delete Item
	 * @return transaction status
	 */
	@RequestMapping(value = "/inventory/{id}", method = RequestMethod.DELETE)
	ResponseEntity<?> delete(@PathVariable long id) {
		try {
			if (itemsRepo.exists(id))
				itemsRepo.delete(id);
			else {
				return ResponseEntity.notFound().build();
			}
		}
		catch (Exception ex) {
			logger.error("Error deleting item: " + ex);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting item: " + ex.toString());
		}
		return ResponseEntity.ok().build();
	}

	private Iterable<Inventory> failGood() {
		// Simply return an empty array
		ArrayList<Inventory> inventoryList = new ArrayList<Inventory>();
		return inventoryList;
	}

	/**
	 * @return Cirtcuit breaker tripped
	 */
	@HystrixCommand(fallbackMethod="failGood")
	@RequestMapping("/circuitbreaker")
	@ResponseBody
	public String tripCircuitBreaker() {
		System.out.println("Circuitbreaker Service invoked");
		return "";
	}
}
