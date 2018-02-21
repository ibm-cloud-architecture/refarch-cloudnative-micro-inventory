package application.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import models.Item;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ElasticSearch {

	private ItemService itemService = new ItemService() ;
    
    Config config = ConfigProvider.getConfig();

    private String url = config.getValue("elasticsearch_url", String.class);

    // Optional
    private String user;

    //Optional
    private String password;

    private String index= config.getValue("elasticsearch_index", String.class);

    private String doc_type= config.getValue("elasticsearch_doc_type", String.class);

    private OkHttpClient client = new OkHttpClient();

    private Map<Long, Item> getAllRowsFromCache() {
    	
    	final List<models.Item> allItems = itemService.findAll();

    	// hash the items by Id
    	final Map<Long, Item> itemMap = new HashMap<Long, Item>();
    	for (final Item item : allItems) {
    		itemMap.put(item.getId(), item);
    	}
        
    	return itemMap;

    }

    // load multi-rows
    public void loadRows(List<Item> items) {
    	
    	// convert Item to JSONArray
    	final ObjectMapper objMapper = new ObjectMapper();

    	final Map<Long, Item> allItemMap = getAllRowsFromCache();
    	final StringBuilder sb = new StringBuilder();

    	// convert to a bulk update
    	// { "index": {"_index": "<index>", "_type": "<type>", "_id": "<itemId", "_retry_on_conflict": "3" } }
    	// { "doc": <document> }
    	for (final Item item : items) {
    		if (allItemMap.containsKey(item.getId()) &&
    			(allItemMap.remove(item.getId()).equals(item))) {
    			// the item already exists, and it's exactly the same.  continue
    			continue;
    		}

    		sb.append("{ \"index\": { \"_index\": \"" + index + "\", \"_type\": \"" + doc_type + "\", \"_id\": \"" + item.getId() + "\", \"_retry_on_conflict\": \"3\" } }\n");
			String jsonString;
			
			try {
				jsonString = objMapper.writeValueAsString(item);
			} catch (JsonProcessingException e1) {
				System.err.println("Failed to convert object to JSON "+ e1);
				continue;
			}

            System.out.println("Adding/updating item: \n" + item.getId() + ": " + jsonString);
            sb.append(jsonString + "\n");
    	}

    	// everything left in allItemMap is stuff that is still in cache that we should remove
    	for (final Item item : allItemMap.values()) {
    		System.out.println("Deleting item: \n" + item.getId());
			sb.append("{ \"delete\": { \"_index\": \"" + index + "\", \"_type\": \"" + doc_type + "\", \"_id\": \"" + item.getId() + "\", \"_retry_on_conflict\": \"3\" } }\n");
    	}

		try {

			if (sb.toString().length() == 0) {
				return;
			}
			
		    MediaType mediaType = MediaType.parse("application/json");
		    RequestBody body = RequestBody.create(mediaType, sb.toString());

			// Build URL
			//String url = String.format("%s/%s/%s/%s", this.url, index, doc_type, item.getId());
			String url = String.format("%s/_bulk", this.url);
			Request.Builder builder = new Request.Builder().url(url)
					.post(body)
					.addHeader("content-type", "application/json");

			if (user != null && !user.equals("") && password != null && !password.equals("")) {
				builder.addHeader("Authorization", Credentials.basic(user, password));
			}

			Request request = builder.build();

			Response response = client.newCall(request).execute();
			String resp_string = response.body().string();
			System.out.println("resp_string: \n" + resp_string);
			JSONObject resp = new JSONObject(resp_string);
			boolean errors = resp.getBoolean("errors");

			if (errors) {
				System.err.println("Error(s) were found with bulk items update: " + resp_string);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
    }

}
