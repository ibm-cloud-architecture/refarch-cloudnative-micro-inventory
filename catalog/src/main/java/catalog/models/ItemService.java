package catalog.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import catalog.Config;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class ItemService {
    private String url;
    private String user;
    private String password;
    private String index;
    private String doc_type;
    private OkHttpClient client;

    // Constructor
    public ItemService() {
        // Get config object
        Config config = new Config();

        // Get es_url, es_index, and es_doc_type
        url = config.es_url;
        user = config.es_user;
        password = config.es_password;

        // Optional
        index = config.es_index;
        if (index == null || index.equals("")) {
            index = "api";
        }

        doc_type = config.es_doc_type;
        if (doc_type == null || doc_type.equals("")) {
            doc_type = "items";
        }

        client = new OkHttpClient();
    }

    // Get all rows from database
    public List<Item> findAll() {
        List<Item> list = new ArrayList<Item>();

        try {
            Request.Builder builder = new Request.Builder()
                    .url(url + "/api/items/_search")
                    .get()
                    .addHeader("content-type", "application/json");

            if (user != null && !user.equals("") && password != null && !password.equals("")) {
                System.out.println("Adding credentials to request");
                builder.addHeader("Authorization", Credentials.basic(user, password));
            }

            Request request = builder.build();
            Response response = client.newCall(request).execute();
            list = getItemsFromResponse(response);

        } catch (IOException e) {
            System.out.println(e);
        }

        return list;
    }

    // Get all rows from database
    public Item findById(long id) {
        Item item = null;

        try {
            Request.Builder builder = new Request.Builder()
                    .url(url + "/api/items/" + id)
                    .get();

            if (user != null && !user.equals("") && password != null && !password.equals("")) {
                System.out.println("Adding credentials to request");
                builder.addHeader("Authorization", Credentials.basic(user, password));
            }

            Request request = builder.build();
            Response response = client.newCall(request).execute();
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
        List<Item> list = null;

        try {
            Request.Builder builder = new Request.Builder()
                    .url(url + "/api/items/_search?q=name%3A" + name)
                    .get()
                    .addHeader("content-type", "application/json");

            if (user != null && !user.equals("") && password != null && !password.equals("")) {
                System.out.println("Adding credentials to request");
                builder.addHeader("Authorization", Credentials.basic(user, password));
            }

            Request request = builder.build();
            Response response = client.newCall(request).execute();
            list = getItemsFromResponse(response);

        } catch (IOException e) {
            System.out.println(e);
        }

        return list;
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