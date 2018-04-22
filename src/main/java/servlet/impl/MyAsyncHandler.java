//package servlet.impl;
//
//import com.alibaba.fastjson.JSONObject;
//import org.eclipse.jetty.continuation.Continuation;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import servlet.QueryPack;
//import util.Global;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.*;
//
///**
// * Created by fanyuanyuan on 2018/4/15.
// */
//public class MyAsyncHandler implements Serializable{
//    private static final Logger logger = LoggerFactory.getLogger(MyAsyncHandler.class);
//
//    private static final long serialVersionUID = -7890663945232864573L;
//
//    private Continuation continuation;
//
//    private HttpServletResponse response;
//    private HttpServletRequest request;
//    private InputStream inputStream;
//    private BufferedReader br;
//
//    public MyAsyncHandler(Continuation ctu, final HttpServletRequest request, HttpServletResponse response){
//        this.continuation = ctu;
//        this.response = response;
//        this.request = request;
//        try {
//            inputStream = request.getInputStream();
//            br = new BufferedReader(new InputStreamReader(inputStream, Global.ENCODE));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public JSONObject onEvent(JSONObject jsonReq) {
//        try {
//            send(jsonReq);
//            return recv();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//
//    public void send(JSONObject jsonReq) {
//        PrintWriter printWriter = null;
//        try {
//            printWriter = response.getWriter();
//            printWriter.write(jsonReq.toJSONString());
//            printWriter.flush();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public JSONObject recv() {
//        try {
//            char[] buf = new char[1024];
//            int len = br.read(buf);
////            if(len>=0){
//                String msg = new String(buf, 0, len);
//                JSONObject jsonResult = QueryPack.postQueryPack(msg);
//                System.out.println("sb_recv:" + msg);
//                return jsonResult;
////            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//}
