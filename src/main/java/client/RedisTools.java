package client;

import com.alibaba.fastjson.JSONObject;
import util.ConstKey;
import util.RedisUtil;

import java.util.concurrent.CompletableFuture;

public class RedisTools extends Thread{

    private static RedisClient redisClient = RedisFactory.getClient(RedisFactory.RedisKey.APP);

    public static CompletableFuture<Void> setDeviceStatusSync(String key, String v, int overTime) {
        return CompletableFuture.runAsync(() -> {
            redisClient.put(key, v, overTime);
        });
    }

    public static String set(String key, String v, int overTime) {
        return redisClient.put(key, v, overTime);
    }

    public static String get(String key) {
        return redisClient.get(key);
    }

//    public static void main(String[] argc){
//        JSONObject jsonUserInfo = new JSONObject();
//        jsonUserInfo.put("")
//        String redisKey_userInfo = RedisUtil.getRedisKey_UserInfo(jsonUserInfo);
//        String v = jsonUserInfo.toJSONString();
//        RedisTools.set(redisKey_userInfo, v, ConstKey.user_info_over_time);
//    }
}