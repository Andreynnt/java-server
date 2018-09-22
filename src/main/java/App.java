public class App {
    public static void main(String[] args) {

        Server server = new Server();
        ConfigReader configReader = new ConfigReader();
        FileModel.initTypeFiles();

        boolean useDefaultSettings = false;
        try {
            configReader.readConfig("/etc/httpd.conf");
        } catch (Exception e) {
            System.err.println(e.toString());
            useDefaultSettings = true;
        }

        if (!useDefaultSettings) {
            configReader.setServerSettings(server);
        }

        server.getInfo();
        server.start();
    }
}
