package util;



import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Global {

    public static final String ENCODE = "utf-8";
    public static final String TAB = "\t";

    public static final String userDeviceKey = "user_device:";

    public static final int defaultOverTime = 3600;
    public static final String  userId = "user_id";
    public static final String  deviceId = "device_id";

    public static void checkDir(String dirname) {
        try {
            File f = new File(dirname);
            if (!f.exists()) {
                f.mkdir();
            }
        } catch (Exception e) {
            System.out.println("mkdir {} failed " + dirname);
        }
    }


    public static void deleteFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
            }
            file.delete();
        }
    }
    public static byte[] intToByte(int a){
        return new byte[] {
                (byte) ((a >> 24) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }

    public static int byteToInt(byte[] b, int pos) {
        if(pos>=b.length){
            return 0;
        }
        return  ((b[pos+3] & 0xFF)   |
                ((b[pos+2]<<8) & 0xFF00)  |
                ((b[pos+1]<<16) & 0xFF0000) |
                ((b[pos]<<24) & 0xFF000000));
    }
}
