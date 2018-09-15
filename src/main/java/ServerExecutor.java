import java.io.*;
import java.net.Socket;
import java.util.Date;

public class ServerExecutor implements Runnable {
    private Socket socket;
    private static String WEB_ROOT;

    public ServerExecutor(Socket s) {
        socket = s;
    }

    public static void setWebRoot(String webRoot) {
        WEB_ROOT = webRoot;
    }

    @Override
    public void run() {
        PrintWriter headersOut;
        BufferedOutputStream dataOut;

        try {
            headersOut = new PrintWriter(socket.getOutputStream());
            dataOut = new BufferedOutputStream(socket.getOutputStream());

            File file = new File(WEB_ROOT, "index.html");
            int fileLength = (int) file.length();
            String content = "html";
            byte[] fileData = readFileData(file, fileLength);

            headersOut.println("HTTP/1.1 200 OK");
            headersOut.println("Server: Java HTTP Server from SSaurel : 1.0");
            headersOut.println("Date: " + new Date());
            headersOut.println("Content-type: " + content);
            headersOut.println("Content-length: " + fileLength);
            headersOut.println(); // blank line between headers and content, very important !
            headersOut.flush(); // flush character output stream buffer

            dataOut.write(fileData, 0, fileLength);
            dataOut.flush();
        } catch(Exception e) {
            System.err.println(e.toString());
        }
    }

    private byte[] readFileData(File file, int fileLength) throws IOException {
        FileInputStream fileIn = null;
        byte[] fileData = new byte[fileLength];

        try {
            fileIn = new FileInputStream(file);
            fileIn.read(fileData);
        } finally {
            if (fileIn != null)
                fileIn.close();
        }

        return fileData;
    }
}
