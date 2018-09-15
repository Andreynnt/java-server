public class App {
    public static void main(String[] args) {

        Server server = new Server();
        ConfigReader configReader = new ConfigReader();
        Boolean useDefaultSettings
                = configReader.readConfig("/home/andreynt/park/Highload/config.json");

        if (!useDefaultSettings) {
            configReader.setServerSettings(server);
        }

        server.getInfo();
        server.start();
    }
}
