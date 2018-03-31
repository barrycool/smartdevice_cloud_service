package servlet.impl;

import client.MongoXClient;
import client.RedisTools;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ConstKey;
import util.MailSender;
import util.RedisUtil;
import util.StringUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Arrays.asList;

/**
 * Created by fanyuanyuan on 2018/3/17.
 */
public class UserCtrlImpl {

    private static final Logger logger = LoggerFactory.getLogger(UserCtrlImpl.class);


    private ConcurrentHashMap<String, String> sendUserCodeMap = new ConcurrentHashMap<>();

    private MongoXClient mongoXClient = MongoXClient.getInstance();

    public JSONObject sendCode(JSONObject jsonReq) {
        JSONObject queryResult = new JSONObject();
        queryResult.put(ConstKey.nameSpace, jsonReq.getString(ConstKey.nameSpace));
        queryResult.put(ConstKey.name, "requestRegisterCode.Response");
        JSONObject jsonResult = new JSONObject();
        jsonResult.put(ConstKey.code, "failed");
        jsonResult.put(ConstKey.msg, "send register code failed");
        String userMailAddr = jsonReq.getString(ConstKey.userAccount);

        if(StringUtil.isEmpty(userMailAddr)){
            queryResult.put(ConstKey.result, jsonResult);
            return queryResult;
        }


        String code = MailSender.sendEmail(userMailAddr);
        if (code != null) {
            jsonResult.put(ConstKey.code, "OK");
            jsonResult.put(ConstKey.msg, "send register code successfully");
            queryResult.put(ConstKey.result, jsonResult);
            sendUserCodeMap.put(userMailAddr, code);
        }
        return queryResult;
    }

    public JSONObject userCtrl(JSONObject jsonReq){

        JSONObject queryResult = new JSONObject();
        String name = jsonReq.getString(ConstKey.name);
        switch (name){
            case "requestRegisterCode":
                queryResult = sendCode(jsonReq);
                break;
            case "AddUser":
//                queryResult = addUser(jsonReq);
                break;
            default:
                break;
        }
        return queryResult;
    }

    public boolean verify(String userMailAddr, String code) {
        String hist = sendUserCodeMap.get(userMailAddr);
        if (hist == null || code == null || !hist.equals(code)) {
            return false;
        }
        return true;
    }


    public JSONObject get(String m2, String... fieldNames) {
        JSONObject jsonResult = new JSONObject();

        MongoCursor<Document> cur = mongoXClient.findIterByM2(m2, fieldNames);
        if (cur == null) {
            return jsonResult;
        }
        while (cur.hasNext()) {
            Document doc = cur.next();
            insertQueryResult(doc, jsonResult, fieldNames);
        }

        return jsonResult;
    }

    private void insertQueryResult(Document doc, JSONObject jsonResult, String... fieldNames) {
        List<String> names = asList(fieldNames);
        for (String name : names) {
            String info = doc.getString(name);
            if (info != null && info.length() != 0) {
                jsonResult.put(name, info);
            }
        }
    }

    public void insertNewUser(JSONObject jsonUserInfo){
        Document userDoc = new Document();

        for(Map.Entry<String, Object> entry : jsonUserInfo.entrySet()){
            userDoc.put(entry.getKey(), entry.getValue().toString());
        }

        mongoXClient.insertDoc(userDoc);
    }

    public void updateUserInfo(JSONObject jsonUserInfo){
        mongoXClient.updateDoc(jsonUserInfo);
    }


    public JSONObject setUserToken(JSONObject jsonReq){
        String userId = RedisUtil.getUserId(jsonReq);
        String redisKey = RedisUtil.getRedisKey_UserToken(jsonReq);
        if(StringUtil.isEmpty(userId) || StringUtil.isEmpty(redisKey)){
            return null;
        }
        RedisTools.set(redisKey, userId, ConstKey.user_token_over_time);
        return null;
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
        String redisKey = RedisUtil.getRedisKey_DevList(jsonReq);
        if(redisKey==null || redisKey.length()==0){
            return null;
        }

        String redisValue = RedisUtil.getRedisValue(redisKey);
        String v = addDevice(redisValue, jsonReq);
        if(!StringUtil.isEmpty(v)){
            RedisTools.set(redisKey, v, ConstKey.user_device_list_over_time);
        }

        JSONObject jsonResult = new JSONObject();
        return jsonResult;
    }


    public JSONObject discovery(JSONObject jsonReq){
        JSONObject jsonResult = new JSONObject();
        jsonResult.put(ConstKey.name, "Discover.Response");
        jsonResult.put(ConstKey.nameSpace, "Alexa.Discovery");

        String redisKey = RedisUtil.getRedisKey_DevList(jsonReq);
        String redisValue = RedisUtil.getRedisValue(redisKey);
        if(StringUtil.isEmpty(redisValue)){
            jsonResult.put(ConstKey.devices, "none");
            return jsonResult;
        }
        JSONArray jsonDevList = new JSONArray();
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
        jsonResult.put(ConstKey.devices, jsonDevList);
        return jsonResult;
    }


    public static void main(String[] argc){
        UserCtrlImpl userCtrl = new UserCtrlImpl();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("user_id", "1234");
        jsonObject.put("user_name", "meizi");
        jsonObject.put("passwd", "meizi1234");
        jsonObject.put("phone", "12121");
//        userCtrl.insertNewUser(jsonObject);

        JSONObject jsonUpdate = new JSONObject();
        jsonUpdate.put("phone", "22222");
        jsonUpdate.put("user_id", "1234");
        userCtrl.updateUserInfo(jsonObject);

        System.out.println(userCtrl.get("1234", "phone"));
    }


}
