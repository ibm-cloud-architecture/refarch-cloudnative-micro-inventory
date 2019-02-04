package inventory;

import java.util.Properties;
import java.util.Arrays;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import inventory.config.MHConfig;
import inventory.models.Inventory;
import inventory.models.InventoryRepo;
import inventory.rest.RESTAdmin;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component("MHConsumer")
public class MHConsumer {
	private static final Logger logger = LoggerFactory.getLogger(MHConsumer.class);

	@Autowired
	@Qualifier("inventoryRepo")
	private InventoryRepo itemsRepo;

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

    public Boolean valid_config = true;

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

        if (username == null || username.equals("")) {
            valid_config = false;
            return;
        }

        if (password == null || password.equals("")) {
            valid_config = false;
            return;
        }

        if (rest_url == null || rest_url.equals("")) {
            valid_config = false;
            return;
        }

        if (api_key == null || api_key.equals("")) {
            valid_config = false;
            return;
        }

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
            logger.debug("REST Listing Topics: " + topics_string);

            // Check if topic exist
            JSONArray topics = new JSONArray(topics_string);
            boolean create_topic = true;

            for (int i = 0; i < topics.length(); i++) {
                JSONObject t = topics.getJSONObject(i);
                String t_name = t.getString("name");

                if (t_name.equals(topic)) {
                    logger.warn("Topic " + topic + " already exists!");
                    create_topic = false;
                    break;
                }
            }

            // Create topic if it does not exist
            if (create_topic) {
                logger.info("Creating the topic " + topic);
                String restResponse = RESTAdmin.createTopic(rest_url, api_key, topic);
                JSONObject json = new JSONObject(restResponse);
                String error = json.has("errorMessage") ? json.getString("errorMessage") : null;

                if (error != null) {
                    throw new Exception(error);
                }

                logger.info("Successfully created the topic");
            }

            consumer = new KafkaConsumer<String, String>(props);
            logger.info("Created Kafka Consumer");

        } catch (Exception e) {
            logger.error("Exception occurred, application will terminate:", e);
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
                logger.debug("\nMessage = %s\n", record.value());

                try {
                    JSONObject object = new JSONObject(record.value());

                    // Check if it has required fields
                    if (!object.has("itemId") || !object.has("count")) {
						logger.warn("Invalid message received: " + record.value() + ", ignoring");
						continue;
                    }

					logger.info("Valid object: " + object.getLong("itemId") + " count: " + object.getInt("count"));

					// get item for itemRepo:
					final Inventory item = itemsRepo.findOne(object.getLong("itemId"));
					if (item == null) {
						logger.warn("Received message for item that does not exist!" + object.getLong("itemId"));
						continue;
					}

					item.setStock(item.getStock() - object.getInt("count"));
					itemsRepo.save(item);
					logger.info("Updated inventory: " + object.getLong("itemId") + " new stock: " + item.getStock());

                } catch (Exception e) {
                    logger.error("Something happened parsing out message (probably not valid or JSON): " + record.value() , e);
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
            logger.info("Successfully wrote to JAAS configuration file");

        } catch (final IOException e) {
            logger.error("Error: Failed accessing to JAAS config file:");
            logger.error(e.getMessage(), e);
            throw e;
        } finally {
            if (jaasOutStream != null) {
                try {
                    jaasOutStream.close();
                    logger.info("Closed JAAS file");
                } catch (final Exception e) {
                    logger.error("Error closing generated JAAS config file:", e);
                }
            }
        }
    }
}