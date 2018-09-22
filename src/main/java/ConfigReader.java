import java.io.*;


public class ConfigReader {
    private Integer port;
    private Integer maxThreadsCount;
    private String documentsRoot;

    public void readConfig(String pathToConfig) throws Exception{
        FileInputStream fstream = new FileInputStream(pathToConfig);

        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
        String configLine;

        while ((configLine = br.readLine()) != null) {
            final String name = configLine.split(" ")[0];
            final String value = configLine.split(" ")[1];

            switch (name) {
                case "listen": port = Integer.parseInt(value); break;
                case "document_root": documentsRoot = value; break;
                case "thread_limit": maxThreadsCount = Integer.parseInt(value); break;
            }
        }
        fstream.close();
        br.close();
    }

    public void setServerSettings(Server server) {
        server.setDocumentsRoot(documentsRoot);
        server.setMaxThreadsCount(maxThreadsCount);
        server.setPort(port);
    }
}
