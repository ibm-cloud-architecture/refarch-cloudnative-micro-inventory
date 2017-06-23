package catalog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import catalog.models.Item;
import catalog.models.ItemService;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Component("ElasticSearch")
public class ElasticSearch {
	private static final Logger logger = LoggerFactory.getLogger(ElasticSearch.class);
	
    @Autowired
    private ElasticsearchConfig config;
    
    @Autowired
    private ItemService itemService;
    
    private String url;
    private String user;
    private String password;
    private String index;
    private String doc_type;
    
    private OkHttpClient client;

    @PostConstruct
    public void init() {
        // Get es_url, es_index, and es_doc_type
        url = config.getUrl();
        user = config.getUser();
        password = config.getPassword();

        // Optional
        index = config.getIndex();
        if (index == null || index.equals("")) {
            index = "micro";
        }

        doc_type = config.getDoc_type();
        if (doc_type == null || doc_type.equals("")) {
            doc_type = "items";
        }

        client = new OkHttpClient();

    }
    
    private Map<Long, Item> getAllRowsFromCache() {
    	final List<catalog.models.Item> allItems = itemService.findAll();
    	
    	
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
				logger.error("Failed to convert object to JSON", e1);
				continue;
			}
    	
            logger.info("Adding/updating item: \n" + item.getId() + ": " + jsonString);
            sb.append(jsonString + "\n");
    	}
    	
    	// everything left in allItemMap is stuff that is still in cache that we should remove
    	for (final Item item : allItemMap.values()) {
            logger.info("Deleting item: \n" + item.getId());
			sb.append("{ \"delete\": { \"_index\": \"" + index + "\", \"_type\": \"" + doc_type + "\", \"_id\": \"" + item.getId() + "\", \"_retry_on_conflict\": \"3\" } }\n");
    	}

		try {
			logger.info("post body:\n" + sb.toString() );
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(mediaType, sb.toString());

			// Build URL
			//String url = String.format("%s/%s/%s/%s", this.url, index, doc_type, item.getId());
			String url = String.format("%s/_bulk", this.url);

			Request.Builder builder = new Request.Builder().url(url)
					.post(body)
					.addHeader("content-type", "application/json");

			if (user != null && !user.equals("") && password != null && !password.equals("")) {
				logger.debug("Adding credentials to request");
				builder.addHeader("Authorization", Credentials.basic(user, password));
			}

			Request request = builder.build();

			Response response = client.newCall(request).execute();
			String resp_string = response.body().string();
			logger.info("resp_string: \n" + resp_string);
			JSONObject resp = new JSONObject(resp_string);
			boolean errors = resp.getBoolean("errors");
			
			if (errors) {
				logger.error("Error(s) were found with bulk items update: " + resp_string);
			}

			//logger.info(String.format("Item %s was %s\n\n", resp.getString("_id"), ((created == true) ? "Created" : "Updated")));

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			//e.printStackTrace();
		}
    }
}