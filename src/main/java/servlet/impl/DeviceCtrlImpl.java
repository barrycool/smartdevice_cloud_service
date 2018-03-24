package servlet.impl;

import client.RedisTools;
import client.RedisClient;
import client.RedisFactory;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import device.DeviceFactory;
import util.ConstKey;
import util.Global;
import util.StringUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by fanyuanyuan on 2018/3/10.
 */
public class DeviceCtrlImpl {

//    private MongoXClient mongoXClient = MongoXClient.getInstance();
    private RedisClient redisClient = RedisFactory.getClient(RedisFactory.RedisKey.APP);

    private DeviceFactory deviceFactory = DeviceFactory.getInstance();


    private static JSONObject deviceOpenStatus = new JSONObject();
    private static JSONObject deviceClosedStatus = new JSONObject();
    static{
        deviceOpenStatus.put("status", "on");
        deviceClosedStatus.put("status", "off");
    }


    public String getRedisValueKey4DevStatus(JSONObject jsonReq){
        String userId = getUserId(jsonReq);
        String deviceId = jsonReq.getString(ConstKey.deviceId);
        if(userId==null || deviceId==null || userId.length()==0 || deviceId.length()==0){
            return null;
        }
        return ConstKey.redis_key_prefix_user_device_status + userId + ":" +  deviceId;
    }

    public String getRedisValueKey4UserToken(JSONObject jsonReq){
        String token = jsonReq.getString(ConstKey.token);
        if(StringUtil.isEmpty(token)){
            return null;
        }
        return ConstKey.redis_key_prefix_user_token +  token;
    }

    public String getRedisValueKey4DevList(JSONObject jsonReq){
        String userId = getUserId(jsonReq);
        String deviceId = jsonReq.getString(ConstKey.deviceId);
        if(userId==null || deviceId==null || userId.length()==0 || deviceId.length()==0){
            return null;
        }
        return ConstKey.redis_key_prefix_user_device_list + userId + ":";
    }

    private String getRedisValue(String redisKey){
        if(StringUtil.isEmpty(redisKey)){
            return null;
        }
        if(redisClient==null){
            return null;
        }

        return redisClient.get(redisKey);
    }

    public JSONObject setDevStatus(JSONObject jsonReq){
        String name = jsonReq.getString(ConstKey.name);
        String redisKey = getRedisValueKey4DevStatus(jsonReq);

        JSONObject jsonResult = new JSONObject();
        String deviceId = jsonReq.getString(ConstKey.deviceId);
        jsonResult.put(ConstKey.deviceId, deviceId);
        jsonResult.put(ConstKey.name, "Response");
        jsonResult.put(ConstKey.nameSpace, "Alexa");
        if(StringUtil.isEmpty(redisKey)){
            return jsonResult;
        }
        RedisTools.set(redisKey, name, ConstKey.user_device_status_over_time);

        return jsonResult;
    }

    public JSONObject getDevStatus(JSONObject jsonReq){
        String redisKey = getRedisValueKey4DevStatus(jsonReq);
        String redisValue = getRedisValue(redisKey);

        JSONObject jsonCtrl = new JSONObject();
        jsonCtrl.put(ConstKey.name, jsonReq.getString(ConstKey.name));
        jsonCtrl.put(ConstKey.value, redisValue);
        jsonCtrl.put(ConstKey.nameSpace, "Alexa.PowerController");

        JSONObject jsonHealth = new JSONObject();
        jsonHealth.put(ConstKey.name, "connectivity");
        jsonHealth.put(ConstKey.value, redisValue);
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

    public void setUserToken(JSONObject jsonReq){
        String userId = getUserId(jsonReq);
        String redisKey = getRedisValueKey4UserToken(jsonReq);
        if(StringUtil.isEmpty(userId) || StringUtil.isEmpty(redisKey)){
            return;
        }
        RedisTools.set(redisKey, userId, ConstKey.user_token_over_time);
    }

    public String getUserId(JSONObject jsonReq){
        String redisKey = getRedisValueKey4UserToken(jsonReq);
        return getRedisValue(redisKey);
    }

    public String addDevice(String redisValue, JSONObject jsonReq){
        if(jsonReq==null){
            return redisValue;
        }
        String deviceId = jsonReq.getString(ConstKey.deviceId);
        String deviecType = jsonReq.getString(ConstKey.deviecType);
        String friendlyName = jsonReq.getString(ConstKey.friendlyName);
        String manufacturerName = jsonReq.getString(ConstKey.manufacturerName);

        if(StringUtil.isEmpty(deviceId) || StringUtil.isEmpty(deviecType) ||
                StringUtil.isEmpty(friendlyName) || StringUtil.isEmpty(manufacturerName)){
            return redisValue;
        }

        String info = deviceId + ":" + deviecType + ":" + friendlyName + ":" + manufacturerName;
        Set<String> set = new HashSet<String>(Arrays.asList(redisValue.split(",")));
        if(set.contains(info)){
            return redisValue;
        }
        return redisValue + "," + info;
    }

    public JSONObject addDevice(JSONObject jsonReq){
        String redisKey = getRedisValueKey4DevList(jsonReq);
        if(redisKey==null || redisKey.length()==0){
            return null;
        }

        String redisValue = redisClient.get(ConstKey.redis_key_prefix_user_device_list);
        String v = addDevice(redisValue, jsonReq);
        if(!StringUtil.isEmpty(v)){
            RedisTools.set(redisKey, v, ConstKey.user_device_list_over_time);
        }

        JSONObject jsonResult = new JSONObject();
        return jsonResult;
    }



    public JSONObject getDevList(JSONObject jsonReq){
        String redisKey = getRedisValueKey4DevList(jsonReq);
        String redisValue = getRedisValue(redisKey);
        JSONArray jsonDevList = new JSONArray();
        if(StringUtil.isEmpty(redisValue)){
            String[] items = redisValue.split(",");
            for(String item : items){
                String[] infos = item.split(":");
                if(infos.length!=4){
                    continue;
                }
                JSONObject jsonDev = new JSONObject();
                jsonDev.put(ConstKey.deviceId, infos[0]);
                jsonDev.put(ConstKey.deviecType, infos[1]);
                jsonDev.put(ConstKey.friendlyName, infos[2]);
                jsonDev.put(ConstKey.manufacturerName, infos[3]);
                jsonDevList.add(jsonDev);
            }
        }

        JSONObject jsonResult = new JSONObject();
        jsonResult.put(ConstKey.name, "Discover.Response");
        jsonResult.put(ConstKey.nameSpace, "Alexa.Discovery");
        jsonResult.put(ConstKey.devices, jsonDevList);
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
