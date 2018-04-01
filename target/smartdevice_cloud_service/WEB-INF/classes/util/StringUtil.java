package util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xerial.snappy.Snappy;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class StringUtil {
    private static final Logger logger = LoggerFactory.getLogger(StringUtil.class);


    /**
     * 获取String的MD5值
     *
     * @param info 字符串
     * @return 该字符串的MD5值
     */
    public static String getMD5(String info) {
        try {
            //获取 MessageDigest 对象，参数为 MD5 字符串，表示这是一个 MD5 算法（其他还有 SHA1 算法等）：
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            //update(byte[])方法，输入原数据
            //类似StringBuilder对象的append()方法，追加模式，属于一个累计更改的过程
            md5.update(info.getBytes("UTF-8"));
            //digest()被调用后,MessageDigest对象就被重置，即不能连续再次调用该方法计算原数据的MD5值。可以手动调用reset()方法重置输入源。
            //digest()返回值16位长度的哈希值，由byte[]承接
            byte[] md5Array = md5.digest();
            //byte[]通常我们会转化为十六进制的32位长度的字符串来使用,本文会介绍三种常用的转换方法
            return bytesToHex1(md5Array);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    private static String bytesToHex1(byte[] md5Array) {
        StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < md5Array.length; i++) {
            int temp = 0xff & md5Array[i];//TODO:此处为什么添加 0xff & ？
            String hexString = Integer.toHexString(temp);
            if (hexString.length() == 1) {//如果是十六进制的0f，默认只显示f，此时要补上0
                strBuilder.append("0").append(hexString);
            } else {
                strBuilder.append(hexString);
            }
        }
        return strBuilder.toString();
    }

    //通过java提供的BigInteger 完成byte->HexString
    private static String bytesToHex2(byte[] md5Array) {
        BigInteger bigInt = new BigInteger(1, md5Array);
        return bigInt.toString(16);
    }

    //通过位运算 将字节数组到十六进制的转换
    public static String bytesToHex3(byte[] byteArray) {
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] resultCharArray = new char[byteArray.length * 2];
        int index = 0;
        for (byte b : byteArray) {
            resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
            resultCharArray[index++] = hexDigits[b & 0xf];
        }
        return new String(resultCharArray);
    }


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

    public static void main(String[] argc){
        while(true){
            System.out.println(getMD5("{'name_space':'AccountManagement1','name':'AddUser','userName':'barry test','userPasswd':'test123','userEmail':'404414244@qq.com','RegisterCode':'865395'}"));
            System.out.println(getMD5("{'name_space':'AccountManagement2','name':'AddUser','userName':'barry test','userPasswd':'test123','userEmail':'404414244@qq.com','RegisterCode':'865395'}"));
            System.out.println(getMD5("{'name_space':'AccountManagement3','name':'AddUser','userName':'barry test','userPasswd':'test123','userEmail':'404414244@qq.com','RegisterCode':'865395'}"));

        }
    }

}
