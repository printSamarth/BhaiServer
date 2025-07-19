package com.myserver.framework;
import com.myserver.http.BhaiPlease;
import com.myserver.http.BhaiMila;

import java.lang.reflect.Method;

public class RouteHandler {
    public final Object controllerInstance;
    public final Method method;

    public RouteHandler(Object controllerInstance, Method method) {
        this.controllerInstance = controllerInstance;
        this.method = method;
    }

    public void invoke(BhaiPlease req, BhaiMila res) throws Exception {
        if (method.getParameterCount() == 2) {
            method.invoke(controllerInstance, req, res);
        } else {
            method.invoke(controllerInstance);
        }
    }
}
