import java.io.*;
import java.net.Socket;
import java.net.URLDecoder;


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

            if (firstLine[0].toLowerCase().equals("get") || firstLine[0].toLowerCase().equals("head")) {
                response(firstLine[1], firstLine[0]);
            } else {
                responseNotAllowed();
            }
        } catch(Exception e) {
            System.out.println(e.toString());
        } finally {
            closeConnection();
        }
    }

    private void response(String fileName, String method) {
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
            File file = getFile(WEB_ROOT + fileModel.getName());
            //no index file
            if (!file.canRead()) {
                responseForbidden();
                return;
            }
        } else {
            File file = getFile(WEB_ROOT + fileModel.getName());

            if (!isValidRoot(file) || !fileModel.isValidType()) {
                responseNotAllowed();
                return;
            }
        }

        try {
            sendResponse(fileModel, method);
        } catch (Exception e) {
            System.err.println("Error in sendResponse()" + e.toString());
        }
    }

    private void sendResponse(FileModel fileModel, String method) throws IOException {
        File file = getFile(WEB_ROOT + fileModel.getName());

        if (!file.canRead()) {
            responseNotFound();
            return;
        }

        if (!isValidRoot(file)) {
            responseNotAllowed();
            return;
        }

        int fileLength = (int) file.length();
        responseOkHeaders(fileLength, fileModel.getType());

        if (method.toLowerCase().equals("get")) {
            byte[] fileData = readFileData(file, fileLength);
            outputData.write(fileData, 0, fileLength);
            outputData.flush();
        }
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
        String line = inputData.readLine();
        return line.split("\\s+");
    }


    private boolean isValidRoot(File file) {
        try {
            if (!file.getCanonicalPath().startsWith(WEB_ROOT)) {
                responseForbidden();
                System.err.println("Can't read file or inaccessible WEB_ROOT");
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            System.err.println("file.getCanonicalPath().startsWith(WEB_ROOT) " + e.toString());
        }
        return false;
    }

    private byte[] readFileData(File file, int fileLength) throws IOException {
        FileInputStream fileIn = null;
        byte[] fileData = new byte[fileLength];

        try {
            fileIn = new FileInputStream(file);
            fileIn.read(fileData);
        } finally {
            if (fileIn != null) {
                fileIn.close();
            }
        }
        return fileData;
    }

    private File getFile(String fileName) {
        final int posOfQueryStart = fileName.indexOf('?');

        if (posOfQueryStart == -1) {
            return new File(fileName);
        } else {
            return new File(fileName.substring(0, posOfQueryStart));
        }
    }

    private void closeConnection() {
        try {
            this.inputData.close();
            this.socket.close();
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }
}
