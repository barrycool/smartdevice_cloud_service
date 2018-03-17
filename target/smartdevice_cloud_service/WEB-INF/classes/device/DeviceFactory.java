package device;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by fanyuanyuan on 2018/3/10.
 */
public  class DeviceFactory {

    private Map<String, JSONObject> deviceCtrlMap = new HashMap<>();

    private DeviceFactory(){
        initDeviceFactory();
    }

    private String deviceConfig = "/device.properties";

    private void initDeviceFactory(){
        try {
            InputStream in = DeviceFactory.class.getResourceAsStream(deviceConfig);
            Properties p = new Properties();
            p.load(in);
            for (Map.Entry<Object, Object> entry : p.entrySet()) {
                String device = (String) entry.getKey();
                String v = (String) entry.getValue();
                JSONObject jsonCtrl = JSON.parseObject(v);
                deviceCtrlMap.put(device, jsonCtrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static DeviceFactory deviceFactory = new DeviceFactory();

    public static DeviceFactory getInstance(){
        return deviceFactory;

    }


    public static JSONObject notExistDeviceObj = new JSONObject();
    public static JSONObject errorCtrlCmd = new JSONObject();
    static {
        notExistDeviceObj.put("info", "device not exist");
        notExistDeviceObj.put("status", "-1");
        errorCtrlCmd.put("info", "command error");
        errorCtrlCmd.put("status", "-1");
    }

    public JSONObject getDeviceCtrlCmd(String device, String cmd){
        JSONObject jsonDevice = deviceCtrlMap.get(device);
        if(jsonDevice==null){
            return notExistDeviceObj;
        }

        JSONObject jsonCtrl = jsonDevice.getJSONObject(cmd);
        if(jsonCtrl==null){
            return errorCtrlCmd;
        }
        return jsonCtrl;
    }

    public  JSONObject on(){
        JSONObject jsonResult = new JSONObject();
        jsonResult.put("status", "1");
        return jsonResult;
    }
    public  JSONObject off(){
        JSONObject jsonResult = new JSONObject();
        jsonResult.put("status", "0");
        return jsonResult;
    }

    public static void main(String[] argc){
        DeviceFactory deviceFactory = DeviceFactory.getInstance();
    }
}
