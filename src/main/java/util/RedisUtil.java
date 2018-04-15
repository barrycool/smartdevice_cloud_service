package util;

import client.RedisClient;
import client.RedisFactory;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by fanyuanyuan on 2018/3/24.
 */
public class RedisUtil {
    private static RedisClient redisClient = RedisFactory.getClient(RedisFactory.RedisKey.APP);

    public static String getRedisValue(String redisKey){
        if(StringUtil.isEmpty(redisKey)){
            return null;
        }
        if(redisClient==null){
            return null;
        }

        return redisClient.get(redisKey);
    }

    public static String getUserId(JSONObject jsonReq){
        String redisKey = getRedisKey_UserToken(jsonReq);
        return getRedisValue(redisKey);
    }

    public static String getRedisKey_DevStatus(JSONObject jsonReq){
//        String userId = getUserId(jsonReq);
        String deviceId = jsonReq.getString(ConstKey.deviceId);
//        if(userId==null || deviceId==null || userId.length()==0 || deviceId.length()==0){
//            return null;
//        }
        if(StringUtil.isEmpty(deviceId)){
            return null;
        }
        return ConstKey.redis_key_prefix_user_device_status + deviceId + ":";
    }

    public static String getRedisKey_DevConnectStatus(JSONObject jsonReq){
//        String userId = getUserId(jsonReq);
        String deviceId = jsonReq.getString(ConstKey.deviceId);
//        if(userId==null || deviceId==null || userId.length()==0 || deviceId.length()==0){
//            return null;
//        }
        if(StringUtil.isEmpty(deviceId)){
            return null;
        }
        return ConstKey.redis_key_prefix_user_device_connect_status + deviceId + ":";
    }

    public static String getRedisKey_UserToken(JSONObject jsonReq){
        String token = jsonReq.getString(ConstKey.token);
        if(StringUtil.isEmpty(token)){
            return null;
        }
        return ConstKey.redis_key_prefix_user_token +  token;
    }

    public static String getRedisKey_DevList(JSONObject jsonReq){
        String userId = getUserId(jsonReq);
        if(StringUtil.isEmpty(userId)){
            return null;
        }
        return ConstKey.redis_key_prefix_user_device_list + userId + ":";
    }

    public static String getRedisKey_DevList(String  userId){
        return ConstKey.redis_key_prefix_user_device_list + userId + ":";
    }

    public static String getRedisKey_UserInfo(JSONObject jsonReq){
        String userId = jsonReq.getString(ConstKey.userId);
        if(StringUtil.isEmpty(userId)){
            return null;
        }
        return ConstKey.redis_key_prefix_user_id + userId + ":";
    }


    public static String getRedisKey_sendCode(String mailInfo){
        if(StringUtil.isEmpty(mailInfo)){
            return null;
        }
        return ConstKey.redis_key_prefix_register_code + mailInfo + ":";
    }

    public static String getRedisKey_userLogin(String login){
        return ConstKey.redis_key_prefix_user_login_name + login + ":";
    }


}
