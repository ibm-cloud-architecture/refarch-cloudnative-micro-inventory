package inventory.mysql;

import inventory.mysql.models.Inventory;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ElasticSearch {

    private InventoryController inventoryController;

    private String connection;
    private String index;
    private String doc_type;
    private OkHttpClient client;

    // Constructor
    public ElasticSearch() {
        // Get config object
        Config config = new Config();

        // Get es_connection_string, es_index, and es_doc_type
        connection = config.es_connection_string;

        // Optional
        index = config.es_index;
        if (index == null || index.equals("")) {
            index = "api";
        }

        doc_type = config.es_doc_type;
        if (doc_type == null || doc_type.equals("")) {
            doc_type = "items";
        }

        // Get InventoryController
        inventoryController = ((InventoryController) StaticApplicationContext.getContext().getBean("inventoryController"));
        client = new OkHttpClient();
    }

    // Subscribe to topic and start polling
    public void refresh_cache() {
        JSONArray rows = get_all_rows();
        load_rows_into_cache(rows);
    }

    // Get all rows from database
    public JSONArray get_all_rows() {
        JSONArray rows = null;
        try {
            Iterable<Inventory> items = inventoryController.getInventory();
            StringBuilder rows_string = new StringBuilder();

            // Build the string for JSONArray
            rows_string.append("[");
            for (Inventory item : items) {
                rows_string.append(item.toString());
                rows_string.append(",");
            }
            // Remove last ,
            rows_string.deleteCharAt(rows_string.length() - 1);

            // Finish array
            rows_string.append("]");

            rows = new JSONArray(rows_string.toString());

        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            System.out.println(sw.toString());

            rows = new JSONArray("[]");
        }

        return rows;
    }

    // Subscribe to topic and start polling
    public void load_rows_into_cache(JSONArray rows) {
        for (int i = 0; i < rows.length(); i++) {
            JSONObject jsonObj = rows.getJSONObject(i);
            System.out.println(jsonObj.toString());

            try {
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, jsonObj.toString());

                // Build URL
                String url = String.format("%s/%s/%s/%s", connection, index, doc_type, jsonObj.getInt("id"));
                Request request = new Request.Builder()
                        .url(url)
                        .put(body)
                        .addHeader("content-type", "application/json")
                        .build();

                Response response = client.newCall(request).execute();
                JSONObject resp = new JSONObject(response.body().string());
                boolean created = resp.getBoolean("created");

                System.out.printf("Item %s was %s\n\n", resp.getString("_id"), ((created == true) ? "Created" : "Updated"));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}