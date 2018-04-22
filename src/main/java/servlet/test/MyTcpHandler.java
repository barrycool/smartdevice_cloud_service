package servlet.test;

import com.alibaba.fastjson.JSONObject;
import servlet.QueryPack;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by fanyuanyuan on 2018/4/22.
 */
public class MyTcpHandler extends Thread{
    private Socket socket;
    private BufferedReader bufferedReader;
    private OutputStream outputStream;

    public MyTcpHandler(Socket socket, BufferedReader bufferedReader) throws Exception{
        this.socket = socket;
        this.outputStream = socket.getOutputStream();
        this.bufferedReader = bufferedReader;
    }

    public JSONObject onEvent(JSONObject jsonReq) {
        try {
            send(jsonReq);
            return recv();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public void send(JSONObject jsonReq) {
        try {
            byte[] p = (jsonReq.toJSONString()+"\n").getBytes();
            outputStream.write(p);
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void write(byte[] p ){
        try {
            outputStream.write(p);
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JSONObject recv() {
        try {
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            char[] buf = new char[2048];
            int len = bufferedReader.read(buf);
            String data = new String(buf, 0, len);
            JSONObject jsonResult = QueryPack.postQueryPack(data);
            return jsonResult;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    public String getIp(){
        return this.socket.getInetAddress().getHostAddress();
    }
}
