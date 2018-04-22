package servlet.websocket;

import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;

/**
 * Created by fanyuanyuan on 2018/4/22.
 */
public class MyAdvancedEchoCreator implements WebSocketCreator {

    AnnotatedEchoSocket annotatedEchoSocket;


    public MyAdvancedEchoCreator() {
        annotatedEchoSocket = new AnnotatedEchoSocket();
    }

    @Override
    public Object createWebSocket(ServletUpgradeRequest req, ServletUpgradeResponse resp) {
        for (String subprotocol : req.getSubProtocols()) {
            System.out.println(subprotocol);
        }
//        annotatedEchoSocket.onMessage("asd");
        return annotatedEchoSocket;
    }
}
