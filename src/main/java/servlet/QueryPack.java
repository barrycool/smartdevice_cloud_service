package servlet;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ConstKey;
import util.Global;
import util.StringUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuanyuanfan on 2018/1/23.
 */
public class QueryPack {
    private static final Logger logger = LoggerFactory.getLogger(QueryPack.class);

    private static Map<String, String> parseURL(String url) {
        if (url == null || url.length() == 0) {
            return null;
        }
        Map<String, String> mapURL = new HashMap<String, String>();
        String[] items = url.split("&");
        for (String item : items) {
            String[] kv = item.split("=");
            if (kv.length != 2) {
                continue;
            }
            mapURL.put(kv[0], kv[1]);
        }
        return mapURL;
    }

    public static void setDefaultParam(JSONObject jsonReq, String para, String defaultValue) {
        String v = jsonReq.getString(para);
        if (v == null) {
            jsonReq.put(para, defaultValue);
        }
    }

    public static String toString(HttpServletRequest request) throws IOException, JSONException {
        StringBuffer sb = new StringBuffer();
        InputStream is = request.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is, Global.ENCODE));
        String s;
        while ((s = br.readLine()) != null) {
            sb.append(s);
        }
        return sb.toString();
    }


    private static JSONObject parseParam(String url) {
        if (url == null || url.length() == 0) {
            return null;
        }
        JSONObject jsonReq = new JSONObject();
        String[] items = url.split("&");
        for (String item : items) {
            String[] kv = item.split("=");
            if (kv.length != 2) {
                continue;
            }
            jsonReq.put(kv[0], kv[1]);
        }
        return jsonReq;
    }

    public static JSONObject postQueryPack(HttpServletRequest request)
            throws ServletException, IOException {
        JSONObject jsonReq = new JSONObject();
        try {
            String ctx = toString(request);
            if(StringUtil.isEmpty(ctx)){
                return jsonReq;
            }
            jsonReq = JSON.parseObject(ctx);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("request failed, ERROR={}", e);
        }
        return jsonReq;
    }

    public static JSONObject postQueryPack(String in){
        JSONObject jsonReq = new JSONObject();
        try {
            if(StringUtil.isEmpty(in)){
                return jsonReq;
            }
            jsonReq = JSON.parseObject(in);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("request failed, ERROR={}", e);
        }
        return jsonReq;
    }

    public static JSONObject queryPack(HttpServletRequest request)
            throws ServletException, IOException {
        try {
            JSONObject jsonReq = parseParam(request.getQueryString());
            setDefaultParam(jsonReq, ConstKey.deviceId, "");
            setDefaultParam(jsonReq, ConstKey.name, "");
            setDefaultParam(jsonReq, ConstKey.nameSpace, "");
            setDefaultParam(jsonReq, ConstKey.token, "");

            return jsonReq;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("request failed, error={}", e);
            return null;
        }
    }
}
