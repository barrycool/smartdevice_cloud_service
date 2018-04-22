package servlet.test;


import com.alibaba.fastjson.JSONObject;
import servlet.QueryPack;
import servlet.test.MyTcpHandler;
import util.ConstKey;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fanyuanyuan on 2018/4/22.
 */
public class TcpServer implements Runnable{
    private Map<String, MyTcpHandler> mapTcpHandler = new HashMap<String, MyTcpHandler>();

    public TcpServer()throws Exception{
        this.run();
    }

    @Override
    public  void run() {
        Socket socket = null;
        OutputStream out = null;
        BufferedReader bufferedReader = null;
        try {
            ServerSocket serverSocket = new ServerSocket(12345);
            socket = serverSocket.accept();
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            char[] buf = new char[2048];
            int len = bufferedReader.read(buf);
            String data = new String(buf, 0, len);
            JSONObject jsonResult = QueryPack.postQueryPack(data);
            String deviceId = jsonResult.getString(ConstKey.deviceId);
            MyTcpHandler myTcpHandler = new MyTcpHandler(socket);
            mapTcpHandler.put(deviceId, myTcpHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JSONObject onEvent(JSONObject jsonReq) {
        try {
            String deviceId = jsonReq.getString(ConstKey.deviceId);
            MyTcpHandler myTcpHandler = mapTcpHandler.get(deviceId);
            send(myTcpHandler, jsonReq);
            return myTcpHandler.recv();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public void send(MyTcpHandler myTcpHandler, JSONObject jsonReq) {
        try {
            byte[] p = (jsonReq.toJSONString()+"\n").getBytes();
            myTcpHandler.write(p);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }







}
