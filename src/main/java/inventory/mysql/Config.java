package inventory.mysql;

import org.json.JSONArray;
import org.json.JSONObject;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

public class Config {

    // Elasticsearch stuff
    public String es_connection_string;
    public String es_index;
    public String es_doc_type;

    // MessageHUB Stuff
    public String mh_topic;
    public String mh_message;

    public String mh_user;
    public String mh_password;
    public String mh_api_key;
    public String mh_kafka_rest_url;
    public String mh_kafka_brokers_sasl;

    public Config() {

        JSONObject json = null;

        try {
            // Get application.yml from resources folder
            ClassLoader classLoader = getClass().getClassLoader();
            String path = classLoader.getResource("application.yml").getFile();

            // Get YAML string
            String fileContents = Config.readFileAsString(path);

            // Convert to JSON as it easier to extract data
            String jsonString = Config.convertToJson(fileContents);

            // Extract properties from JSON
            json = new JSONObject(jsonString);


        } catch (Exception e) {
            e.printStackTrace();
        }

        // Extract stuff
        // Elasticsearch
        JSONObject elasticsearch = json.getJSONObject("elasticsearch");
        es_connection_string = Config.select(System.getenv("elasticsearch_connection_string"),
                elasticsearch.getString("connection_string"));

        // Optional
        es_index = Config.select(System.getenv("elasticsearch_connection_string"),
                elasticsearch.getString("index"));

        es_doc_type = Config.select(System.getenv("elasticsearch_connection_string"),
                elasticsearch.getString("doc_type"));

        // Get topic and message
        JSONObject messagehub = json.getJSONObject("message_hub");

        mh_topic = Config.select(System.getenv("message_hub_topic"),
                messagehub.getString("topic"));

        mh_message = Config.select(System.getenv("message_hub_message"),
                messagehub.getString("message"));

        // Message Hub
        // Check if running on Bluemix and Message Hub is bound
        String vcap_string = System.getenv("VCAP_SERVICES");
        if (vcap_string != null && (vcap_string.equals("") == false)) {
            System.out.println("Message Hub is IN VCAP!");

            JSONObject vcap = new JSONObject(vcap_string);
            StringBuilder brokers = new StringBuilder();

            JSONArray messagehub_array = vcap.getJSONArray("messagehub");
            JSONObject msghub = messagehub_array.getJSONObject(0);
            JSONObject credentials = msghub.getJSONObject("credentials");

            // Assign username and password
            mh_user = credentials.getString("user");
            mh_password = credentials.getString("password");
            mh_api_key = credentials.getString("api_key");
            mh_kafka_rest_url = credentials.getString("kafka_rest_url");

            // Assign servers
            JSONArray brokers_array = credentials.getJSONArray("kafka_brokers_sasl");

            for (int i = 0; i < brokers_array.length(); i++) {
                String broker = brokers_array.getString(i);
                brokers.append(broker);
                // Append separator
                if (i < (brokers_array.length() - 1)) {
                    brokers.append(",");
                }
            }

            // Assign servers
            mh_kafka_brokers_sasl = brokers.toString();

        } else {
            System.out.println("Message HUB not in VCAP, using environment variables");

            mh_user = Config.select(System.getenv("message_hub_user"),
                    messagehub.getString("user"));

            mh_password = Config.select(System.getenv("message_hub_password"),
                    messagehub.getString("password"));

            mh_api_key = Config.select(System.getenv("message_hub_api_key"),
                    messagehub.getString("api_key"));

            mh_kafka_rest_url = Config.select(System.getenv("message_hub_kafka_rest_url"),
                    messagehub.getString("kafka_rest_url"));

            mh_kafka_brokers_sasl = Config.select(System.getenv("message_hub_kafka_brokers_sasl"),
                    Config.get_servers(messagehub.getJSONArray("kafka_brokers_sasl")));
        }

        // Validate all the things
        Config.validate("connection_string", es_connection_string, "elasticsearch");
        Config.validate("index", es_index, "elasticsearch");
        Config.validate("doc_type", es_doc_type, "elasticsearch");
        Config.validate("topic", mh_topic, "message_hub");
        Config.validate("message", mh_message, "message_hub");
        Config.validate("user", mh_user, "message_hub");
        Config.validate("password", mh_password, "message_hub");
        Config.validate("api_key", mh_api_key, "message_hub");
        Config.validate("kafka_rest_url", mh_kafka_rest_url, "message_hub");
        Config.validate("kafka_brokers_sasl", mh_kafka_brokers_sasl, "message_hub");
    }

    private static void validate(String key, String value, String section) {
        if (value == null || value.equals("")) {
            System.out.println(String.format("\"%s\" parameter is equal to \"%s\", which is not valid", key, value));
            System.out.println(String.format("Please provide \"%s\" in the \"%s\" section in src/main/resources/application.yml OR", key, section));
            System.out.println(String.format("Provide \"%s_%s\" as an environment variable", section, key));
        }
    }

    private static String select(String a, String b) {
        String string = a;

        if (string == null || string.equals("")) {
            string = b;
        }

        if (string == null || string.equals("")) {
            string = null;
        }

        return string;
    }

    private static String get_servers(JSONArray brokers_array) {
        StringBuilder brokers = new StringBuilder();
        // Get servers
        for (int i = 0; i < brokers_array.length(); i++) {
            String broker = brokers_array.getString(i);
            brokers.append(broker);
            // Append separator
            if (i < (brokers_array.length() - 1)) {
                brokers.append(",");
            }
        }
        return brokers.toString();
    }

    private static String convertToJson(String yamlString) {
        Yaml yaml = new Yaml();
        Map<String, Object> map = (Map<String, Object>) yaml.load(yamlString);

        JSONObject jsonObject = new JSONObject(map);
        return jsonObject.toString();
    }

    private static String readFileAsString(String filePath) throws IOException {
        StringBuffer fileData = new StringBuffer();
        BufferedReader reader = new BufferedReader(
                new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead = 0;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
        }
        reader.close();
        return fileData.toString();
    }
}
