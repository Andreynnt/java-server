import java.util.HashMap;

public class FileModel {
    private String name;
    private String type;
    private static HashMap<String, String> allowedTypes;


    public FileModel(String name) {
        this.name = name;
    }

    public boolean isDir() {
        final int lastDot = name.lastIndexOf('.');
        return (name.endsWith("/") && lastDot == -1);
    }

    public boolean isValidType() {
        int dotPos = name.lastIndexOf('.');
        if (dotPos != -1) {
            try {
                this.type = allowedTypes.get(name.substring(dotPos + 1));
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static void initTypeFiles() {
        final HashMap<String, String> allowedTypes = new HashMap<>();
        allowedTypes.put("html", "text/html");
        allowedTypes.put("css", "text/css");
        allowedTypes.put("js", "text/javascript");
        allowedTypes.put("jpg", "image/jpeg");
        allowedTypes.put("jpeg", "image/jpeg");
        allowedTypes.put("png", "image/png");
        allowedTypes.put("gif", "image/gif");
        allowedTypes.put("swf", "application/x-shockwave-flash");
        FileModel.allowedTypes = allowedTypes;
    }
}
