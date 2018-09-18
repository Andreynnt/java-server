import java.io.*;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.Date;


public class ServerExecutor implements Runnable {
    private Socket socket;
    private static String WEB_ROOT;
    private PrintWriter outputHeaders;
    private BufferedOutputStream outputData;
    private BufferedReader inputData;

    public ServerExecutor(Socket s) {
        socket = s;
    }

    public static void setWebRoot(String webRoot) {
        WEB_ROOT = webRoot;
    }

    @Override
    public void run() {
        try {
            inputData = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            outputHeaders = new PrintWriter(socket.getOutputStream());
            outputData = new BufferedOutputStream(socket.getOutputStream());
            String[] firstLine = readFirstLine();

            System.out.println("HTTP version = " + firstLine[2]);
            System.out.println("Requested file = " + firstLine[1]);
            System.out.println();

            switch (firstLine[0].toLowerCase()) {
                case "get": responseToGet(firstLine[1], firstLine[2]);
                case "head": responseToHead();
                default: responseNotAllowed();
            }
            closeConnection();
        } catch(Exception e) {
            System.err.println(e.toString());
        }
    }

    private void responseToGet(String fileName, String httpVersion) {
        try {
            fileName = URLDecoder.decode(fileName, "UTF-8");
        } catch (Exception e) {
            System.err.println("Can't decode url" + fileName);
            return;
        }

        FileModel fileModel = new FileModel(fileName);
        if (fileModel.isDir()) {
            fileModel.setName(fileName + "index.html");
            fileModel.setType("html");
        } else if (!fileModel.isValidType()){
             System.err.println("Bad type for file: " + fileName);
             responseNotAllowed();
             closeConnection();
             return;
        }

        try {
            sendResponse(fileModel, "get", httpVersion);
        } catch (Exception e) {
            System.err.println("Error in sendResponse()" + e.toString());
        }
    }

    private void sendResponse(FileModel fileModel, String method, String httpVersion) throws IOException {
        File file = getFile(WEB_ROOT + fileModel.getName());

        if (!file.canRead()) {
            responseNotFound();
            closeConnection();
            return;
        }

//        System.out.println("File WEB_ROOT + name = " + WEB_ROOT + fileModel.getName());
//        System.out.println("File type = " + fileModel.getType());
//        System.out.println("File canonical path = " + file.getCanonicalPath());
//        System.out.println("File fileLength = " + (int) file.length());

        if (file.getCanonicalPath().startsWith(WEB_ROOT)) {
            int fileLength = (int) file.length();
            responseOkHeaders(fileLength, fileModel.getType());

            if (method.toLowerCase().equals("get")) {
                byte[] fileData = readFileData(file, fileLength);
                outputData.write(fileData, 0, fileLength);
                outputData.flush();
            }
        } else {
            responseNotAllowed();
            System.err.println("Can't read file or inaccessible WEB_ROOT");
        }

        closeConnection();
    }

    private void responseToHead() {

    }

    private void responseOkHeaders(int length, String contentType) {
        outputHeaders.write(StatusCodes.OK(length, contentType));
        outputHeaders.flush();
    }

    private void responseNotAllowed() {
        outputHeaders.write(StatusCodes.NOT_ALLOWED());
        outputHeaders.flush();
    }

    private void responseForbidden() {
        outputHeaders.write(StatusCodes.FORBIDDEN());
        outputHeaders.flush();
    }

    private void responseNotFound() {
        System.out.println("Page not-found");
        outputHeaders.write(StatusCodes.NOT_FOUND());
        outputHeaders.flush();
    }

    private String[] readFirstLine() throws IOException {
        return inputData.readLine().split("\\s+");
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

    private File getFile(String fileName) {
        final int posOfQueryStart = fileName.indexOf('?');
        if (posOfQueryStart == -1) {
            return new File(fileName);
        } else {
            return new File(fileName.substring(0,
                    WEB_ROOT.length() + posOfQueryStart));
        }
    }

    private void closeConnection() {
        try {
            inputData.close();
        } catch (Exception e) {
            System.err.println(e.toString());
        }
        try {
            outputHeaders.close();
        } catch (Exception e) {
            System.err.println(e.toString());
        }
        try {
            outputData.close();
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }
}
