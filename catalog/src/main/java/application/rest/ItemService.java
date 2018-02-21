package application.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;

import models.Item;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@ApplicationScoped
public class ItemService {
	
	Config config = ConfigProvider.getConfig();

    private String url = config.getValue("elasticsearch_url", String.class);

    // Optional
    private String user;

    //Optional
    private String password;

    private String index= config.getValue("elasticsearch_index", String.class);

    private String doc_type= config.getValue("elasticsearch_doc_type", String.class);

    private OkHttpClient client;

    // Constructor
    public ItemService() {
        client = new OkHttpClient();
    }

    // Get all rows from database
    public List<Item> findAll() {
        List<Item> list;
        final String req_url = url+"/"+index+"/"+doc_type+"/_search?size=1000&pretty=1";
        final Response response = perform_request(req_url);
        
        try {
            list = getItemsFromResponse(response);
        } catch (IOException e) {
            // Just to be safe
            list = null;
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

            if (resp.has("found") && resp.getBoolean("found") == true) {
                JSONObject itm = resp.getJSONObject("_source");

                item = new Gson().fromJson(itm.toString(), Item.class);
            }

        } catch (IOException e) {
        	e.printStackTrace();
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
            e.printStackTrace();
        }

        return list;
    }

    private Response perform_request(String req_url) {
        Response response;
        try {
            Request.Builder builder = new Request.Builder()
                    .url(req_url)
                    .get()
                    .addHeader("content-type", "application/json");

            if (user != null && !user.equals("") && password != null && !password.equals("")) {
                builder.addHeader("Authorization", Credentials.basic(user, password));
            }

            Request request = builder.build();
            
            response = client.newCall(request).execute();    

        } catch (IOException e) {
            // Just to be safe
            response = null;
            e.printStackTrace();
        }

        return response;
    }

    private List<Item> getItemsFromResponse(Response response) throws IOException {
        List<Item> list = new ArrayList<Item>();
        JSONObject resp = new JSONObject(response.body().string());
        if (!resp.has("hits")) {
        	    // empty cache
        	    return list;
        }

        JSONArray hits = resp.getJSONObject("hits").getJSONArray("hits");

        for (int i = 0; i < hits.length(); i++) {
            JSONObject hit = hits.getJSONObject(i).getJSONObject("_source");
            Item item = new Gson().fromJson(hit.toString(), Item.class);
            list.add(item);
        }

        return list;
    }

    /*private static List<ConnectionSpec> createConnectionSpecs(OkHttpClient okHttpClient) {
    	ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
    		    .tlsVersions(TlsVersion.TLS_1_2)
    		    .cipherSuites(
    		          CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
    		          CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
    		          CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256)
    		    .allEnabledTlsVersions()
    		    .supportsTlsExtensions(false)
    		    .allEnabledCipherSuites()
    		    .build();
        return Collections.singletonList(spec);
      }*/

}
