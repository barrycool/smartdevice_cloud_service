package servlet.test;

import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * Created by fanyuanyuan on 2018/4/14.
 */
public class PostRequest {
    public static void jsonPost(String strURL, JSONObject params) {
        try {
            URL url = new URL(strURL);// 创建连接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST"); // 设置请求方式
//            conn.setRequestProperty("Connection", "Keep-Alive");
//            conn.setRequestProperty("Accept", "application/json"); // 设置接收数据的格式
//            conn.setRequestProperty("Content-Type", "application/json"); // 设置发送数据的格式

            while (true){
                OutputStream out = conn.getOutputStream();
                byte[] p = (params.toString()).getBytes();
                out.write(p);
                out.flush();
                out.close();

                InputStream inputStream  = conn.getInputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                char[] buf = new char[1024];
                int len = in.read(buf);
                System.out.println(new String(buf, 0, len));
                Thread.sleep(1000);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] argc){
        JSONObject jsob = new JSONObject();
        jsob.put("deviceId", "12322");
        jsob.put("name_space'", "Alexa");
        jsob.put("name", "ReportState");
        jsob.put("token", "2222");

        jsonPost("http://localhost:8080/smartdevice_cloud_service/connect", jsob);

    }
}
