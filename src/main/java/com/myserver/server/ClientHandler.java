package com.myserver.server;

import com.myserver.framework.RouteHandler;
import com.myserver.framework.Router;
import com.myserver.http.BhaiPlease;
import com.myserver.http.BhaiMila;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final Router router;

    public ClientHandler(Socket socket, Router router) {
        this.socket = socket;
        this.router = router;
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        ) {
            System.out.println("Incoming request received... [" + socket.getPort() + "]");

            String requestLine = in.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                return;
            }
            String[] parts = requestLine.split(" ");
            String method = parts[0];
            String path = parts[1];

            StringBuilder body = new StringBuilder();

            Map<String, String> headers = new HashMap<>();

            String headerLine;
            while ((headerLine = in.readLine()) != null && !headerLine.isEmpty()) {
                int idx = headerLine.indexOf(":");
                if (idx != -1) {
                    String key = headerLine.substring(0, idx).trim();
                    String value = headerLine.substring(idx + 1).trim();
                    headers.put(key.toLowerCase(), value);
                }
            }

            if ("POST".equalsIgnoreCase(method)) {
                String contentLengthHeader = headers.get("content-length");
                if (contentLengthHeader != null) {
                    int contentLength = Integer.parseInt(contentLengthHeader);
                    for (int i = 0; i < contentLength; i++) {
                        int ch = in.read();
                        if (ch != -1) body.append((char) ch);
                    }
                }
            }

            System.out.println("Method: " + method);
            System.out.println("Body: " + body);
            System.out.println("Path: " + path);
            BhaiPlease request = new BhaiPlease(method, path, body.toString().trim());
            BhaiMila response = new BhaiMila(out);

            RouteHandler handler = router.findHandler(method, request.getPath());

            if (handler != null) {
                // Invoke underlying controller method
                handler.invoke(request, response);

            } else {
                response.bhaiBhejDe(404, "Address not found");
            }

            out.flush();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}