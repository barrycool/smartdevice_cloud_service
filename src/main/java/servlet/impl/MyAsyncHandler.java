package servlet.impl;

import com.alibaba.fastjson.JSONObject;
import org.eclipse.jetty.continuation.Continuation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(MyAsyncHandler.class);

    private static final long serialVersionUID = -7890663945232864573L;

    private Continuation continuation;

    private HttpServletResponse response;
    private HttpServletRequest request;

    public MyAsyncHandler(Continuation ctu, HttpServletRequest request, HttpServletResponse response){
        this.continuation = ctu;
        this.response = response;
        this.request = request;
    }

    public JSONObject onSetEvent(JSONObject jsonReq) {
        PrintWriter printWriter = null;
        try {
            printWriter = response.getWriter();
            printWriter.println(jsonReq.toJSONString());
            printWriter.flush();
            JSONObject jsonResult = QueryPack.postQueryPack(request);
            logger.error("return:{}" , jsonResult);
            System.out.println("return: " + jsonResult.toString());
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}
