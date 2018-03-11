package servlet;

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


    private static JSONObject deviceStatus = new JSONObject();
    static{
        deviceStatus.put("status", "off");
    }


    public String strCatUserDeviceStatusRedisKey(JSONObject jsonReq){
        String userId = jsonReq.getString("user_id");
        String device = jsonReq.getString("device");
        if(userId==null || device==null){
            return "";
        }
        return Global.userDeviceKey + userId + ":" +  device;
    }


    private String verifyUserDeviceStatus(JSONObject jsonReq){

        String redisKey = strCatUserDeviceStatusRedisKey(jsonReq);
        if(redisClient!=null) {
            return redisClient.get(redisKey);
        }
        return "1";
    }

    public JSONObject process(JSONObject jsonReq){

        String status = verifyUserDeviceStatus(jsonReq);

        if(status!=null && status.equals("1")){
            String device = jsonReq.getString("device");
            String cmd = jsonReq.getString("cmd");
            return deviceFactory.getDeviceCtrlCmd(device, cmd);
        }
        return deviceStatus;
    }

    public void set(JSONObject jsonReq){
        String status = jsonReq.getString("status");
        String redisKey = strCatUserDeviceStatusRedisKey(jsonReq);
        KafkaClient.setDeviceStatus(redisKey, status, Global.defaultOverTime);
    }


}
