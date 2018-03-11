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


    private String verifyUserDeviceStatus(JSONObject jsonReq){
        String userId = jsonReq.getString("user_id");
        String device = jsonReq.getString("device");
        String redisKey = Global.userDeviceKey + userId + ":" +  device;
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
        String userId = jsonReq.getString("user_id");
        String device = jsonReq.getString("device");
        String status = jsonReq.getString("status");
        String redisKey = Global.userDeviceKey + userId + ":" +  device;
        KafkaClient.setDeviceStatus(redisKey, status, Global.defaultOverTime);
    }


}
