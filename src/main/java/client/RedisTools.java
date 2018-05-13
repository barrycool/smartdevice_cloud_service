package client;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import servlet.impl.UserCtrlImpl;
import util.ConstKey;
import util.RedisUtil;
import util.StringUtil;

import java.util.concurrent.CompletableFuture;

public class RedisTools extends Thread{

    private static RedisClient redisClient = RedisFactory.getClient(RedisFactory.RedisKey.APP);
    private static final Logger logger = LoggerFactory.getLogger(RedisTools.class);

    public static CompletableFuture<Void> setDeviceStatusSync(String key, String v, int overTime) {
        return CompletableFuture.runAsync(() -> {
            redisClient.put(key, v, overTime);
        });
    }

    public static String set(String key, String v, int overTime) {
        try{
            if (StringUtil.isEmpty(v) || StringUtil.isEmpty(key)) {
                return ConstKey.Failed;
            }
            return redisClient.put(key, v, overTime);
        }catch (Exception e){
            logger.error("set redis failed, k={}, v={}", key, v);
            return ConstKey.Failed;
        }

    }

    public static String get(String key) {
        return redisClient.get(key);
    }

    public static void main(String[] argc){
        String redisKey_userInfo = RedisUtil.getRedisKey_UserInfo("4e3a350ef7d0948a9f82a9431cc29e63");
        System.out.println(RedisTools.get(redisKey_userInfo));
    }
}