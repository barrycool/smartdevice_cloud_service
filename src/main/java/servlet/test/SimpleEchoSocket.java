package servlet.test;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.StatusCode;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by fanyuanyuan on 2018/4/22.
 */
@WebSocket(maxTextMessageSize = 64 * 1024)
public class SimpleEchoSocket {
    private final CountDownLatch closeLatch;
    @SuppressWarnings("unused")
    private Session session;

    public SimpleEchoSocket()
    {
        this.closeLatch = new CountDownLatch(1);
    }

    public boolean awaitClose(int duration, TimeUnit unit) throws InterruptedException
    {
        return this.closeLatch.await(duration,unit);
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason)
    {
        System.out.printf("客户端关闭: %d - %s%n",statusCode,reason);
        this.session = null;
        this.closeLatch.countDown(); // 触发位置
    }

    @OnWebSocketConnect
    public void onConnect(Session session)
    {
        System.out.printf("客户端连接: %s%n",session);
        this.session = session;
        try
        {
            Future<Void> fut;
            System.out.println("客户端已经发送");
            fut = session.getRemote().sendStringByFuture("11111Hello");
            fut.get(2,TimeUnit.SECONDS); // 等待发送完成

            fut = session.getRemote().sendStringByFuture("222222Thanks for the conversation.");
            fut.get(2, TimeUnit.SECONDS); // 等待发送完成
            System.out.println("客户端已经发送完成");
            session.close(StatusCode.NORMAL,"I'm done");
        }
        catch (Throwable t)
        {

            t.printStackTrace();
        }
    }


    @OnWebSocketMessage
    public void onMessage(String msg)
    {
        System.out.printf("接收到服务器消息: %s%n",msg);
    }
}
