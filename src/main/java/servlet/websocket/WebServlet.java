package servlet.websocket;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

/**
 * Created by fanyuanyuan on 2018/4/22.
 */
@SuppressWarnings("serial")
@javax.servlet.annotation.WebServlet(name = "MyEcho WebSocket Servlet", urlPatterns = { "/echo" })
public class WebServlet extends WebSocketServlet {
    public void configure(WebSocketServletFactory factory) {
        // set a 10 second timeout
        factory.getPolicy().setIdleTimeout(10000);

        // register MyEchoSocket as the WebSocket to create on Upgrade
        factory.register(WebServlet.class);

        factory.setCreator(new MyAdvancedEchoCreator());
    }
}
