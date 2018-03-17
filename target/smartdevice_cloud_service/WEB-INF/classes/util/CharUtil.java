package util;

import java.util.regex.Pattern;

public class CharUtil {
    public static boolean isNumEn(char c) {
        if (c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c == 32) {
            return true;
        }
        return false;
    }

    public static boolean isNumEn(String in){
        for(int i=0; i<in.length(); ++i){
            if(!isNumEn(in.charAt(i))){
                return false;
            }
        }
        return true;
    }

    // 根据Unicode编码完美的判断中文汉字和符号
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
//            || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
//            || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                ) {
            return true;
        }
        return false;
    }

    // 完整的判断中文汉字和符号
    public static boolean isChinese(String strName) {
        char[] ch = strName.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (!isChinese(c)) {
                return false;
            }
        }
        return true;
    }

    // 只能判断部分CJK字符（CJK统一汉字）
    public static boolean isChineseByREG(String str) {
        if (str == null) {
            return false;
        }
        Pattern pattern = Pattern.compile("[\\u4E00-\\u9FBF]+");
        return pattern.matcher(str.trim()).find();
    }
}