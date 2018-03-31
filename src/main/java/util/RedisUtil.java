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
        String userId = getUserId(jsonReq);
        String deviceId = jsonReq.getString(ConstKey.deviceId);
        if(userId==null || deviceId==null || userId.length()==0 || deviceId.length()==0){
            return null;
        }
        return ConstKey.redis_key_prefix_user_device_status + userId + ":" +  deviceId;
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
        String deviceId = jsonReq.getString(ConstKey.deviceId);
        if(userId==null || deviceId==null || userId.length()==0 || deviceId.length()==0){
            return null;
        }
        return ConstKey.redis_key_prefix_user_device_list + userId + ":";
    }


}
