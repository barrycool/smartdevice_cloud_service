package servlet.impl;

import client.MongoXClient;
import client.RedisTools;
import com.alibaba.fastjson.JSON;
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


    private static UserCtrlImpl userCtrl = new UserCtrlImpl();

    public static UserCtrlImpl getUserCtrl() {
        return userCtrl;
    }

    private UserCtrlImpl() {
    }

    public JSONObject sendCode(JSONObject jsonReq) {
        JSONObject queryResult = new JSONObject();
        queryResult.put(ConstKey.nameSpace, jsonReq.getString(ConstKey.nameSpace));
        queryResult.put(ConstKey.name, "RequestRegisterCode.Response");
        JSONObject jsonResult = new JSONObject();
        jsonResult.put(ConstKey.code, "failed");
        jsonResult.put(ConstKey.msg, "send register code failed");
        String userMailAddr = jsonReq.getString(ConstKey.userAccount);

        if (StringUtil.isEmpty(userMailAddr)) {
            queryResult.put(ConstKey.result, jsonResult);
            return queryResult;
        }


        String code = MailSender.sendEmail(userMailAddr);
        if (code != null) {
            jsonResult.put(ConstKey.code, "OK");
            jsonResult.put(ConstKey.msg, "send register code successfully");
            queryResult.put(ConstKey.result, jsonResult);

            String redisKey_sendCode = RedisUtil.getRedisKey_sendCode(userMailAddr);
            RedisTools.set(redisKey_sendCode, code, ConstKey.register_code_over_time);
        }
        return queryResult;
    }

    public JSONObject packUserInfo(JSONObject jsonReq) {
        JSONObject jsonUserInfo = new JSONObject();
        try {
            jsonUserInfo.put(ConstKey.userName, jsonReq.getString(ConstKey.userName));
            jsonUserInfo.put(ConstKey.userPasswd, jsonReq.getString(ConstKey.userPasswd));
            jsonUserInfo.put(ConstKey.userPhone, jsonReq.getString(ConstKey.userPhone));
            jsonUserInfo.put(ConstKey.userEmail, jsonReq.getString(ConstKey.userEmail));
            jsonUserInfo.put(ConstKey.RegisterCode, jsonReq.getString(ConstKey.RegisterCode));
        } catch (Exception e) {
            e.printStackTrace();

        }
        return jsonUserInfo;
    }

    public JSONObject userCtrl(JSONObject jsonReq) {

        JSONObject queryResult = new JSONObject();
        String name = jsonReq.getString(ConstKey.name);
        switch (name) {
            case "RequestRegisterCode":
                queryResult = sendCode(jsonReq);
                break;
            case "AddUser":
                JSONObject jsonUserInfo = packUserInfo(jsonReq);
                queryResult = addUser(jsonUserInfo);
                break;
            case "Login":
                queryResult = login(jsonReq);
                break;
            default:
                break;
        }
        return queryResult;
    }

    public JSONObject login(JSONObject jsonReq) {
        JSONObject queryResult = new JSONObject();
        queryResult.put(ConstKey.nameSpace, "AccountManagement");
        queryResult.put(ConstKey.name, "logIn.Response");
        JSONObject jsonResult = new JSONObject();
        jsonResult.put(ConstKey.code, "OK");
        jsonResult.put(ConstKey.msg, "logIn successfully");

        String logInName = jsonReq.getString(ConstKey.loginName);
        String passwd = jsonReq.getString(ConstKey.userPasswd);
        String redisKey_userLogin = RedisUtil.getRedisKey_userLogin(logInName);
        String strUserInfo = RedisTools.get(redisKey_userLogin);
        if(StringUtil.isEmpty(passwd) || StringUtil.isEmpty(strUserInfo) ){
            jsonResult.put(ConstKey.code, "Failed");
            jsonResult.put(ConstKey.msg, "user_name or passwd not correct!!!");
            queryResult.put(ConstKey.result, jsonResult);
            return queryResult;
        }

        JSONObject jsonUserInfo = JSON.parseObject(strUserInfo);
        String redisPasswd = jsonUserInfo.getString(ConstKey.userPasswd);
        if(!passwd.equals(redisPasswd)){
            jsonResult.put(ConstKey.code, "Failed");
            jsonResult.put(ConstKey.msg, "user_name or passwd not correct!!!");
            queryResult.put(ConstKey.result, jsonResult);
            return queryResult;
        }
        jsonUserInfo.remove(ConstKey.userPasswd);
        jsonUserInfo.remove("_id");
        jsonResult.put(ConstKey.userInfo, jsonUserInfo);
        queryResult.put(ConstKey.result, jsonResult);
        return queryResult;
    }


    public JSONObject get(String m2, String... fieldNames) {
        JSONObject jsonResult = new JSONObject();

        MongoCursor<Document> cur = mongoXClient.findIterByUserId(m2, fieldNames);
        if (cur == null) {
            return jsonResult;
        }
        while (cur.hasNext()) {
            Document doc = cur.next();
            addUser(doc, jsonResult, fieldNames);
        }

        return jsonResult;
    }

    private void addUser(Document doc, JSONObject jsonResult, String... fieldNames) {
        List<String> names = asList(fieldNames);
        for (String name : names) {
            String info = doc.getString(name);
            if (info != null && info.length() != 0) {
                jsonResult.put(name, info);
            }
        }
    }


    public JSONObject addUser(JSONObject jsonUserInfo) {

        JSONObject queryResult = new JSONObject();
        queryResult.put(ConstKey.nameSpace, "AccountManagement");
        queryResult.put(ConstKey.name, "AddUser.Response");

        JSONObject jsonResult = new JSONObject();
        jsonResult.put(ConstKey.code, "OK");
        jsonResult.put(ConstKey.msg, "add user successfully");

        String sendCode = jsonUserInfo.getString(ConstKey.RegisterCode);
        String mailInfo = jsonUserInfo.getString(ConstKey.userEmail);

        String redisKey_sendCode = RedisUtil.getRedisKey_sendCode(mailInfo);
        String v = RedisTools.get(redisKey_sendCode);
        if(StringUtil.isEmpty(sendCode) || StringUtil.isEmpty(v) || !v.equals(sendCode)){
            jsonResult.put(ConstKey.code, "Failed");
            jsonResult.put(ConstKey.msg, "register code is error or invalid!!!");
            queryResult.put(ConstKey.result, jsonResult);
            return  queryResult;
        }

        String userPhone = jsonUserInfo.getString(ConstKey.userPhone);
        String userEmail = jsonUserInfo.getString(ConstKey.userEmail);
        String userId = "";
        if(!StringUtil.isEmpty(userPhone)){
            userId = StringUtil.getMD5(userPhone);
        }
        if(StringUtil.isEmpty(userId) && !StringUtil.isEmpty(userEmail)){
            userId = StringUtil.getMD5(userEmail);
        }
        jsonUserInfo.put(ConstKey.userId, userId);
        jsonUserInfo.put("_id", userId);

        JSONObject jsonExistUserInfo = mongoXClient.getUserInfo(userId);
        if(jsonExistUserInfo!=null && jsonExistUserInfo.size()!=0){
            jsonResult.put(ConstKey.code, "Failed");
            jsonResult.put(ConstKey.msg, "add user has exist!!!");
            queryResult.put(ConstKey.result, jsonResult);
            return  queryResult;
        }
        jsonUserInfo.remove(ConstKey.RegisterCode);
        addUserMongo(jsonUserInfo);
        addUserRedis(jsonUserInfo);

        queryResult.put(ConstKey.result, jsonResult);

        return queryResult;
    }

    public boolean addUserMongo(JSONObject jsonUserInfo) {
        Document userDoc = new Document();
        for (Map.Entry<String, Object> entry : jsonUserInfo.entrySet()) {
            String key = entry.getKey();
            String value = (String)entry.getValue();
            userDoc.put(key, value);
        }
        mongoXClient.insertDoc(userDoc);
        return true;
    }

    public boolean addUserRedis(JSONObject jsonUserInfo) {
        String redisKey_userInfo = RedisUtil.getRedisKey_UserInfo(jsonUserInfo);
        String v = jsonUserInfo.toJSONString();
        RedisTools.set(redisKey_userInfo, v, ConstKey.user_id_over_time);

        String userPhone = jsonUserInfo.getString(ConstKey.userPhone);
        String redisKey_userPhoneLogin = RedisUtil.getRedisKey_userLogin(userPhone);
        RedisTools.set(redisKey_userPhoneLogin, v, ConstKey.user_login_name_over_time);

        String userMail = jsonUserInfo.getString(ConstKey.userEmail);
        String redisKey_userMailLogin = RedisUtil.getRedisKey_userLogin(userMail);
        RedisTools.set(redisKey_userMailLogin, v, ConstKey.user_login_name_over_time);
        return true;
    }


    public void updateUserInfo(JSONObject jsonUserInfo) {
        mongoXClient.updateDoc(jsonUserInfo);
    }


    public JSONObject setUserToken(JSONObject jsonReq) {
        JSONObject queryResult = new JSONObject();
        queryResult.put(ConstKey.nameSpace, "Oauth");
        queryResult.put(ConstKey.name, "Update.Response");
        JSONObject jsonResult = new JSONObject();

//        String userId = RedisUtil.getUserId(jsonReq);
        String userId = jsonReq.getString("userId");
        String redisKey = RedisUtil.getRedisKey_UserToken(jsonReq);
        if (StringUtil.isEmpty(userId) || StringUtil.isEmpty(redisKey)) {
            jsonResult.put(ConstKey.code, "Failed");
            jsonResult.put(ConstKey.msg, "user_id is null");
            queryResult.put(ConstKey.result, jsonResult);
            return queryResult;
        }
        String status = RedisTools.set(redisKey, userId, ConstKey.user_token_over_time);
        jsonResult.put(ConstKey.code, "OK");
        jsonResult.put(ConstKey.msg, "Update successfully");
        queryResult.put(ConstKey.result, jsonResult);

        return queryResult;
    }

    public JSONObject createDeviceInfo(JSONObject jsonReq){
        if (jsonReq == null) {
            return null;
        }
        String deviceId = jsonReq.getString(ConstKey.deviceId);
        String deviceType = jsonReq.getString(ConstKey.deviceType);
        String friendlyName = jsonReq.getString(ConstKey.friendlyName);
        String manufactureName = jsonReq.getString(ConstKey.manufactureName);
        JSONObject jsonDevice = new JSONObject();
        if(!StringUtil.isEmpty(deviceId)){
            jsonDevice.put(ConstKey.deviceId, deviceId);
        }
        if(!StringUtil.isEmpty(deviceType)){
            jsonDevice.put(ConstKey.deviceType, deviceType);
        }
        if(!StringUtil.isEmpty(friendlyName)){
            jsonDevice.put(ConstKey.friendlyName, friendlyName);
        }
        if(!StringUtil.isEmpty(manufactureName)){
            jsonDevice.put(ConstKey.manufactureName, manufactureName);
        }

        return jsonDevice;
    }


    public String addDevice(String redisValue, JSONObject jsonReq) {
        if (jsonReq == null) {
            return redisValue;
        }

        JSONObject jsonDeviceInfo = createDeviceInfo(jsonReq);

        if(StringUtil.isEmpty(redisValue)){
            return jsonDeviceInfo.toJSONString();
        }
        JSONArray jsonArray = JSON.parseArray(redisValue);
        for(Object obj : jsonArray){
            JSONObject existDevice = (JSONObject)obj;
            if(existDevice.get(ConstKey.deviceId).equals(jsonDeviceInfo.get(ConstKey.deviceId))){
                return jsonArray.toJSONString();
            }
        }
        jsonArray.add(jsonDeviceInfo);
        return jsonArray.toJSONString();
    }

    public void setRedisDeviceList(String key, JSONObject jsonReq){
        String userKey = jsonReq.getString(key);
        String redisKey = RedisUtil.getRedisKey_DevList(userKey);
        if (StringUtil.isEmpty(redisKey)) {
            return;
        }
        String redisValue = RedisUtil.getRedisValue(redisKey);
        String v = addDevice(redisValue, jsonReq);

        if (!StringUtil.isEmpty(v)) {
            RedisTools.set(redisKey, v, ConstKey.user_device_list_over_time);
        }
    }

    public void setMongoDeviceList(String key, JSONObject jsonReq){
        String userKey = jsonReq.getString(key);
        String redisKey = RedisUtil.getRedisKey_DevList(userKey);
        if (StringUtil.isEmpty(redisKey)) {
            return;
        }
        String redisValue = RedisUtil.getRedisValue(redisKey);
        String v = addDevice(redisValue, jsonReq);

        if (!StringUtil.isEmpty(v)) {
            RedisTools.set(redisKey, v, ConstKey.user_device_list_over_time);
        }
    }

    public JSONObject addDevice(JSONObject jsonReq) {

        setRedisDeviceList(ConstKey.token, jsonReq);
        setRedisDeviceList(ConstKey.userId, jsonReq);

        setMongoDeviceList(ConstKey.token, jsonReq);
        setMongoDeviceList(ConstKey.userId, jsonReq);

        JSONObject queryResult = new JSONObject();
        queryResult.put(ConstKey.nameSpace, "DeviceManagement");
        queryResult.put(ConstKey.name, "AddDevice.Response");

        JSONObject jsonResult = new JSONObject();
        jsonResult.put(ConstKey.code, "OK");
        jsonResult.put(ConstKey.msg, "add device successfully");
        queryResult.put(ConstKey.result, jsonResult);
        return queryResult;
    }


    public JSONObject discovery(JSONObject jsonReq) {
        JSONObject jsonResult = new JSONObject();
        jsonResult.put(ConstKey.name, "Discover.Response");
        jsonResult.put(ConstKey.nameSpace, "Alexa.Discovery");

        String userId = jsonReq.getString(ConstKey.userId);
        String token = jsonReq.getString(ConstKey.token);
        String redisKey = RedisUtil.getRedisKey_DevList(userId);
        if(StringUtil.isEmpty(redisKey)){
            redisKey = RedisUtil.getRedisKey_DevList(token);
        }
        String redisValue = RedisUtil.getRedisValue(redisKey);
        if (StringUtil.isEmpty(redisValue)) {
            jsonResult.put(ConstKey.devices, "none");
            return jsonResult;
        }
        JSONArray jsonDevList = JSON.parseArray(redisValue);
        jsonResult.put(ConstKey.devices, jsonDevList);
        return jsonResult;
    }


    public static void main(String[] argc) {
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
