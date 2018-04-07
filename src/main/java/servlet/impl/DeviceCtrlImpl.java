package servlet.impl;

import client.RedisTools;
import client.RedisClient;
import client.RedisFactory;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import device.DeviceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ConstKey;
import util.RedisUtil;
import util.StringUtil;

import java.util.*;

/**
 * Created by fanyuanyuan on 2018/3/10.
 */
public class DeviceCtrlImpl {
    private static final Logger logger = LoggerFactory.getLogger(DeviceCtrlImpl.class);

//    private MongoXClient mongoXClient = MongoXClient.getInstance();

    private DeviceFactory deviceFactory = DeviceFactory.getInstance();

    private static JSONObject deviceOpenStatus = new JSONObject();
    private static JSONObject deviceClosedStatus = new JSONObject();
    static{
        deviceOpenStatus.put("status", "ON");
        deviceClosedStatus.put("status", "OFF");
    }



    public static Map<String, String> mapCtrl = new HashMap<String, String>();

    static {
        mapCtrl.put("TurnOn", "ON");
        mapCtrl.put("TurnOff", "OFF");
        mapCtrl.put("ON", "TurnOn");
        mapCtrl.put("OFF", "TurnOff");
    }

    public String convertCtrlName(String name){
        String v = mapCtrl.get(name);
        if(StringUtil.isEmpty(v)){
            v = "invalid";
        }
        return v;
    }

    public JSONObject setQueryResult(){
        return new JSONObject();
    }

    public JSONObject setDevStatus(JSONObject jsonReq){
        String name = jsonReq.getString(ConstKey.name);
        String ctrlValue = convertCtrlName(name);
        String healthValue = "OK";
        String redisKey = RedisUtil.getRedisKey_DevStatus(jsonReq);
        String status = RedisTools.set(redisKey, ctrlValue, ConstKey.user_device_status_over_time);
        logger.error("setStatus, device_id={}, status={}", redisKey, status);
        JSONObject jsonCtrl = new JSONObject();
        jsonCtrl.put(ConstKey.name, "powerState");
        jsonCtrl.put(ConstKey.value, ctrlValue);
        jsonCtrl.put(ConstKey.nameSpace, "Alexa.PowerController");

        JSONObject jsonHealth = new JSONObject();
        jsonHealth.put(ConstKey.name, "connectivity");
        if(!"OK".equals(status)){
            jsonCtrl.put(ConstKey.value, "OFF");
            healthValue = "UNREACHABLE";
        }
        jsonHealth.put(ConstKey.value, healthValue);
        jsonHealth.put(ConstKey.nameSpace, "Alexa.EndpointHealth");

        JSONArray jsonProperties = new JSONArray();
        jsonProperties.add(jsonCtrl);
        jsonProperties.add(jsonHealth);

        JSONObject jsonResult = new JSONObject();
        jsonResult.put(ConstKey.deviceId, jsonReq.getString(ConstKey.deviceId));
        jsonResult.put(ConstKey.nameSpace, "Alexa");
        jsonResult.put(ConstKey.name, "Response");
        jsonResult.put(ConstKey.properties, jsonProperties);

        return jsonResult;
    }

    public JSONObject getDevStatus(JSONObject jsonReq){
        JSONObject jsonCtrl = new JSONObject();

        String redisKey = RedisUtil.getRedisKey_DevStatus(jsonReq);
        String redisValue = RedisUtil.getRedisValue(redisKey);
        logger.error("getStatus, device_id={}, status={}", redisKey, redisValue);
        String healthValue = "OK";
        jsonCtrl.put(ConstKey.value, redisValue);
        if(StringUtil.isEmpty(redisValue)){
            jsonCtrl.put(ConstKey.value, "OFF");
            healthValue = "UNREACHABLE";
        }
        jsonCtrl.put(ConstKey.name, jsonReq.getString(ConstKey.name));
        jsonCtrl.put(ConstKey.nameSpace, "Alexa.PowerController");

        JSONObject jsonHealth = new JSONObject();
        jsonHealth.put(ConstKey.name, "connectivity");
        jsonHealth.put(ConstKey.value, healthValue);
        jsonHealth.put(ConstKey.nameSpace, "Alexa.EndpointHealth");

        JSONArray jsonProperties = new JSONArray();
        jsonProperties.add(jsonCtrl);
        jsonProperties.add(jsonHealth);

        JSONObject jsonResult = new JSONObject();
        jsonResult.put(ConstKey.deviceId, jsonReq.getString(ConstKey.deviceId));
        jsonResult.put(ConstKey.nameSpace, "Alexa");
        jsonResult.put(ConstKey.name, "Response");
        jsonResult.put(ConstKey.properties, jsonProperties);

        return jsonResult;
    }

    public static void main(String[] argc){
        DeviceCtrlImpl deviceCtrl = new DeviceCtrlImpl();
        JSONObject jsonReq = new JSONObject();
        jsonReq.put(ConstKey.deviceId, "12321");
        jsonReq.put("status", "0");

        deviceCtrl.setDevStatus(jsonReq);
        System.out.println(deviceCtrl.getDevStatus(jsonReq));
    }


}
