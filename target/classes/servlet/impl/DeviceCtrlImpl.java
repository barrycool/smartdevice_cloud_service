package servlet.impl;

import client.KafkaClient;
import client.MongoXClient;
import client.RedisClient;
import client.RedisFactory;
import com.alibaba.fastjson.JSONObject;
import device.DeviceFactory;
import util.Global;

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


    public String strCatUserdeviceOpenStatusRedisKey(JSONObject jsonReq){
        String userId = jsonReq.getString("user_id");
        String deviceId = jsonReq.getString(Global.deviceId);
        if(userId==null || deviceId==null){
            return "";
        }
        return Global.userDeviceKey + userId + ":" +  deviceId;
    }


    private String verifyUserdeviceOpenStatus(JSONObject jsonReq){

        String status = "0";
        String redisKey = strCatUserdeviceOpenStatusRedisKey(jsonReq);
        if(redisClient!=null) {
            status =  redisClient.get(redisKey);
        }
        if(status==null){
            status = "0";
        }
        return status;
    }

    public JSONObject process(JSONObject jsonReq){

        String status = verifyUserdeviceOpenStatus(jsonReq);

        if(status.equals("1")){
            String device = jsonReq.getString(Global.deviceId);
            String cmd = jsonReq.getString("cmd");
            return deviceFactory.getDeviceCtrlCmd(device, cmd);
        }
        return deviceClosedStatus;
    }

    public void set(JSONObject jsonReq){
        String status = jsonReq.getString("status");
        String redisKey = strCatUserdeviceOpenStatusRedisKey(jsonReq);
        KafkaClient.setDeviceStatus(redisKey, status, Global.defaultOverTime);
    }

    public JSONObject get(JSONObject jsonReq){
        String status = verifyUserdeviceOpenStatus(jsonReq);
        if(status.equals("1")){
            return deviceOpenStatus;
        }
        return deviceClosedStatus;
    }

    public static void main(String[] argc){
        DeviceCtrlImpl deviceCtrl = new DeviceCtrlImpl();
        JSONObject jsonReq = new JSONObject();
        jsonReq.put("user_id", "Fy4TG6j");
        jsonReq.put(Global.deviceId, "tv");
//        jsonReq.put("status", "1");

//        deviceCtrl.set(jsonReq);

        while(true){
            System.out.println(deviceCtrl.get(jsonReq));
        }
    }


}
