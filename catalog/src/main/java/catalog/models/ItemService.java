package catalog.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ItemService {
    @Value("${elasticsearch.url}")
    private String url;

    @Value("${elasticsearch.user}")
    private String user;

    @Value("${elasticsearch.password}")
    private String password;

    @Value("${elasticsearch.index}")
    private String index;

    @Value("${elasticsearch.doc_type}")
    private String doc_type;

    private OkHttpClient client;

    // Constructor
    public ItemService() {
        client = new OkHttpClient();
    }

    // Get all rows from database
    public List<Item> findAll() {
        List<Item> list;
        String req_url = url + "/" + index + "/" + doc_type + "/_search?size=1000&pretty=1";
        Response response = perform_request(req_url);

        try {
            list = getItemsFromResponse(response);

        } catch (IOException e) {
            // Just to be safe
            list = null;
            System.out.println(e);
        }

        return list;
    }

    // Get all rows from database
    public Item findById(long id) {
        Item item = null;
        String req_url = url + "/" + index + "/" + doc_type + "/" + id;
        Response response = perform_request(req_url);

        try {
            JSONObject resp = new JSONObject(response.body().string());
            System.out.println("Response: " + resp.toString());

            if (resp.has("found") && resp.getBoolean("found") == true) {
                JSONObject itm = resp.getJSONObject("_source");
                System.out.println("Found item: " + id);
                System.out.println(itm);
                item = new ObjectMapper().readValue(itm.toString(), Item.class);
            }

        } catch (IOException e) {
            System.out.println(e);
        }

        return item;
    }

    // Get all rows from database
    public List<Item> findByNameContaining(String name) {
        List<Item> list;
        String req_url = url + "/" + index + "/" + doc_type + "/_search?q=name:" + name;
        Response response = perform_request(req_url);

        try {
            list = getItemsFromResponse(response);

        } catch (IOException e) {
            // Just to be safe
            list = null;
            System.out.println(e);
        }

        return list;
    }

    private Response perform_request(String req_url) {
        Response response;
        System.out.println("req_url: " + req_url);

        try {
            Request.Builder builder = new Request.Builder()
                    .url(req_url)
                    .get()
                    .addHeader("content-type", "application/json");

            if (user != null && !user.equals("") && password != null && !password.equals("")) {
                System.out.println("Adding credentials to request");
                builder.addHeader("Authorization", Credentials.basic(user, password));
            }

            Request request = builder.build();
            response = client.newCall(request).execute();

        } catch (IOException e) {
            // Just to be safe
            response = null;
            System.out.println(e);
        }

        return response;
    }

    private List<Item> getItemsFromResponse(Response response) throws IOException {
        List<Item> list = new ArrayList<Item>();

        JSONObject resp = new JSONObject(response.body().string());
        JSONArray hits = resp.getJSONObject("hits").getJSONArray("hits");

        for (int i = 0; i < hits.length(); i++) {
            JSONObject hit = hits.getJSONObject(i).getJSONObject("_source");
            Item item = new ObjectMapper().readValue(hit.toString(), Item.class);
            list.add(item);
        }

        return list;
    }
}