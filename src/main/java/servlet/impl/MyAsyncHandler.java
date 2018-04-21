package servlet.impl;

import com.alibaba.fastjson.JSONObject;
import org.eclipse.jetty.continuation.Continuation;
import servlet.QueryPack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;

/**
 * Created by fanyuanyuan on 2018/4/15.
 */
public class MyAsyncHandler implements Serializable{

    private static final long serialVersionUID = -7890663945232864573L;

    private Continuation continuation;

    private HttpServletResponse response;
    private HttpServletRequest request;

    public MyAsyncHandler(Continuation ctu, HttpServletRequest request, HttpServletResponse response){
        this.continuation = ctu;
        this.response = response;
        this.request = request;
    }

    public void onSetEvent(JSONObject jsonReq) {

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
