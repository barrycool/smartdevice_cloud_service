package servlet.websocket;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by fanyuanyuan on 2018/4/22.
 */

@WebSocket(maxTextMessageSize = 128 * 1024, maxBinaryMessageSize = 128 * 1024)
public class AnnotatedEchoSocket {

    @OnWebSocketConnect
    public void onText(Session session) throws Exception {
        if (session.isOpen()) {
            //System.out.printf("返回消息 [%s]%n","ss");
            session.getRemote().sendString("服务器发送数据： 测试001");
            System.out.println("============================================");
            Future<Void> fut;
            fut = session.getRemote().sendStringByFuture("Hello");
            try {
                fut.get(2, TimeUnit.SECONDS);

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

                fut = session.getRemote().sendStringByFuture(df.format(new Date()));
                fut.get(2, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
            }
        }

    }

    @OnWebSocketClose
    public void onWebSocketBinary(int i, String string) {
        System.out.println("关闭");
    }

    @OnWebSocketMessage
    public void onMessage(String msg) {
        System.out.println("服务器已经收到消息 " + msg);
        System.out.printf("Got msg: %s%n", msg);
        System.out.println();

    }

    public void onWebSocketBinary(org.eclipse.jetty.websocket.api.Session session, int a, java.lang.String s) {


    }


}
