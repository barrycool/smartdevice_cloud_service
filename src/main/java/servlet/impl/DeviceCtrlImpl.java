package servlet.impl;

import client.RedisTools;
import client.RedisClient;
import client.RedisFactory;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import device.DeviceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import servlet.test.TcpServer;
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

//    private Map<String, MyAsyncHandler> mapHandler = new HashMap<>();

    private TcpServer tcpServer =  TcpServer.getTcpServer();


    private static JSONObject deviceOpenStatus = new JSONObject();
    private static JSONObject deviceClosedStatus = new JSONObject();


    private static DeviceCtrlImpl deviceCtrl = new DeviceCtrlImpl();

    public static DeviceCtrlImpl getInstance(){
        return deviceCtrl;
    }

    private DeviceCtrlImpl(){}

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

    public boolean getDevConnectStatus(JSONObject jsonReq){
        String redisKey = RedisUtil.getRedisKey_DevConnectStatus(jsonReq);
        String redisValue = RedisTools.get(redisKey);
        if(StringUtil.isEmpty(redisValue) || "0".equals(redisValue)){
            return false;
        }
        return true;
    }

    public boolean setDevConnectStatus(JSONObject jsonReq){
        String redisKey = RedisUtil.getRedisKey_DevConnectStatus(jsonReq);
        String redisValue = RedisTools.get(redisKey);
        if(StringUtil.isEmpty(redisValue) || "0".equals(redisValue)){
            return false;
        }
        String status = RedisTools.set(redisKey, redisValue, ConstKey.user_device_connect_status_over_time);
        if(!"OK".equals(status)){
            return false;
        }
        return true;
    }

    public JSONObject setDevStatus(JSONObject jsonReq){
        String healthValue = "OK";
        JSONObject jsonResponse = new JSONObject();

        String deviceId = jsonReq.getString(ConstKey.deviceId);
        String redisKey_connect = RedisUtil.getRedisKey_DevConnectStatus(deviceId);

        String connectStatus = RedisTools.get(redisKey_connect);
        if(StringUtil.isEmpty(connectStatus) || connectStatus.equals(ConstKey.offLine)){
            healthValue = "UNREACHABLE";
        }else{
            jsonResponse = tcpServer.onEvent(jsonReq);
            if(jsonResponse==null || jsonResponse.size()==0){
                healthValue = "UNREACHABLE";
            }
        }

        JSONObject jsonHealth = new JSONObject();
        jsonHealth.put(ConstKey.name, "connectivity");

        jsonHealth.put(ConstKey.value, healthValue);
        jsonHealth.put(ConstKey.nameSpace, "Alexa.EndpointHealth");


        JSONObject jsonResult = new JSONObject();
        jsonResult.put(ConstKey.deviceId, jsonReq.getString(ConstKey.deviceId));
        jsonResult.put(ConstKey.nameSpace, "Alexa");
        jsonResult.put(ConstKey.name, "Response");
        jsonResult.fluentPutAll(jsonResponse);

        return jsonResult;
    }

    public JSONObject getDevStatus(JSONObject jsonReq){
        JSONObject jsonResponse = new JSONObject();
        String deviceId = jsonReq.getString(ConstKey.deviceId);
        String redisKey_connect = RedisUtil.getRedisKey_DevConnectStatus(deviceId);
        String healthValue = "OK";

        String connectStatus = RedisTools.get(redisKey_connect);
        if(StringUtil.isEmpty(connectStatus) || connectStatus.equals(ConstKey.offLine)){
            healthValue = "UNREACHABLE";
        }else{
            jsonResponse = tcpServer.onEvent_getSwitch(jsonReq);
            if(jsonResponse==null || jsonResponse.size()==0){
                healthValue = "UNREACHABLE";
            }
        }
        JSONObject jsonHealth = new JSONObject();
        jsonHealth.put(ConstKey.name, "connectivity");
        jsonHealth.put(ConstKey.value, healthValue);
        jsonHealth.put(ConstKey.nameSpace, "Alexa.EndpointHealth");

        JSONArray jsonProperties = new JSONArray();
        jsonProperties.add(jsonResponse);
        jsonProperties.add(jsonHealth);

        JSONObject jsonResult = new JSONObject();
        jsonResult.put(ConstKey.deviceId, deviceId);
        jsonResult.put(ConstKey.nameSpace, "Alexa");
        jsonResult.put(ConstKey.name, "Response");
        jsonResult.put(ConstKey.properties, jsonProperties);

        return jsonResult;
    }

//    public JSONObject getDevStatus(JSONObject jsonReq){
//        JSONObject jsonCtrl = new JSONObject();
//        String redisKey = RedisUtil.getRedisKey_DevStatus(jsonReq);
//        String redisValue = RedisUtil.getRedisValue(redisKey);
//        String healthValue = "OK";
//        jsonCtrl.put(ConstKey.value, redisValue);
//        if(StringUtil.isEmpty(redisValue)){
//            jsonCtrl.put(ConstKey.value, "OFF");
//            healthValue = "UNREACHABLE";
//        }
//        jsonCtrl.put(ConstKey.name, jsonReq.getString(ConstKey.name));
//        jsonCtrl.put(ConstKey.nameSpace, "Alexa.PowerController");
//
//        JSONObject jsonHealth = new JSONObject();
//        jsonHealth.put(ConstKey.name, "connectivity");
//        jsonHealth.put(ConstKey.value, healthValue);
//        jsonHealth.put(ConstKey.nameSpace, "Alexa.EndpointHealth");
//
//        JSONArray jsonProperties = new JSONArray();
//        jsonProperties.add(jsonCtrl);
//        jsonProperties.add(jsonHealth);
//
//        JSONObject jsonResult = new JSONObject();
//        jsonResult.put(ConstKey.deviceId, jsonReq.getString(ConstKey.deviceId));
//        jsonResult.put(ConstKey.nameSpace, "Alexa");
//        jsonResult.put(ConstKey.name, "Response");
//        jsonResult.put(ConstKey.properties, jsonProperties);
//
//        return jsonResult;
//    }

    public static void main(String[] argc){
        DeviceCtrlImpl deviceCtrl = new DeviceCtrlImpl();
        JSONObject jsonReq = new JSONObject();
        jsonReq.put(ConstKey.deviceId, "12321");
        jsonReq.put("status", "0");

        deviceCtrl.setDevStatus(jsonReq);
        System.out.println(deviceCtrl.getDevStatus(jsonReq));
    }


}
