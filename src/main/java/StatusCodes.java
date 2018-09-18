import java.util.Date;

public class StatusCodes {
        public static String OK(int length, String contentType) {
            return "HTTP/1.1 200 OK" + "\r\n" +
                    "Date: " + new Date().toString() + "\r\n" +
                    "Server: Java-Highload \r\n"+
                    "Content-Length: " + length + "\r\n" +
                    "Content-type: " + contentType + "\r\n"+
                    "Connection: " + "Closed" + "\r\n\r\n";
        }

        public static String FORBIDDEN() {
            return "HTTP/1.1 403 Forbidden" + "\r\n"+
                    "Date: " + new Date().toString() + "\r\n" +
                    "Server: Java-Highload \r\n"+
                    "Connection: " + "Closed" + "\r\n\r\n";
        }

        public static String NOT_FOUND() {
            return "HTTP/1.1 404 Not Found" + "\r\n"+
                    "Date: " + new Date().toString() + "\r\n" +
                    "Server: Java-Highload \r\n"+
                    "Connection: " + "Closed" + "\r\n\r\n";
        }

        public static String NOT_ALLOWED() {
            return "HTTP/1.1 405 Method Not Allowed" + "\r\n"+
                    "Date: " + new Date().toString() + "\r\n" +
                    "Server: Java-Highload \r\n"+
                    "Connection: " + "Closed" + "\r\n\r\n";
        }
}
