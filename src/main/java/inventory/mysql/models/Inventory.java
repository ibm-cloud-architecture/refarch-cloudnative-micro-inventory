package inventory.mysql.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Entity: inventorydb.items
 *
 */
@Entity
@Table(name = "items")
public class Inventory {
  
  // Use generated ID
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  
  // Item name
  @NotNull
  private String name;

  // Item description
  @NotNull
  private String description;

  // Item price
  @NotNull
  private int price;

  // Item img_alt
  private String img_alt;  

  // Item img
  @NotNull
  private String img;

  // Item stock
  @NotNull
  private int stock;

  public Inventory() { }

  public Inventory(long id) { 
    this.id = id;
  }
  
  public Inventory(String name, String description, int price, String img_alt, String img, int stock) {
    this.name = name;
    this.description = description;
    this.price = price;
    this.img = img;
    this.img_alt = img_alt;
    this.stock = stock;
  }

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

  public int getPrice() {
    return price;
  }

  public void setPrice(int value) {
    this.price = value;
  }
  
  public String getImg() {
	  return img;
  }
  
  public void setImg(String img) {
	  this.img = img;
  }

  public String getImgAlt() {
    return img_alt;
  }
  
  public void setImgAlt(String img_alt) {
    this.img_alt = img_alt;
  }

  public int getStock() {
    return stock;
  }

  public void setStock(int value) {
    this.stock = value;
  }
}