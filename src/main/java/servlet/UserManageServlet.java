package servlet;

import com.alibaba.fastjson.JSONObject;
import log.SaveTraceLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import servlet.impl.UserCtrlImpl;
import util.ConstKey;
import util.PrintUtil;
import util.StringUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * Created by fanyuanyuan on 2018/3/17.
 */
public class UserManageServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(UserManageServlet.class);


    private UserCtrlImpl userCtrl = UserCtrlImpl.getUserCtrl();

    protected JSONObject processRequest(JSONObject jsonReq)
            throws ServletException, IOException {

        JSONObject queryResult = new JSONObject();
        try {
            String nameSpace = jsonReq.getString(ConstKey.nameSpace);
            if(StringUtil.isEmpty(nameSpace)){
                return null;
            }
            switch (nameSpace){
                case "AccountManagement":
                    queryResult = userCtrl.userCtrl(jsonReq);
                    break;
                case "DeviceManagement":
                    queryResult = userCtrl.addDevice(jsonReq);
                    break;
                case "Alexa.Discovery":
                    queryResult = userCtrl.discovery(jsonReq);
                    break;
                case "Oauth":
                    queryResult = userCtrl.setUserToken(jsonReq);
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
