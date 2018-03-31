package util;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by fanyuanyuan on 2018/3/24.
 */
public class UtilJson {

    public static void add(String k, String v, JSONObject json){
        if(json==null || StringUtil.isEmpty(k)|| StringUtil.isEmpty(v)){
            return;
        }
        json.put(k, v);
    }



    public static JSONObject getCtrlResp(JSONObject jsonReq, JSONObject jsonResult){
        JSONObject jsonResp = new JSONObject();
        String deviceId = jsonReq.getString(ConstKey.deviceId);
        String nameSpace = jsonReq.getString(ConstKey.nameSpace);
        String name = jsonReq.getString(ConstKey.name);
        add(ConstKey.deviceId, deviceId, jsonResp);
        add(ConstKey.nameSpace, nameSpace, jsonResp);
        add(ConstKey.name, name, jsonResp);

        JSONObject jsonProp = new JSONObject();
//        jsonResp.put(ConstKey.properties, jsonProp);

        return jsonResp;


    }
}
