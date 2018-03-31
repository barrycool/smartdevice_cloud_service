package servlet;

import com.alibaba.fastjson.JSONObject;
import log.SaveTraceLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import servlet.impl.DeviceCtrlImpl;
import util.ConstKey;
import util.PrintUtil;
import util.StringUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * Created by yuanyuanfan on 2018/1/23.
 */
public class DeviceGetServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(DeviceGetServlet.class);

    private DeviceCtrlImpl deviceCtrl = new DeviceCtrlImpl();


    protected void processGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        JSONObject jsonReq = QueryPack.queryPack(request);

        long start = System.currentTimeMillis();

        JSONObject queryResult  = null;
        try {
            String nameSpace = jsonReq.getString(ConstKey.nameSpace);
            if(StringUtil.isEmpty(nameSpace)){
                //TODO
            }

            switch (nameSpace){
                case "Alexa.PowerController":
                    queryResult = deviceCtrl.getDevStatus(jsonReq);
                    break;
                case "Alexa.Discovery":
                    queryResult = deviceCtrl.discovery(jsonReq);
                    break;
                case "AccountManagement":
                    break;
                default:
                    break;
            }
            PrintUtil.print(queryResult, response);

        } catch (Exception e) {
            logger.error("request failed, ERROR={}", e);
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
            logger.error("request param is not valid, request={}", jsonReq);
            return;
        }

        try {
            JSONObject queryResult = new JSONObject();
            String nameSpace = jsonReq.getString(ConstKey.nameSpace);
            if(nameSpace.equals("Alexa.PowerController")){
                queryResult = deviceCtrl.getDevStatus(jsonReq);
                PrintUtil.print(queryResult, response);
            }else if(nameSpace.equals("Alexa.Discovery")){
                queryResult = deviceCtrl.discovery(jsonReq);
                PrintUtil.print(queryResult, response);
            }

            PrintUtil.print(queryResult, response);

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
