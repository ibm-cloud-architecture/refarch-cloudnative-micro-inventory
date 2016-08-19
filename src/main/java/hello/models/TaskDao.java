package hello.models;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;

/**
 * JPA DOA Object
 * 
 * @author gchen
 */
@Transactional
public interface TaskDao extends CrudRepository<Task, Long> {

  /**
   * 
   */
  //public Task findByDescription(String description);

}