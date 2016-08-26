package inventory.mysql.models;

import java.util.List;
import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;

/**
 * Inventory Repository
 * 
 */
@Transactional
public interface IInventoryRepo extends CrudRepository<Inventory, Long> {
	// find all by naming like /inventory/name/{name}
	List<Inventory> findByNameContaining(String name);
	
	// find all whose price is less than or equal to /inventory/price/{price}
	List<Inventory> findByPriceLessThanEqual(int price);
}