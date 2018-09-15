import java.net.ServerSocket;
import java.util.Date;

public class Server {
    private Integer port = 8080;
    private Integer maxThreadsCount = 4;
    private String documentsRoot = "def_root";

    public void setPort(Integer port) {
        this.port = port;
    }

    public void setMaxThreadsCount(Integer maxThreadsCount) {
        this.maxThreadsCount = maxThreadsCount;
    }

    public void setDocumentsRoot(String documentsRoot) {
        this.documentsRoot = documentsRoot;
    }

    public void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            ServerExecutor.setWebRoot(documentsRoot);

            while (true) {
                ServerExecutor serverExecutor = new ServerExecutor(serverSocket.accept());
                System.out.println("Connecton opened. (" + new Date() + ")");

                Thread thread = new Thread(serverExecutor);
                thread.start();
            }

        } catch (Exception e) {
            System.err.println("Server Connection error : " + e.getMessage());
        }
    }

    public void getInfo() {
        StringBuilder s = new StringBuilder();
        s.append("\nServer port is: ");
        s.append(port);
        s.append("\nServer maxThreadsCount is: ");
        s.append(maxThreadsCount);
        s.append("\nServer documentsRoot: ");
        s.append(documentsRoot);
        s.append("\n\n");
        System.out.println(s.toString());
    }

}
