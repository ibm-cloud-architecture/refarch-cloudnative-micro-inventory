package catalog;

import catalog.models.Item;

import java.util.List;

import catalog.models.ItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    Logger logger = LoggerFactory.getLogger(CatalogController.class);

    @Autowired
    ItemService itemsRepo;

    /**
     * @return all items in inventory
     */
    @HystrixCommand(fallbackMethod = "failGood")
    @RequestMapping(value = "/items", method = RequestMethod.GET)
    @ResponseBody
    List<Item> getInventory() {
        return itemsRepo.findAll();
    }

    /**
     * @return item by id
     */
    @RequestMapping(value = "/items/{id}", method = RequestMethod.GET)
    ResponseEntity<?> getById(@PathVariable long id) {
        Item item = itemsRepo.findById(id);
        if (item == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(item);
    }

    /**
     * @return item(s) containing name
     */
    @RequestMapping(value = "/items/name/{name}", method = RequestMethod.GET)
    @ResponseBody
    List<Item> getByName(@PathVariable String name) {
        return itemsRepo.findByNameContaining(name);
    }

}
