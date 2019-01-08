package com.tangseng.HttpServlet;

import javax.print.attribute.HashDocAttributeSet;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class MyRequest {
    private String URL;
    private String method;

    private Map<String, String> parameterMap = new HashMap<>();
    public MyRequest(InputStream inputStream) throws IOException
    {
        String httpRequest="";
        byte[] httpRequestBytes = new byte[1024];
        int length = 0;
        if ((length = inputStream.read(httpRequestBytes))>0){
            httpRequest = new String(httpRequestBytes,0,length);
        }
        String httpHead =  httpRequest.split("\n")[0];
        URL = httpHead.split("\\s")[1];
        method = httpHead.split("\\s")[0];
        if(URL.indexOf("?")!=-1){

            String str = URL.substring(URL.indexOf("?"));
            String[] split = str.split("&");
            if(split!=null){
                for (int i = 0;i<split.length;i++)
                {
                    parameterMap.put(split[i].substring(0,split[i].indexOf("=")),split[i].substring(split[i].indexOf("=")+1));
                }
            }
            URL = URL.substring(0,URL.indexOf("?"));
        }
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public Map<String, String> getParameterMap() {
        return parameterMap;
    }

    public void setParameterMap(Map<String, String> parameterMap) {
        this.parameterMap = parameterMap;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
