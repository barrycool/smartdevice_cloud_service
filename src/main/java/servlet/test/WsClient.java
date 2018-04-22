package servlet.test;

import com.alibaba.fastjson.JSONObject;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.net.URI;
import java.util.concurrent.TimeUnit;

/**
 * Created by fanyuanyuan on 2018/4/22.
 */
public class WsClient {
    public static void main(String args[])
    {
        String destUri = "ws://localhost:8080/smartdevice_cloud_service/echo";
        if (args.length > 0)
        {
            destUri = args[0];
        }
        WebSocketClient client = new WebSocketClient();
        SimpleEchoSocket socket = new SimpleEchoSocket();
        try
        {
            client.start();
            while(true) {
                URI echoUri = new URI(destUri);
                ClientUpgradeRequest request = new ClientUpgradeRequest();

                JSONObject jsob = new JSONObject();
                jsob.put("deviceId", "12322");
                jsob.put("name_space'", "Alexa");
                jsob.put("name", "ReportState");
                jsob.put("token", "2222");

                request.setSubProtocols(jsob.toString());
                request.setHeader("index", "3");
            /* 使用相应的webSocket进行连接 */
                client.connect(socket, echoUri, request);

                System.out.printf("Connecting to : %s%n", echoUri);
                socket.awaitClose(1000, TimeUnit.SECONDS);
            }
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
        finally
        {
            try
            {
                client.stop();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

}
