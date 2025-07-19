package com.myserver.framework;

import com.myserver.annotations.*;

import java.lang.reflect.Method;
import java.util.*;

public class Router {
    private final Map<String, RouteHandler> getRoutes = new HashMap<>();
    private final Map<String, RouteHandler> postRoutes = new HashMap<>();

    public void registerControllers(List<Class<?>> controllerClasses) throws Exception {
        for (Class<?> cls : controllerClasses) {
            if (!cls.isAnnotationPresent(BhaiControl.class)) continue;

            Object controllerInstance = cls.getDeclaredConstructor().newInstance();

            for (Method method : cls.getDeclaredMethods()) {
                if (method.isAnnotationPresent(BhaiDe.class)) {
                    String path = method.getAnnotation(BhaiDe.class).value();
                    getRoutes.put(path, new RouteHandler(controllerInstance, method));
                } else if (method.isAnnotationPresent(BhaiLe.class)) {
                    String path = method.getAnnotation(BhaiLe.class).value();
                    postRoutes.put(path, new RouteHandler(controllerInstance, method));
                }
            }
            System.out.println("Get Paths: ");
            System.out.println(getRoutes);
            System.out.println("POST Paths: ");
            System.out.println(postRoutes);
        }
    }

    public RouteHandler findHandler(String httpMethod, String path) {

        return switch (httpMethod) {
            case "GET" -> getRoutes.get(path);
            case "POST" -> postRoutes.get(path);
            default -> null;
        };
    }
}
