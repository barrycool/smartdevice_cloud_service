package client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Global;
import util.UtilKey;


import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.*;

/**
 * Created by fanyuanyuan-iri on 2017/5/8.
 */
public class RedisFactory {
    private static final Logger logger = LoggerFactory.getLogger(RedisFactory.class);

    private static Map<RedisKey, RedisClient> redisClientMap = new HashMap<RedisKey, RedisClient>();

    static  {
        initConfig();
    }

    /**
     * 自定义Redis集群Key
     */
    public enum RedisKey{
        APP,
    }

    /**
     * 根据Redis集群Key初始化连接池
     */
    private static void initConfig(){
        initConfig("/redis.properties", RedisKey.APP);
    }

    private static void initConfig(String filename, RedisKey redisKey){
        InputStream in = RedisFactory.class.getResourceAsStream(filename);
        Properties p = new Properties();
        try {
            p.load(in);
            String host = p.getProperty("host");
            String port = p.getProperty("port");
            String auth = p.getProperty("auth");
            RedisClient client = RedisClient.getJedisClient(host, auth, Integer.parseInt(port));
            redisClientMap.put(redisKey, client);
        } catch (IOException e) {
            logger.error("init jedis client failed, error={}", e);
        }
    }

    private static void initConfig(String filename, RedisKey redisKey, String idc){
        InputStream in = RedisFactory.class.getResourceAsStream(filename);
        Properties p = new Properties();
        try {
            p.load(in);
            String host = p.getProperty(idc + "_host");
            String port = p.getProperty(idc + "_port");
            String auth = p.getProperty(idc + "_auth");
            RedisClient client = RedisClient.getJedisClient(host, auth, Integer.parseInt(port));
            redisClientMap.put(redisKey, client);
        } catch (IOException e) {
            logger.error("init jedis client failed, error={}", e);
        }
    }

    public static RedisClient getClient(RedisKey redisKey){
        return redisClientMap.get(redisKey);
    }

    /**
     * 获取本机所在机房
     * @return void
     */
    private static String getIDC() {
        InetAddress iAddr = null;
        String addr = "";
        try {
            iAddr = InetAddress.getLocalHost();
            addr = iAddr.getHostName().toString();
        } catch (Exception e) {

        }
        String idc = "zwt";
        String[] parts = addr.split("\\.");
        if (parts.length < 3) {
            return idc;
        }
        return parts[2];
    }

    public static void main(String[] argc){
        RedisClient redisClient = RedisFactory.getClient(RedisKey.APP);
        redisClient.put(UtilKey.redis_key_prefix_user_device_status +"123:tv", "1", 3600);
        System.out.println(redisClient.get(UtilKey.redis_key_prefix_user_device_status +"123:tv"));
    }
}
