package servlet;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.StringUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuanyuanfan on 2018/1/23.
 */
public class QueryPack {
    private static final Logger logger = LoggerFactory.getLogger(QueryPack.class);

    private static Map<String, String> parseURL(String url){
        if(url==null || url.length()==0){
            return null;
        }
        Map<String, String> mapURL = new HashMap<String, String>();
        String[] items = url.split("&");
        for(String item : items){
            String[] kv = item.split("=");
            if(kv.length!=2){
                continue;
            }
            mapURL.put(kv[0], kv[1]);
        }
        return mapURL;
    }

    public static void setDefaultParam(JSONObject jsonReq, String para, String defaultValue){
        String v = jsonReq.getString(para);
        if(v==null){
            jsonReq.put(para, defaultValue);
        }
    }




    private static JSONObject parseParam(String url){
        if(url==null || url.length()==0){
            return null;
        }
        JSONObject jsonReq = new JSONObject();
        String[] items = url.split("&");
        for(String item : items){
            String[] kv = item.split("=");
            if(kv.length!=2){
                continue;
            }
            jsonReq.put(kv[0], kv[1]);
        }
        return jsonReq;
    }



    public static JSONObject queryPack(HttpServletRequest request)
            throws ServletException, IOException {
        try {
            JSONObject jsonReq = parseParam(request.getQueryString());

            return jsonReq;
        } catch (Exception e) {
            logger.error("request failed, error={}", e);
            return null;
        }
    }
}
