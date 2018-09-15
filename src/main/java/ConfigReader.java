import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.*;


public class ConfigReader {
    private Long port;
    private Long maxThreadsCount;
    private String documentsRoot;

    public Boolean readConfig(String pathToConfig) {
        JSONParser parser = new JSONParser();
        try {
            JSONObject config = (JSONObject) parser.parse(new FileReader(pathToConfig));
            port = (Long) config.get("PORT");
            maxThreadsCount = (Long) config.get("MAX_THREADS_COUNT");
            documentsRoot = (String) config.get("DOCUMENTS_ROOT");
            return false;
        } catch (Exception error) {
            System.err.println("Error with JSON config file: " + error.toString());
            return true;
        }
    }

    public void setServerSettings(Server server) {
        server.setDocumentsRoot(documentsRoot);
        server.setMaxThreadsCount(maxThreadsCount.intValue());
        server.setPort(port.intValue());
    }
}
