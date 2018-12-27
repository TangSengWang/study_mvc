package com.tangseng.servlet;

import com.tangseng.Annotation.Autowired;
import com.tangseng.Annotation.Controller;
import com.tangseng.Annotation.RequestMapping;
import com.tangseng.Annotation.Service;


import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

public class MyDispatcherServlet extends HttpServlet {

    private Properties properties = new Properties();


    private List<String> classArr  = new ArrayList<>();


    private Map<String ,Object> ioc = new HashMap<>();

    private Map<String ,Method> HandlerMapping = new HashMap<>();

    private Map<Method,Object> ControllerMap = new HashMap<>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        initConfig(config);
        loadClass(properties.getProperty("scanPackage"));
        Instance();
        Autowired();
        initHandlerMapping();


    }

    private void initHandlerMapping() {
        if(ioc.isEmpty()){
            return;
        }
        try{
            for(Map.Entry<String,Object> entry:ioc.entrySet()){
                Class<?> aClass = entry.getValue().getClass();
                if(!aClass.isAnnotationPresent(Controller.class)){
                    continue;
                }else{
                    String Url="";
                    if(aClass.isAnnotationPresent(RequestMapping.class)){
                        Url=aClass.getAnnotation(RequestMapping.class).value();
                    }
                    Method[] declaredMethods = aClass.getDeclaredMethods();
                    for(Method method:declaredMethods){
                        if( method.isAnnotationPresent(RequestMapping.class)){
                            RequestMapping annotation = method.getAnnotation(RequestMapping.class);
                            String value = annotation.value();
                            HandlerMapping.put(Url+"/"+ value,method);
                            ControllerMap.put(method,aClass.getSimpleName());
                        }else{
                            continue;
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void Autowired() {
        if(ioc.isEmpty()){
            return;
        }
        for(Map.Entry<String,Object> entry:ioc.entrySet()){
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            for(Field field:fields){
                if(!field.isAnnotationPresent(Autowired.class)){
                    continue;
                }
                Autowired annotation = field.getAnnotation(Autowired.class);
                String value = annotation.value().trim();
                if(value.isEmpty()){
                    value = field.getType().getName();
                }
                field.setAccessible(true);
                try {
                    field.set(entry.getValue(),ioc.get(value));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public String toLowerFirstWord(String str){

        return str.charAt(0)+"".toLowerCase()+ str.substring(1);
    }
    private void Instance() {
        if(classArr.isEmpty()){
            return;
        }
        for(String cl : classArr){
            try{
                Class<?> aClass = Class.forName(cl.replace("/","."));
                if(aClass.isAnnotationPresent(Controller.class)){
                    ioc.put(toLowerFirstWord(aClass.getSimpleName()),aClass.newInstance());
                }else if(aClass.isAnnotationPresent(Service.class)){
                    Service annotation = aClass.getAnnotation(Service.class);
                    String value = annotation.value();
                    if(value.isEmpty()){
                        value = toLowerFirstWord(aClass.getSimpleName());
                    }
                    Object Obj = aClass.newInstance();
                    ioc.put(value,Obj);
                    Class<?>[] interfaces = aClass.getInterfaces();
                    for(Class cla:interfaces){
                        ioc.put(cla.getName(),Obj);
                    }
                }else{
                    continue;
                }
            }catch (Exception e){
                e.printStackTrace();
                continue;
            }
        }
    }

    private void loadClass(String url) {
        URL resource = this.getClass().getClassLoader().getResource("/" + url.replace('.', '/'));
        File superFile = new File(resource.getFile());
        for (File file : superFile.listFiles())
        {
            try {
                if(file.isDirectory()){

                        loadClass(url.replace('.','/')+"/"+file.getName());

                }else{
                    classArr.add(url.replace('.','/')+"/"+file.getName().replace(".class",""));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException
    {
        doPost(req,resp);
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException
    {
        doDispatch(req,resp);
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) {
        if(HandlerMapping.isEmpty()){
            return;
        }
        String requestURI = req.getRequestURI();
        String contextPath = req.getContextPath();
        requestURI= requestURI.replace(contextPath, "");

        if(!HandlerMapping.containsKey(requestURI)){
            try {
                resp.getWriter().print("404");
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Method method = HandlerMapping.get(requestURI);
        Class<?>[] parameterTypes = method.getParameterTypes();
        Map<String, String[]> parameterMap = req.getParameterMap();
        Object [] paramValues= new Object[parameterTypes.length];
        System.out.println(parameterTypes);
        for(int i = 0 ;i<parameterTypes.length;i++){
            String className = parameterTypes[i].getSimpleName();
            if("HttpServletRequest".equals(className)){
                paramValues[i]=req;
            }
            if("HttpServletResponse".equals(className)){
                paramValues[i]=resp;
            }
            if("String".equals(className)){
                String params="";

                for(Map.Entry<String,String[]> param:parameterMap.entrySet()){
                    String value =Arrays.toString(param.getValue()).replaceAll("\\[|\\]", "").replaceAll(",\\s", ",");
                    paramValues[i] = value;
                }
            }
        }
        try {
            Method method1 = HandlerMapping.get(requestURI);
            Object o = ControllerMap.get(method1);
            Object obj = ioc.get(o);


            method.invoke(obj,paramValues);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void initConfig(ServletConfig config) {
        InputStream scanPackage=null;
        try {
            scanPackage = this.getClass().getClassLoader().getResourceAsStream(config.getInitParameter("contextConfigLocation"));
            properties.load(scanPackage);

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                if(scanPackage != null){
                    scanPackage.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
