package hello;

import hello.models.Task;
import hello.models.TaskDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

import org.springframework.web.client.RestTemplate;

import java.util.Observable;


/**
 * This is the main class interacts with ClearDB MySQL Database
 *
 * @author gchen
 */
@Controller
public class TaskController {

   @Autowired
  private TaskDao taskDao;


  /**
   * Add a new task
   *
   * @param name Task name
   * @param description Task description
   * @return Add status
   */
  @RequestMapping("/create")
  @ResponseBody
  public String create(String description, String name) {
    Task task = null;
    try {
      task = new Task(description, name);
      taskDao.save(task);
    }
    catch (Exception ex) {
      return "Error adding task: " + ex.toString();
    }
    return "Task succesfully added! (id = " + task.getId() + ")";
  }

  /**
   *
   * @param id task id
   * @return Delete status
   */
  @RequestMapping("/delete")
  @ResponseBody
  public String delete(long id) {
    try {
      Task task = new Task(id);
      taskDao.delete(task);
    }
    catch (Exception ex) {
      return "Error deleting the task:" + ex.toString();
    }
    return "Task succesfully deleted!";
  }


  @RequestMapping("/tasklist")
  @ResponseBody
  public String getAllTask() {
    String result = "";
    try {

    for (Task task : taskDao.findAll()) {
        result = result + task.getId() + "-->" + task.getName() + ", ";
      }
    }
    catch (Exception ex) {
      return "Task not found";
    }
    return result;
  }

  public String failGood(String input) {
     System.out.println("Circuit fallback");
      return "Circuit Breaker Tripped, try later -> " + input;
    }

  @HystrixCommand(fallbackMethod="failGood")
  @RequestMapping("/circuitbreaker")
  @ResponseBody
  public String testHystrix(String email) {
    System.out.println("Circuitbreaker Service is invoked");
    String result = protectedService();
    return result;
  }

  public String protectedService() {
    System.out.println("Protected Service is invoked");
    try{
      Thread.sleep(800);
      RestTemplate restTemplate = new RestTemplate();
      String consumeJSONString = restTemplate.getForObject("http://169.44.1.151:8080/rest/v1/hello/to/IBM", String.class);
      //String consumeJSONString = restTemplate.getForObject("https://www.google.com", String.class);
      return consumeJSONString;
    }catch(InterruptedException ie)
    {
      return "Interrupted";
    }

  }

  /**
   *
   */
  @RequestMapping("/update")
  @ResponseBody
  public String updateTask(long id, String description, String name) {
    try {
      Task task = taskDao.findOne(id);
      task.setDescription(description);
      task.setName(name);
      taskDao.save(task);
    }
    catch (Exception ex) {
      return "Error updating the task: " + ex.toString();
    }
    return "Task succesfully updated!";
  }

}
