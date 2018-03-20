package servlet.impl;

import client.RedisTools;
import client.RedisClient;
import client.RedisFactory;
import com.alibaba.fastjson.JSONObject;
import device.DeviceFactory;
import util.UtilKey;

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


    public String getUserDeviceRedisKey(JSONObject jsonReq){
        String userId = jsonReq.getString(UtilKey.userId);
        String deviceId = jsonReq.getString(UtilKey.deviceId);
        if(userId==null || deviceId==null || userId.length()==0 || deviceId.length()==0){
            return null;
        }
        return UtilKey.redis_key_prefix_user_device_status + userId + ":" +  deviceId;
    }


    private String getUserDeviceStatus(JSONObject jsonReq){

        String status = "0";
        String redisKey = getUserDeviceRedisKey(jsonReq);
        if(redisClient!=null || redisKey!=null) {
            status =  redisClient.get(redisKey);
        }
        if(status==null){
            status = "0";
        }
        return status;
    }

    public JSONObject process(JSONObject jsonReq){

        String status = getUserDeviceStatus(jsonReq);

        if(status.equals("1")){
            String device = jsonReq.getString(UtilKey.deviceId);
            String cmd = jsonReq.getString("cmd");
            return deviceFactory.getDeviceCtrlCmd(device, cmd);
        }
        return deviceClosedStatus;
    }

    public void setDeviceStatus(JSONObject jsonReq){
        String status = jsonReq.getString("status");
        String redisKey = getUserDeviceRedisKey(jsonReq);
        if(redisKey==null || redisKey.length()==0){
            return;
        }
        RedisTools.set(redisKey, status, UtilKey.user_device_status_over_time);
    }

    public void setDeviceList(JSONObject jsonReq){
        String status = jsonReq.getString("status");
        String redisKey = getUserDeviceRedisKey(jsonReq);
        if(redisKey==null || redisKey.length()==0){
            return;
        }
        RedisTools.set(redisKey, status, UtilKey.user_device_list_over_time);
    }

    public JSONObject get(JSONObject jsonReq){
        String status = getUserDeviceStatus(jsonReq);
        if(status.equals("1")){
            return deviceOpenStatus;
        }
        return deviceClosedStatus;
    }

    public static void main(String[] argc){
        DeviceCtrlImpl deviceCtrl = new DeviceCtrlImpl();
        JSONObject jsonReq = new JSONObject();
        jsonReq.put(UtilKey.deviceId, "12321");
        jsonReq.put("status", "0");

        deviceCtrl.setDeviceStatus(jsonReq);
        System.out.println(deviceCtrl.get(jsonReq));
    }


}
