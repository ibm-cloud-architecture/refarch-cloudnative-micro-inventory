package inventory.mysql;

import java.util.Properties;
import java.util.Arrays;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.json.JSONArray;
import org.json.JSONObject;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public class MHConsumer {

    private KafkaConsumer<String, String> consumer;
    private String topic;
    private String message;
    private String servers;
    private String username;
    private String password;

    // Construcor
    public MHConsumer() {
        // Assign topic and message
        topic = System.getenv("mh_topic");
        if (topic == null || topic.equals("")) {
            topic = "api";
        }

        message = System.getenv("mh_message");
        if (message == null || message.equals("")) {
            message = "refresh_cache";
        }

        try {
            // Set servers and credentials
            set_servers_and_credentials();

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

            consumer = new KafkaConsumer<String, String>(props);
            System.out.println("Created Kafka Consumer");

        } catch (Exception e) {
            System.out.println("Exception occurred, application will terminate:");
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
                System.out.printf("Message = %s\n", record.value());

                // Check if message matches the trigger
                if (record.value().toLowerCase().contains(message.toLowerCase())) {
                    System.out.println("Got the right message! Refreshing cache!\n\n");
                    ElasticSearch es = new ElasticSearch();
                    es.refresh_cache();
                }
            }
        }
    }

    // Extracts kafka servers and Message Hub username/password from VCAP_SERVICES
    private void set_servers_and_credentials () {
        String vcap_string = System.getenv("VCAP_SERVICES");
        JSONObject vcap = new JSONObject(vcap_string);
        StringBuilder brokers = new StringBuilder();

        JSONArray messagehub_array = vcap.getJSONArray("messagehub");
        JSONObject messagehub = messagehub_array.getJSONObject(0);
        JSONObject credentials = messagehub.getJSONObject("credentials");
        JSONArray brokers_array = credentials.getJSONArray("kafka_brokers_sasl");

        // Get servers
        for (int i = 0; i < brokers_array.length(); i++) {
            String broker = brokers_array.getString(i);
            brokers.append(broker);
            // Append separator
            if (i < (brokers_array.length() - 1)) {
                brokers.append(",");
            }
        }

        // Assign username and password
        username = credentials.getString("user");
        password = credentials.getString("password");

        // Assign servers
        servers = brokers.toString();
        System.out.println("Got servers: " + servers);
    }

    // Creates JAAS configuration file to interact with Kafka servers securely
    // Also sets path to configuration file in Java properties
    private void set_jaas_configuration () throws IOException {
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