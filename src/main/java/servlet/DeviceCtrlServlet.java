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
public class DeviceCtrlServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(DeviceCtrlServlet.class);

    private DeviceCtrlImpl deviceCtrl =  DeviceCtrlImpl.getInstance();

    protected JSONObject processRequest(JSONObject jsonReq)
            throws ServletException, IOException {

        JSONObject queryResult = new JSONObject();
        if (jsonReq == null) {
            return null;
        }
        try {
            String nameSpace = jsonReq.getString(ConstKey.nameSpace);
            if(StringUtil.isEmpty(nameSpace)){
                return null;
            }
            switch (nameSpace){
                case "Alexa.PowerController":
                    queryResult = deviceCtrl.setDevStatus(jsonReq);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            logger.error("request failed, error={}", e);
        }
        return queryResult;
    }

    private void post(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long start = System.currentTimeMillis();

        response.setContentType("text/html;charset=UTF-8");
        JSONObject jsonReq = QueryPack.postQueryPack(request);

        JSONObject queryResult = processRequest(jsonReq);
        PrintUtil.print(queryResult, response);

        long end = System.currentTimeMillis();

        SaveTraceLog.saveTraceLog(request.getRemoteAddr(), end - start, request.getRequestURL().toString());

    }

    private void get(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long start = System.currentTimeMillis();

        response.setContentType("text/html;charset=UTF-8");
        JSONObject jsonReq = QueryPack.queryPack(request);

        JSONObject queryResult = processRequest(jsonReq);
        PrintUtil.print(queryResult, response);

        long end = System.currentTimeMillis();

        SaveTraceLog.saveTraceLog(request.getRemoteAddr(), end - start, request.getRequestURL().toString());

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
