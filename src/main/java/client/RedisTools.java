package client;

import java.util.concurrent.CompletableFuture;

public class RedisTools extends Thread{

    private static RedisClient redisClient = RedisFactory.getClient(RedisFactory.RedisKey.APP);

    public static CompletableFuture<Void> setDeviceStatusSync(String key, String v, int overTime) {
        return CompletableFuture.runAsync(() -> {
            redisClient.put(key, v, overTime);
        });
    }

    public static void set(String key, String v, int overTime) {
        redisClient.put(key, v, overTime);
    }
}