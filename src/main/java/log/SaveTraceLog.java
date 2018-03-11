package log;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;


public class SaveTraceLog extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(SaveTraceLog.class);
    public static CompletableFuture<Void> saveTraceLog(String addr, long cost, String url) {
        return CompletableFuture.runAsync(() -> {
            logger.info("addr:{} cost:{} url:{} title:{}", addr, cost, url);

        });
    }
}
