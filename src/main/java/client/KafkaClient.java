package client;

import java.util.concurrent.CompletableFuture;

public class KafkaClient extends Thread{

    private static RedisClient redisClient = RedisFactory.getClient(RedisFactory.RedisKey.APP);

    public static CompletableFuture<Void> setDeviceStatusSync(String key, String v, int overTime) {
        return CompletableFuture.runAsync(() -> {
            redisClient.put(key, v, overTime);
        });
    }

    public static void setDeviceStatus(String key, String v, int overTime) {
        redisClient.put(key, v, overTime);
    }
}