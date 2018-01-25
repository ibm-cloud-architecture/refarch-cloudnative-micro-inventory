package application.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.squareup.okhttp.CertificatePinner;
import com.squareup.okhttp.CipherSuite;
import com.squareup.okhttp.ConnectionSpec;
import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.TlsVersion;

import models.Item;


@ApplicationScoped
public class ItemService {

	@Inject
    @ConfigProperty(name="elasticsearch-url")
    private String url = "http://elasticsearch:9200";//"https://portal-ssl825-12.bmix-dal-yp-c6544c3b-be6c-4291-b617-b963f0abd273.cent-us-ibm-com.composedb.com:32234";

    @Inject
    @ConfigProperty(name="elasticsearch-user")
    private String user;

    @Inject
    @ConfigProperty(name="elasticsearch-password")
    private String password;

    @Inject
    @ConfigProperty(name="elasticsearch-index")
    private String index="micro";

    @Inject
    @ConfigProperty(name="elasticsearch-doc_type")
    private String doc_type="items";

    private OkHttpClient client;

    // Constructor
    public ItemService() {
        client = new OkHttpClient();
    }

    // Get all rows from database
    public List<Item> findAll() {
	    System.out.println("I am in Item Service findAll");
        List<Item> list;
        final String req_url = url + "/" + index + "/" + doc_type + "/_search?size=1000&pretty=1";
        System.out.println("This is the url requested "+req_url);
        final Response response = perform_request(req_url);
        System.out.println("Generated the response "+response);

        try {
            list = getItemsFromResponse(response);
            System.out.println("The list is "+list);

        } catch (IOException e) {
            // Just to be safe
            list = null;
        }
        System.out.println("List returned");
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
        System.out.println("Request perform method entered");
        try {
            Request.Builder builder = new Request.Builder()
                    .url(req_url)
                    .get()
                    .addHeader("content-type", "application/json");

            System.out.println("builder " + builder);
            System.out.println("user "+user);
            System.out.println("password "+password);

            if (user != null && !user.equals("") && password != null && !password.equals("")) {
                builder.addHeader("Authorization", Credentials.basic(user, password));
            }

            Request request = builder.build();
            System.out.println("Request "+request);
            /*client.setConnectionSpecs(createConnectionSpecs(client));
            client.setCertificatePinner(new CertificatePinner.Builder()
                            .add(url, "sha256/MIIDczCCAlugAwIBAgIEWly9nzANBgkqhkiG9w0BAQ0FADA7MTkwNwYDVQQDDDBjZW50QHVzLmlibS5jb20tZDg0ZmU0MDQ5NmU0NDU2ZTE2ZTVhMTk1YzkzYWY5YTYwHhcNMTgwMTE1MTQ0MTM1WhcNMzgwMTE1MTQwMDAwWjA7MTkwNwYDVQQDDDBjZW50QHVzLmlibS5jb20tZDg0ZmU0MDQ5NmU0NDU2ZTE2ZTVhMTk1YzkzYWY5YTYwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQC2559wZI/qncF5BvSoGYWpigu5FUDLD2jxcou9L/Eem7sHrF1UPKq1op6ov7jg4nkPruXgybwuSAA8LFQJHJcx7IKfoq0+cWkhhjiNaK9yVdmpgkSdRgm5Gr7roZRM1u+tRNKSiz7HCzs7yxwdR6tqMKQgAWfZWTKPlI/Dbpc69fwvjT94Kb0OWSfsEY659hdcmehbclL8ZWrgKBofCBaKut9pvUhG1mOPvHlo8EWAuoOSxgYJ0QovMsrRZzGIiV+K4RQK2CS8Rw/stJkmSPg6KVCBCWUbfeXMXbNLOCMxpDg5/SR66uu9fCmAOoeXfB6ulVZYykUXmWNRomiAyMKRAgMBAAGjfzB9MB0GA1UdDgQWBBRo74jP6ymheDXeQEdPL1/JNy5S6DAOBgNVHQ8BAf8EBAMCAgQwHQYDVR0lBBYwFAYIKwYBBQUHAwEGCCsGAQUFBwMCMAwGA1UdEwQFMAMBAf8wHwYDVR0jBBgwFoAUaO+Iz+spoXg13kBHTy9fyTcuUugwDQYJKoZIhvcNAQENBQADggEBAFEru9nW40Cp/sndTA5iHjhOHl1RPKgLQijWJ4JGPCH2xJjhR2/uBKHKI/WmCp6VK6FH5ShOwOqHCCtiA9LwToQon06Gh0DcV6qgGVCR+kVhh5nSs2ymBCbRBwXF9sTmyZX/BdWLmdJfvqDWs6Ay3NyW8wyscb7Ga/jNleKOIGlwFIExvLR29vczGZZlaNPHMm2PhoRbiJVpOuEJB3YnzaI2nPQJGS/KFLUrYZhPpb5Mhifc1WWVx6vNyUtgCT87WwFGE7fQrPzB0ljYQlJskMPxvgGckOdths4zB6d15XMSh3FaxmhA3xda0Juplm17mImqGgn7ZRBNs5l1iPpQLZ4=")
                            .build());*/
            response = client.newCall(request).execute();
            System.out.println("Response generated here"+response);

        } catch (IOException e) {
            // Just to be safe
            response = null;
            System.out.println("Got you dude"+e);
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

    private static List<ConnectionSpec> createConnectionSpecs(OkHttpClient okHttpClient) {
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
      }

}
