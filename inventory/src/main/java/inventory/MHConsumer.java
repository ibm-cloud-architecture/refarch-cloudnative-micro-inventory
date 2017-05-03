package inventory;

import java.util.Properties;
import java.util.Arrays;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import inventory.config.MHConfig;
import inventory.rest.RESTAdmin;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


@Component("MHConsumer")
public class MHConsumer {

    @Autowired
    @Qualifier("ElasticSearch")
    private ElasticSearch es;

    @Autowired
    @Qualifier("MHConfig")
    MHConfig config;

    private KafkaConsumer<String, String> consumer;
    private String topic;
    private String servers;
    private String username;
    private String password;
    private String rest_url;
    private String api_key;

    @PostConstruct
    public void init() {

        // Assign topic and message
        topic = config.getTopic();
        if (topic == null || topic.equals("")) {
            topic = "orders";
        }

        // Assign username and password
        username = config.getUser();
        password = config.getPassword();
        servers = config.getServers();
        rest_url = config.getKafka_rest_url();
        api_key = config.getApi_key();

        try {

            // Set JAAS config
            set_jaas_configuration();

            Properties props = new Properties();
            props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            props.put("group.id", "inventory-group");
            props.put("client.id", "inventory-id");
            props.put("security.protocol", "SASL_SSL");
            props.put("sasl.mechanism", "PLAIN");
            props.put("ssl.protocol", "TLSv1.2");
            props.put("sl.enabled.protocols", "TLSv1.2");
            props.put("ssl.endpoint.identification.algorithm", "HTTPS");
            props.put("auto.offset.reset", "latest");
            props.put("bootstrap.servers", servers);

            // Get topics to see if our topic exists
            String topics_string = RESTAdmin.listTopics(rest_url, api_key);
            System.out.println("REST Listing Topics: " + topics_string);

            // Check if topic exist
            JSONArray topics = new JSONArray(topics_string);
            boolean create_topic = true;

            for (int i = 0; i < topics.length(); i++) {
                JSONObject t = topics.getJSONObject(i);
                String t_name = t.getString("name");

                if (t_name.equals(topic)) {
                    System.out.println("Topic " + topic + " already exists!");
                    create_topic = false;
                    break;
                }
            }

            // Create topic if it does not exist
            if (create_topic) {
                System.out.println("Creating the topic " + topic);
                String restResponse = RESTAdmin.createTopic(rest_url, api_key, topic);
                JSONObject json = new JSONObject(restResponse);
                String error = json.has("errorMessage") ? json.getString("errorMessage") : null;

                if (error != null) {
                    throw new Exception(error);
                }

                System.out.println("Successfully created the topic");
            }

            consumer = new KafkaConsumer<String, String>(props);
            System.out.println("Created Kafka Consumer");

        } catch (Exception e) {
            System.out.println("Exception occurred, application will terminate:");
            System.out.println(e);
            e.printStackTrace();
            System.exit(-1);
        }
    }

    // Subscribe to topic and start polling for messages
    // If message is found, then it refreshes the cache
    public void subscribe() {
        consumer.subscribe(Arrays.asList(topic));

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(3000);
            for (ConsumerRecord<String, String> record : records) {
                System.out.printf("\nMessage = %s\n", record.value());

                try {
                    JSONObject object = new JSONObject(record.value());

                    // Check if it has required fields
                    if (object.has("itemId") && object.has("count")) {
                        System.out.println("Valid object: " + object.getLong("itemId"));
                        es.refresh_cache(object);
                    }

                } catch (Exception e) {
                    System.out.println("Something happened parsing out message (probably not valid or JSON): ");
                    System.out.println(e);
                }
            }
        }
    }

    // Creates JAAS configuration file to interact with Kafka servers securely
    // Also sets path to configuration file in Java properties
    private void set_jaas_configuration() throws IOException {
        /*
            This is what the jass.conf file should look like

            KafkaClient {
                org.apache.kafka.common.security.plain.PlainLoginModule required
                serviceName="kafka"
                username="USERNAME"
                password="PASSWORD";
            };
        */

        // Create JAAS file path
        String jaas_file_path = System.getProperty("java.io.tmpdir") + "jaas.conf";

        // Set JAAS file path in Java settings
        System.setProperty("java.security.auth.login.config", jaas_file_path);

        // Build JAAS file contents
        StringBuilder jaas = new StringBuilder();
        jaas.append("KafkaClient {\n");
        jaas.append("\torg.apache.kafka.common.security.plain.PlainLoginModule required\n");
        jaas.append("\tserviceName=\"kafka\"\n");
        jaas.append(String.format("\tusername=\"%s\"\n", username));
        jaas.append(String.format("\tpassword=\"%s\";\n", password));
        jaas.append("};");

        // Write to JAAS file
        OutputStream jaasOutStream = null;

        try {
            jaasOutStream = new FileOutputStream(jaas_file_path, false);
            jaasOutStream.write(jaas.toString().getBytes(Charset.forName("UTF-8")));
            System.out.println("Successfully wrote to JAAS configuration file");

        } catch (final IOException e) {
            System.out.println("Error: Failed accessing to JAAS config file:");
            System.out.println(e);
            throw e;
        } finally {
            if (jaasOutStream != null) {
                try {
                    jaasOutStream.close();
                    System.out.println("Closed JAAS file");
                } catch (final Exception e) {
                    System.out.println("Error closing generated JAAS config file:");
                    System.out.println(e);
                }
            }
        }
    }
}