package com.fjd.spring.servlet;

import com.fjd.app.controller.OrderController;
import com.fjd.spring.annotation.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DispatcherServlet extends HttpServlet {
    List<String> classNames = new ArrayList<>();
    Map<String, Object> beans = new HashMap<>();
    Map<String, Method> handlerMap = new HashMap<>();

    public void init(ServletConfig config) {
        basePackageScan("com.fjd.app");
        doInstance();
        doAutowired();
        doUrlMapping();
    }

    private void doUrlMapping() {
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            Object instance = entry.getValue();//orderController
            Class<?> clazz = instance.getClass();//OrderController.class
            if (clazz.isAnnotationPresent(FController.class)) {
                FRequestMapping mappingOnClazz = clazz.getAnnotation(FRequestMapping.class);
                String classPath = mappingOnClazz.value();// /Orders
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    if (method.isAnnotationPresent(FRequestMapping.class)) {
                        FRequestMapping mappingOnMethod = method.getAnnotation(FRequestMapping.class);
                        String methodPath = mappingOnMethod.value();// /query
                        String requestPath = classPath + methodPath;// /Orders/query
                        handlerMap.put(requestPath, method);
                    } else {
                        continue;
                    }
                }
            } else {
                continue;
            }
        }
    }

    private void doAutowired() {
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            Object instance = entry.getValue();//orderController
            Class<?> clazz = instance.getClass();//OrderController.class
            if (clazz.isAnnotationPresent(FController.class)) {
                Field[] fields = clazz.getDeclaredFields();
                for (Field field : fields) {
                    if (field.isAnnotationPresent(FAutowired.class)) {
                        FAutowired autowired = field.getAnnotation(FAutowired.class);
                        String key = autowired.value();//"orderservice"
                        Object bean = beans.get(key);//orderService
                        field.setAccessible(true);
                        try {
                            field.set(instance, bean);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private void doInstance() {
        for (String className : classNames) {

            //com.fjd.app.service.OrderService.class
            String cn = className.replace(".class", "");

            //cn = com.fjd.app.service.OrderService
            try {
                Class<?> clazz = Class.forName(cn);
                if (clazz.isAnnotationPresent(FController.class)) {
                    Object instance = clazz.newInstance();
                    String key = clazz.getSimpleName().toLowerCase();

                    //add to big bean map
                    beans.put(key, instance);
                } else if (clazz.isAnnotationPresent(FService.class)) {
                    Object instance = clazz.newInstance();
                    String key = clazz.getSimpleName().toLowerCase();

                    //add to big bean map
                    beans.put(key, instance);
                } else {
                    continue;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    private void basePackageScan(String basePackage) {
        //scan add .class files
        String fileStr = this.getClass().getClassLoader().getResource("/").getPath() + basePackage.replace(".", "/");
        System.out.println(fileStr);
        File file = new File(fileStr);
        String[] fileStrs = file.list();
        for (String path : fileStrs) {

            File filePath = new File(fileStr + "/" + path);
            if (filePath.isDirectory()) {

                basePackageScan(basePackage + "." + path);
            } else {
                //com.fjd.app.service.OrderService.class
                classNames.add(basePackage + "." + filePath.getName());
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String uri = req.getRequestURI();// /simple-spring/Orders/query
        String context = req.getContextPath(); // /simple-spring
        String path = uri.replace(context, ""); // /Orders/query

        Method method = handlerMap.get(path);
        OrderController instance = (OrderController) beans.get(method.getDeclaringClass().getSimpleName().toLowerCase());
        Object[] args = handle(req, resp, method);
        try {


            //System.out.print(args.length+args[0]);
            method.invoke(instance, args);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    //Originally Strategy Design Pattern, here just simplify to if else
    private static Object[] handle(HttpServletRequest req, HttpServletResponse resp, Method method) {

        Class<?>[] paramClazzs = method.getParameterTypes();
        Object[] args = new Object[paramClazzs.length];
        int argsI = 0;
        int index = 0;
        for (Class<?> paramClazz : paramClazzs) {
            if (ServletRequest.class.isAssignableFrom(paramClazz)) {
                args[argsI++] = req;

            }
            if (ServletResponse.class.isAssignableFrom(paramClazz)) {
                args[argsI++] = resp;

            }
            Annotation[] paramAns = method.getParameterAnnotations()[index++];
            if (paramAns.length > 0) {
                for (Annotation paramAn : paramAns) {
                    if (FRequestParam.class.isAssignableFrom(paramAn.getClass())) {
                        FRequestParam rp = (FRequestParam) paramAn;
                        args[argsI++] = req.getParameter(rp.value());
                    }
                }
            }
        }
        return args;
    }
}
