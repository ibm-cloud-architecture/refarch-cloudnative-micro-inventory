package catalog.client;

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
    
    public catalog.models.Item toModel() {
    	final catalog.models.Item newItem = new catalog.models.Item();
    	
    	newItem.setId(this.id);
    	newItem.setName(this.name);
    	newItem.setDescription(this.description);
    	newItem.setImg(this.img);
    	newItem.setImgAlt(this.imgAlt);
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
        string.append(String.format("\t\"imgAlt\": \"%s\",\n", this.imgAlt));
        string.append(String.format("\t\"img\": \"%s\",\n", this.img));
        string.append(String.format("\t\"stock\": %s\n", this.stock));
        string.append("}");

        return string.toString();
    }
}