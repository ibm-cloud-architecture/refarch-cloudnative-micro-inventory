package catalog;

import catalog.models.CatalogItem;
import catalog.models.CatalogItemRepository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

/**
 * REST Controller to manage Inventory database
 */
@RestController
public class CatalogController {

    private static final Logger logger = LoggerFactory.getLogger(CatalogController.class);
    
    @Autowired
    CatalogItemRepository itemRepo;

    /**
     * @return all items in inventory
     */
    @HystrixCommand(fallbackMethod = "defaultGetAllItems")
    @RequestMapping(value = "/items", method = RequestMethod.GET)
    @ResponseBody
    List<CatalogItem> getInventory() {
    	logger.info("/items");
    	final List<CatalogItem> list = new ArrayList<>();
    	final Iterator<CatalogItem> iter = itemRepo.findAll().iterator();
    	while (iter.hasNext()) {
    		list.add(iter.next());
    	}
    	
    	return list;
    }
    
    List<CatalogItem> defaultGetAllItems() {
    	return new ArrayList<CatalogItem>();
    }

    /**
     * @return item by id
     */
    @HystrixCommand(fallbackMethod = "defaultItemById")
    @RequestMapping(value = "/items/{id}", method = RequestMethod.GET)
    ResponseEntity<?> getById(@PathVariable long id) {
    	logger.info("/items/" + id);
        final CatalogItem item = itemRepo.findOne(id);
        if (item == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(item);
    }
    
	ResponseEntity<?> defaultItemById(long id) {
		// Simply return an empty array
		logger.error("Item not found: " + id);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item with ID " + id + " not found");
    }


    /**
     * @return item(s) containing name
     */
    @HystrixCommand(fallbackMethod = "defaultItemByName")
    @RequestMapping(value = "/items/name/{name}", method = RequestMethod.GET)
    @ResponseBody
    List<CatalogItem> getByName(@PathVariable String name) {
    	logger.info("/items/name/" + name);
        Page<CatalogItem> itemsLike = itemRepo.findByNameLike(name, new PageRequest(0, 10));
        
        return itemsLike.getContent();
    }
    
    List<CatalogItem> defaultItemByName() {
    	return new ArrayList<CatalogItem>();
    }

}
