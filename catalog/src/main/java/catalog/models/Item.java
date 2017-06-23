package catalog.models;

public class Item {

    // Use generated ID
    private long id;

    // Item name
    private String name;

    // Item description
    private String description;

    // Item price
    private int price;

    // Item imgAlt
    private String imgAlt;

    // Item img
    private String img;

    // Item stock
    private int stock;

    public Item() {
    }

    public Item(long id) {
        this.id = id;
    }

    public Item(String name, String description, int price, String imgAlt, String img, int stock) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.img = img;
        this.imgAlt = imgAlt;
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
        return imgAlt;
    }

    public void setImgAlt(String imgAlt) {
        this.imgAlt = imgAlt;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int value) {
        this.stock = value;
    }
    
    public boolean equals(Object otherObj) {
    	if (otherObj.getClass() != Item.class) {
    		return false;
    	}
    	
    	final Item otherItem = (Item)otherObj;
    	
    	if (otherItem.getId() != this.id) {
    		return false;
    	}

    	if (!otherItem.getName().equals(this.name)) {
    		return false;
    	}
    	
    	if (!otherItem.getDescription().equals(this.description)) {
    		return false;
    	}
    	
    	if (otherItem.getPrice() != this.price) {
    		return false;
    	}
    	
    	if (!otherItem.getImg().equals(this.img)) {
    		return false;
    	}
    	 
    	if (!otherItem.getImgAlt().equals(this.imgAlt)) {
    		return false;
    	}
    	
   	
    	if (otherItem.getStock() != this.stock) {
    		return false;
    	}
    	
    	return true;

    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        string.append("{\n");
        string.append(String.format("\t\"id\": %s,\n", this.id));
        string.append(String.format("\t\"name\": \"%s\",\n", this.name));
        string.append(String.format("\t\"description\": \"%s\",\n", this.description));
        string.append(String.format("\t\"price\": %s,\n", this.price));
        string.append(String.format("\t\"imgAlt\": \"%s\",\n", this.imgAlt));
        string.append(String.format("\t\"img\": \"%s\",\n", this.img));
        string.append(String.format("\t\"stock\": %s\n", this.stock));
        string.append("}");

        return string.toString();
    }
}