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
        deviceOpenStatus.put("status", "off");
        deviceClosedStatus.put("status", "on");
    }


    public String strCatUserdeviceOpenStatusRedisKey(JSONObject jsonReq){
        String userId = jsonReq.getString("user_id");
        String device = jsonReq.getString("device");
        if(userId==null || device==null){
            return "";
        }
        return Global.userDeviceKey + userId + ":" +  device;
    }


    private String verifyUserdeviceOpenStatus(JSONObject jsonReq){

        String redisKey = strCatUserdeviceOpenStatusRedisKey(jsonReq);
        if(redisClient!=null) {
            return redisClient.get(redisKey);
        }
        return "1";
    }

    public JSONObject process(JSONObject jsonReq){

        String status = verifyUserdeviceOpenStatus(jsonReq);

        if(status!=null && status.equals("1")){
            String device = jsonReq.getString("device");
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
        if(status!=null && status.equals("1")){
            return deviceOpenStatus;
        }
        return deviceClosedStatus;
    }


}
