package client;

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
            conn.setInstanceFollowRedirects(true);
            conn.setRequestMethod("POST"); // 设置请求方式
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Accept", "application/json"); // 设置接收数据的格式
            conn.setRequestProperty("Content-Type", "application/json"); // 设置发送数据的格式
            byte[] p = params.toString().getBytes();
            conn.setRequestProperty("Content-Length", String.valueOf(p.length));
            conn.setDoOutput(true);
            conn.getOutputStream().write(p);
            Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder sb = new StringBuilder();

            System.out.println(sb.toString());
            URL url2 = new URL(strURL);// 创建连接
            HttpURLConnection conn2 = (HttpURLConnection) url2.openConnection();
            conn2.setDoOutput(true);
            conn2.setDoInput(true);
            conn2.setUseCaches(false);
            conn2.setInstanceFollowRedirects(true);
            conn2.setRequestMethod("POST"); // 设置请求方式
            conn2.setRequestProperty("Connection", "Keep-Alive");
            conn2.setRequestProperty("Accept", "application/json"); // 设置接收数据的格式
            conn2.setRequestProperty("Content-Type", "application/json"); // 设置发送数据的格式
            conn2.setRequestProperty("Content-Length", String.valueOf(p.length));
            conn2.setDoOutput(true);
            conn2.getOutputStream().write(p);
            Reader in2 = new BufferedReader(new InputStreamReader(conn2.getInputStream(), "UTF-8"));
            StringBuilder sb2 = new StringBuilder();
            for (int c; (c = in2.read()) >= 0; ){

                sb.append((char) c);
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public static void main(String[] argc){
        JSONObject jsob = new JSONObject();
        jsob.put("deviceId", "12321");
        jsob.put("name_space'", "Alexa");
        jsob.put("name", "ReportState");
        jsob.put("token", "2222");

        jsonPost("http://localhost:8080/smartdevice_cloud_service/connect", jsob);

    }
}
