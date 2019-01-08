package com.tangseng.tomcat;

import com.tangseng.HttpServlet.MyRequest;
import com.tangseng.HttpServlet.MyResponse;
import com.tangseng.RequestMapping.ServletMapping;
import com.tangseng.servlet.MyDispatcherServlet;
import com.tangseng.servlet.MyServlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class MyTomcat {
    private int port = 8080;


    public MyTomcat(int port) {
        this.port = port;
    }
    public void start(){
        MyDispatcherServlet.init();

        ServerSocket serverSocket =null;
        try {
            serverSocket = new ServerSocket(port);
            while (true){
                Socket accept = serverSocket.accept();
                InputStream inputStream = accept.getInputStream();
                OutputStream outputStream = accept.getOutputStream();
                dispatch(new MyRequest(inputStream),new MyResponse(outputStream));
                accept.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(serverSocket!=null){
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void dispatch(MyRequest myRequest,MyResponse myResponse){
        if("/favicon.ico".equalsIgnoreCase(myRequest.getURL())) {
            return;
        }
        MyDispatcherServlet.doDispatch(myRequest,myResponse);

    }

    public static void main(String[] args) {
        new MyTomcat(8080).start();
    }
}
