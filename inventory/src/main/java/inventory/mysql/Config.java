package inventory.mysql;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;

public class Config {

    // Elasticsearch stuff
    public String es_url;
    public String es_user;
    public String es_password;
    public String es_index;
    public String es_doc_type;

    // MessageHUB Stuff
    public String mh_topic;

    public String mh_user;
    public String mh_password;
    public String mh_api_key;
    public String mh_kafka_rest_url;
    public String mh_kafka_brokers_sasl;

    public Config() {

        Environment env = StaticApplicationContext.getContext().getEnvironment();

        es_url = env.getProperty("elasticsearch.url");
        es_user = env.getProperty("elasticsearch.user");
        es_password = env.getProperty("elasticsearch.password");
        es_index = env.getProperty("elasticsearch.index");
        es_doc_type = env.getProperty("elasticsearch.doc_type");

        mh_topic = env.getProperty("message_hub.topic");

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
            mh_kafka_brokers_sasl = get_servers(brokers_array);

        } else {
            System.out.println("Message HUB not in VCAP, using environment variables");
            mh_user = env.getProperty("message_hub.user");
            mh_password = env.getProperty("message_hub.password");
            mh_api_key = env.getProperty("message_hub.api_key");
            mh_kafka_rest_url = env.getProperty("message_hub.kafka_rest_url");
            // Assuming comma-separated separated string
            mh_kafka_brokers_sasl = env.getProperty("message_hub.kafka_brokers_sasl");

            // Check if passed as a list
            // Empty or null means we need to index-dereference the brokers
            if (mh_kafka_brokers_sasl == null || mh_kafka_brokers_sasl.equals("")) {
                System.out.println("Passed kafka_brokers_sasl as a LIST");

                // Max of servers to parse tops
                int i = 0;
                StringBuilder brokers = new StringBuilder();

                // Get all provided brokers
                while (Config.get_broker(i) != null) {
                    brokers.append(Config.get_broker(i));
                    brokers.append(",");
                    i++;
                }

                mh_kafka_brokers_sasl = Config.remove_last_comma(brokers.toString());
                System.out.println(String.format("Picked up %d brokers", i));
            }

            System.out.println("Brokers: " + mh_kafka_brokers_sasl);
        }

        // Validate all the things
        Config.validate("url", es_url, "elasticsearch");
        Config.validate("user", es_user, "elasticsearch");
        Config.validate("password", es_password, "elasticsearch");
        Config.validate("index", es_index, "elasticsearch");
        Config.validate("doc_type", es_doc_type, "elasticsearch");
        Config.validate("topic", mh_topic, "message_hub");
        Config.validate("es_user", mh_user, "message_hub");
        Config.validate("password", mh_password, "message_hub");
        Config.validate("api_key", mh_api_key, "message_hub");
        Config.validate("kafka_rest_url", mh_kafka_rest_url, "message_hub");
        Config.validate("kafka_brokers_sasl", mh_kafka_brokers_sasl, "message_hub");
    }

    private static String get_broker(int i) {
        Environment env = StaticApplicationContext.getContext().getEnvironment();
        String broker = env.getProperty(String.format("message_hub.kafka_brokers_sasl[%d]", i));
        if (broker != null && broker.isEmpty()) {
            broker = null;
        }
        return broker;
    }

    private static String remove_last_comma(String str) {
        if (str != null && str.length() > 0 && str.charAt(str.length() - 1) == ',') {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    private static void validate(String key, String value, String section) {
        if (value == null || value.equals("")) {
            System.out.println(String.format("\"%s\" parameter is equal to \"%s\", which is not valid", key, value));
            System.out.println(String.format("Please provide \"%s\" in the \"%s\" section in src/main/resources/application.yml OR", key, section));
            System.out.println(String.format("Provide \"%s.%s\" as an environment variable\n\n", section, key));
        }
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
}
