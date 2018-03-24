package servlet;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import log.SaveTraceLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import servlet.impl.DeviceCtrlImpl;
import util.PrintUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * Created by yuanyuanfan on 2018/1/23.
 */
public class DeviceCtrlServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(DeviceCtrlServlet.class);

    private DeviceCtrlImpl deviceCtrl = new DeviceCtrlImpl();


    protected void processGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        JSONObject  queryResult = new JSONObject();
        JSONObject jsonReq = QueryPack.queryPack(request);
        long start = System.currentTimeMillis();
        try {
//            queryResult = deviceCtrl.process(jsonReq);
//            PrintUtil.print(queryResult, response);
        } catch (Exception e) {
            logger.error("request failed, error={}", e);
        }
        long end = System.currentTimeMillis();
        SaveTraceLog.saveTraceLog(request.getRemoteAddr(), end - start, request.getRequestURL().toString());
        return;
    }

    protected void processPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        long start = System.currentTimeMillis();

        response.setContentType("text/html;charset=UTF-8");
        JSONObject jsonReq = QueryPack.postQueryPack(request);
        if (jsonReq == null) {
            logger.error("request param is not valied, error={}", jsonReq);
            return;
        }

        try {
            String type = jsonReq.getString("type");
            JSONObject jsonResult = new JSONObject();
            if(type.equals("")){
//                jsonResult = deviceCtrl.setDevStatus(jsonReq);
            }else{
//                jsonResult = deviceCtrl.setDevList(jsonReq);
            }

            PrintUtil.print(jsonResult, response);

        } catch (Exception e) {
            logger.error("set device failed, ERROR={}", e);
        }
        long end = System.currentTimeMillis();
        SaveTraceLog.saveTraceLog(request.getRemoteAddr(), end - start, request.getRequestURL().toString());
        return;
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processGet(request, response);
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processPost(request, response);
    }

}
