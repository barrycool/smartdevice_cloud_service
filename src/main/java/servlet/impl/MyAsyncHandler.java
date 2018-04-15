package servlet.impl;

import com.alibaba.fastjson.JSONObject;
import org.eclipse.jetty.continuation.Continuation;
import util.PrintUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by fanyuanyuan on 2018/4/15.
 */
public class MyAsyncHandler {

    private Continuation continuation;

    private HttpServletResponse response;

    public MyAsyncHandler(Continuation ctu, HttpServletResponse response){
        this.continuation = ctu;
        this.response = response;
    }

    public void onEvent(JSONObject jsonReq) {

        PrintWriter printWriter = null;
        try {
            printWriter = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        printWriter.println(jsonReq.toJSONString());
        printWriter.flush();
    }
}
