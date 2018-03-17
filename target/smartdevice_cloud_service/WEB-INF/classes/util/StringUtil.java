package util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xerial.snappy.Snappy;

public class StringUtil {
    private static final Logger logger = LoggerFactory.getLogger(StringUtil.class);


    public static boolean isEmpty(String phrase) {
        if (phrase != null && phrase.trim().length() != 0) {
            return false;
        }
        return true;
    }

    public static String full2Half(String fullstr) {
        if (isEmpty(fullstr)) {
            return null;
        }
        char[] c = fullstr.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] >= 65281 && c[i] <= 65374) {
                c[i] = (char) (c[i] - 65248);
            } else if (c[i] == 12288) {
                c[i] = (char) 32;
            }
        }
        return new String(c);
    }


    public static String half2Full(String halfstr) {
        if (isEmpty(halfstr)) {
            return null;
        }
        char[] c = halfstr.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 32) {
                c[i] = (char) 12288;
            } else if (c[i] < 127) {
                c[i] = (char) (c[i] + 65248);
            }
        }
        return new String(c);
    }

    public static String[] splitENChinese(String in){
        if(in==null || in.length()==0){
            return null;
        }
        boolean preChinese = false;

        StringBuilder sb = new StringBuilder();
        if(CharUtil.isChinese(in.charAt(0))){
            preChinese = true;
        }
        sb.append(in.charAt(0));
        for(int i=1; i<in.length(); ++i){
            if(!preChinese){
                sb.append(" ");
            }
            if(preChinese && CharUtil.isNumEn(in.charAt(i))){
                sb.append(" ");
            }
            sb.append(in.charAt(i));
            if(CharUtil.isNumEn(in.charAt(i))){
                preChinese = false;
            }else {
                preChinese = true;
            }
        }
        return sb.toString().split(" ");

    }
    public static String[] splitChinese(String in){
        String[] phrases = in.split("[^\u4E00-\u9FA5]");
        return phrases;
    }

    public static String removeSpaceEx(String phrase) {
        if (isEmpty(phrase)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        boolean prespace = true;
        for (int i = 0; i < phrase.length(); ++i) {
            char c = phrase.charAt(i);
            if (!CharUtil.isNumEn(c) && !CharUtil.isChinese(c)) {
                if (!prespace) {
                    sb.append(' ');
                    prespace = true;
                }
                continue;
            }
            if (c == 32) {
                if (!prespace) {
                    sb.append(phrase.charAt(i));
                    prespace = true;
                }
            } else {
                sb.append(phrase.charAt(i));
                prespace = false;
            }
        }
        return sb.toString().trim();
    }


    public static String normalizeString(String phrase) {
        if (isEmpty(phrase)) {
            return null;
        }
        String halfstr = full2Half(phrase.toLowerCase());
        return removeSpaceEx(halfstr);
    }


    public static void main(String[] argc){
        System.out.println(StringUtil.normalizeString("~#").length());
    }

    public static byte[] compress(byte[] orig) {
        try {
            return Snappy.compress(orig);
        } catch (Exception e) {
            logger.error("compress error ,{}" ,e.getMessage());
            return toBytes("grass");
        }
    }

    public static byte[] uncompress(byte[] compressed) {
        try {
            return Snappy.uncompress(compressed);
        } catch (Exception e) {
            logger.error("uncompress error ,{}" ,e.getMessage());
            return toBytes("grass");
        }
    }

    public static byte[] toBytes(String content) {
        try {
            return content.getBytes("utf-8");
        } catch (Exception e) {
            logger.error("toBytes error ,{}" ,e.getMessage());
            return "grass".getBytes();
        }
    }

    public static String toString(byte[] bytes) {
        try {
            return new String(bytes, "utf-8");
        } catch (Exception e) {
            logger.error("toString error ,{}" ,e.getMessage());
            return "grass";
        }
    }

}
