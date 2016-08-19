package hello.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Entity Ojbect mapping with JPA
 *
 * @author gchen
 */
@Entity
@Table(name = "task")
public class Task {
  
  // Use generated ID
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;
  
  // Task Name
  @NotNull
  private String name;

  // Task description
  @NotNull
  private String description;
  
  public Task() { }

  public Task(long id) { 
    this.id = id;
  }
  
  public Task(String description, String name) {
    this.description = description;
    this.name = name;
  }

  // Getter and setter

  public long getId() {
    return id;
  }

  public void setId(long value) {
    this.id = value;
  }

  public String getDescription() {
    return description;
  }
  
  public void setDescription(String value) {
    this.description = value;
  }
  
  public String getName() {
    return name;
  }

  public void setName(String value) {
    this.name = value;
  }
  
}