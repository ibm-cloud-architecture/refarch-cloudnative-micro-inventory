package catalog;

import java.util.Random;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import catalog.models.Item;

public class CatalogControllerTest {

    @Test
    public void testMarshalToJson() throws Exception {
        final Item inv = new Item();
        final Random rnd = new Random();

        long id = rnd.nextLong();
        int price = rnd.nextInt();
        int stock = rnd.nextInt();

        final ObjectMapper mapper = new ObjectMapper();
        inv.setId(id);
        inv.setName("myInv");
        inv.setDescription("Test inventory description");
        inv.setImg("/image/myimage.jpg");
        inv.setImgAlt("image alt text");
        inv.setPrice(price);
        inv.setStock(stock);


        final String json = mapper.writeValueAsString(inv);

        // construct a json string with the above properties

        final StringBuilder myJsonStr = new StringBuilder();

        myJsonStr.append("{");
        myJsonStr.append("\"id\":").append(id).append(",");
        myJsonStr.append("\"name\":").append("\"myInv\"").append(",");
        myJsonStr.append("\"description\":").append("\"Test inventory description\"").append(",");
        myJsonStr.append("\"img\":").append("\"/image/myimage.jpg\"").append(",");
        myJsonStr.append("\"imgAlt\":").append("\"image alt text\"").append(",");
        myJsonStr.append("\"stock\":").append(stock).append(",");
        myJsonStr.append("\"price\":").append(price);
        myJsonStr.append("}");

        final String myJson = myJsonStr.toString();
        System.out.println("Marshalled Item to JSON:" + myJson);
        System.out.println("My JSON String:" + myJson);

        final JsonNode jsonObj = mapper.readTree(json);
        final JsonNode myJsonObj = mapper.readTree(myJson);


        assert (jsonObj.equals(myJsonObj));
    }

    @Test
    public void testMarshalFromJson() throws Exception {
        final Random rnd = new Random();

        long id = rnd.nextLong();
        int price = rnd.nextInt();
        int stock = rnd.nextInt();

        final ObjectMapper mapper = new ObjectMapper();

        // construct a json string with the above properties

        final StringBuilder myJsonStr = new StringBuilder();

        myJsonStr.append("{");
        myJsonStr.append("\"id\":").append(id).append(",");
        myJsonStr.append("\"name\":").append("\"myInv\"").append(",");
        myJsonStr.append("\"description\":").append("\"Test inventory description\"").append(",");
        myJsonStr.append("\"img\":").append("\"/image/myimage.jpg\"").append(",");
        myJsonStr.append("\"imgAlt\":").append("\"image alt text\"").append(",");
        myJsonStr.append("\"stock\":").append(stock).append(",");
        myJsonStr.append("\"price\":").append(price);
        myJsonStr.append("}");

        final String myJson = myJsonStr.toString();
        System.out.println("My JSON String:" + myJson);

        // marshall json to Item object

        final Item inv = mapper.readValue(myJson, Item.class);

        // make sure all the properties match up
        assert (inv.getId() == id);
        assert (inv.getName().equals("myInv"));
        assert (inv.getDescription().equals("Test inventory description"));
        assert (inv.getImg().equals("/image/myimage.jpg"));
        assert (inv.getImgAlt().equals("image alt text"));
        assert (inv.getStock() == stock);
        assert (inv.getPrice() == price);
    }
}