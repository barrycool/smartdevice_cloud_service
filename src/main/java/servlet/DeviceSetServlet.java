package servlet;

import com.alibaba.fastjson.JSONObject;
import log.SaveTraceLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import servlet.impl.DeviceCtrlImpl;
import util.ConstKey;
import util.PrintUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * Created by yuanyuanfan on 2018/1/23.
 */
public class DeviceSetServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(DeviceSetServlet.class);

    private DeviceCtrlImpl deviceCtrl = new DeviceCtrlImpl();

    protected void processGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        JSONObject jsonReq = QueryPack.queryPack(request);
        long start = System.currentTimeMillis();
        try {

            String nameSpace = jsonReq.getString(ConstKey.nameSpace);
            JSONObject jsonResult = new JSONObject();
            if(nameSpace.equals("Alexa.PowerController")){
                jsonResult = deviceCtrl.setDevStatus(jsonReq);
            }else if(nameSpace.equals("Alexa.AddDevice")){
                jsonResult = deviceCtrl.addDevice(jsonReq);
            }else if(nameSpace.equals("Alexa.UserToken")){
                jsonResult = deviceCtrl.setUserToken(jsonReq);
            }
            PrintUtil.print(jsonResult, response);
        } catch (Exception e) {
            logger.error("set device failed, ERROR:{}", e);
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
            logger.error("request param is not valied, request:{}", jsonReq);
            return;
        }

        try {
            String nameSpace = jsonReq.getString(ConstKey.nameSpace);
            JSONObject jsonResult = new JSONObject();
            if(nameSpace.equals("Alexa.PowerController")){
                jsonResult = deviceCtrl.setDevStatus(jsonReq);
            }else if(nameSpace.equals("Alexa.AddDevice")){
                jsonResult = deviceCtrl.addDevice(jsonReq);
            }
            PrintUtil.print(jsonResult, response);

        } catch (Exception e) {
            logger.error("set device failed, ERROR:{}", e);
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
