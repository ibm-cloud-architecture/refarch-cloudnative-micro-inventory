package client;

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
    private String img_alt;

    // Item img
    private String img;

    // Item stock
    private int stock;
    
    public Item() {
    }

    public Item(long id) {
        this.id = id;
    }

    public Item(String name, String description, int price, String img_alt, String img, int stock) {
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

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getImg_alt() {
		return img_alt;
	}

	public void setImg_alt(String img_alt) {
		this.img_alt = img_alt;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}
	
	public models.Item toModel() {
    	final models.Item newItem = new models.Item();
    	
    	newItem.setId(this.id);
    	newItem.setName(this.name);
    	newItem.setDescription(this.description);
    	newItem.setImg(this.img);
    	newItem.setImgAlt(this.img_alt);
    	newItem.setPrice(this.price);
    	newItem.setStock(this.stock);
    	
    	return newItem;
    }
	
	public String toString() {
        StringBuilder string = new StringBuilder();
        string.append("{\n");
        string.append(String.format("\t\"id\": %s,\n", this.id));
        string.append(String.format("\t\"name\": \"%s\",\n", this.name));
        string.append(String.format("\t\"description\": \"%s\",\n", this.description));
        string.append(String.format("\t\"price\": %s,\n", this.price));
        string.append(String.format("\t\"img_alt\": \"%s\",\n", this.img_alt));
        string.append(String.format("\t\"img\": \"%s\",\n", this.img));
        string.append(String.format("\t\"stock\": %s\n", this.stock));
        string.append("}");

        return string.toString();
    }
}
