package servlet;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import log.SaveTraceLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import servlet.impl.UserCtrlImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * Created by fanyuanyuan on 2018/3/17.
 */
public class UserCtrlServlet extends HttpServlet {
        private static final Logger logger = LoggerFactory.getLogger(UserCtrlServlet.class);


        private UserCtrlImpl userCtrl = new UserCtrlImpl();

        private void print(JSONObject queryResult, HttpServletResponse response){
            try {
                if(queryResult==null){
                    queryResult = new JSONObject();
                }
                PrintWriter printWriter = response.getWriter();
                printWriter.println(queryResult.toJSONString());
                printWriter.flush();
                printWriter.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        protected void processRequest(HttpServletRequest request, HttpServletResponse response)
                throws ServletException, IOException {
            response.setContentType("text/html;charset=UTF-8");

            JSONObject  queryResult = new JSONObject();
            JSONObject jsonReq = QueryPack.queryPack(request);
            System.out.println(jsonReq.toJSONString());
            long start = System.currentTimeMillis();
            try {
                if (jsonReq == null) {
                    print(queryResult, response);
                    return;
                }

                String type = jsonReq.getString("rt");
                String addr = jsonReq.getString("addr");
                boolean stat = false;
                if(type==null || addr==null){
                    stat = false;
                }else if(type.equals("send")){
                    stat = userCtrl.sendCode(addr);
                }else if(type.equals("verify")){
                    String code = jsonReq.getString("code");
                    stat = userCtrl.verify(addr, code);
                }else if(type.equals("update")){
                    userCtrl.updateUserInfo(jsonReq);
                    stat = true;
                }else if(type.equals("insert")){
                    userCtrl.insertNewUser(jsonReq);
                    stat = true;
                }
                queryResult.put("status", stat);
                print(queryResult, response);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("request failed, error={}", e);
            }
            long end = System.currentTimeMillis();
            SaveTraceLog.saveTraceLog(request.getRemoteAddr(), end - start, request.getRequestURL().toString());
            return;
        }

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            processRequest(request, response);
        }


        public static String toString(HttpServletRequest request) throws IOException, JSONException {
            StringBuffer sb = new StringBuffer() ;
            InputStream is = request.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is,"utf-8"));
            String s = "" ;
            while((s=br.readLine())!=null){
                sb.append(s) ;
            }
            if(sb.toString().length()<=0){
                return null;
            }else {
                return sb.toString();
            }
        }

        @Override
        protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            processRequest(request, response);
        }
}
