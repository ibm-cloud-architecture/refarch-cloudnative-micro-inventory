package catalog;

import org.json.JSONObject;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Map;

public class Config {

    // Elasticsearch stuff
    public String es_url;
    public String es_user;
    public String es_password;
    public String es_index;
    public String es_doc_type;

    public Config() {

        // Manifest file json
        // Used to pull configuration locally
        JSONObject json = null;

        try {
            // Get application.yml from resources folder
            File file = null;
            ClassLoader classLoader = getClass().getClassLoader();
            String path = classLoader.getResource("application.yml").getFile();
            file = new File(path);

            // Probably running from command line
            if (!file.exists()) {
                System.out.println("File does not exist");
                file = new File(System.getProperty("user.dir") + "/build/resources/main/application.yml");
                System.out.println(file.getAbsolutePath());
            }

            System.out.println("Using resource resource file: " + file.getAbsolutePath());

            // Get YAML string
            String fileContents = Config.readFileAsString(file.getAbsolutePath());

            // Convert to JSON as it easier to extract data
            String jsonString = Config.convertToJson(fileContents);

            // Extract properties from JSON
            json = new JSONObject(jsonString);

        } catch (IOException | NullPointerException e) {
            System.out.println("Could not open manifest file. Probably running in docker or bluemix");
            System.out.println(e);
            json = null;
        }

        // Get everything from env variables first
        es_url = System.getenv("elasticsearch_url");
        es_user = System.getenv("elasticsearch_user");
        es_password = System.getenv("elasticsearch_password");
        es_index = System.getenv("elasticsearch_index");
        es_doc_type = System.getenv("elasticsearch_doc_type");


        // Last resource, check application.yml for missing configuration variable
        if (json != null) {
            System.out.println("Doing final check with application.yml in case we missed some variables");
            JSONObject elasticsearch = json.getJSONObject("elasticsearch");

            es_url = Config.select(es_url, elasticsearch.getString("url"));
            es_user = Config.select(es_user, elasticsearch.getString("user"));
            es_password = Config.select(es_password, elasticsearch.getString("password"));
            es_index = Config.select(es_index, elasticsearch.getString("index"));
            es_doc_type = Config.select(es_doc_type, elasticsearch.getString("doc_type"));
        }

        // Validate all the things
        Config.validate("connection_string", es_url, "elasticsearch");
        Config.validate("index", es_index, "elasticsearch");
        Config.validate("doc_type", es_doc_type, "elasticsearch");
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
