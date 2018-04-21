package servlet;

import com.alibaba.fastjson.JSONObject;
import log.SaveTraceLog;
import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import servlet.impl.DeviceCtrlImpl;
import servlet.impl.MyAsyncHandler;
import util.ConstKey;
import util.PrintUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fanyuanyuan on 2018/4/7.
 */
public class ContinuationServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(ContinuationServlet.class);

    private DeviceCtrlImpl deviceCtrl =  DeviceCtrlImpl.getInstance();



    private static final long serialVersionUID = 6112996063962978130L;


    protected JSONObject processRequest(JSONObject jsonReq){

        JSONObject queryResult  = null;
        try {
            String nameSpace = jsonReq.getString(ConstKey.nameSpace);
            switch (nameSpace){
                case "Alexa":
                    queryResult = deviceCtrl.getDevStatus(jsonReq);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            logger.error("request failed, ERROR={}", e);
        }
        return queryResult;
    }


    private void post(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        JSONObject jsonReq = QueryPack.postQueryPack(request);

        Continuation continuation = ContinuationSupport.getContinuation(request);
        continuation.setTimeout(0);

        String deviceId = jsonReq.getString(ConstKey.deviceId);
        if (continuation.isInitial()) {
            processRequest(jsonReq);
            continuation.suspend();
            MyAsyncHandler myAsyncHandler = new MyAsyncHandler(continuation, request, response);
            deviceCtrl.addAsynHandler(deviceId, myAsyncHandler);
        }
    }

    private void get(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        JSONObject jsonReq = QueryPack.queryPack(request);

        Continuation continuation = ContinuationSupport.getContinuation(request);
        continuation.setTimeout(0);
        String deviceId = jsonReq.getString(ConstKey.deviceId);
        if (continuation.isInitial()) {
            processRequest(jsonReq);
            continuation.suspend();
            MyAsyncHandler myAsyncHandler = new MyAsyncHandler(continuation, request,  response);
            deviceCtrl.addAsynHandler(deviceId, myAsyncHandler);
        }
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        get(request, response);
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        post(request, response);
    }
}
