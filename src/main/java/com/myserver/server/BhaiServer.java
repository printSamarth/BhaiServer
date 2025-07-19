package com.myserver.server;


import com.myserver.framework.Router;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class BhaiServer {
    private static final String SERVER_START_MESSAGE = "BhaiServer running on port %d";


    private final int port;
    private final ExecutorService threadPool;
    private final Router router;
    private volatile boolean running;

    public BhaiServer(int port, int maxThreads, Router router) {
        this.port = port;
        this.threadPool = Executors.newFixedThreadPool(maxThreads);
        this.router = router;
        this.running = true;
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = initializeServer()) {
            handleClientConnections(serverSocket);
        } finally {
            shutdown();
        }
    }

    private ServerSocket initializeServer() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.printf((SERVER_START_MESSAGE) + "%n", port);
        return serverSocket;
    }

    private void handleClientConnections(ServerSocket serverSocket) throws IOException {
        while (running) {
            Socket client = serverSocket.accept();
            System.out.println("Incoming request, forking out thread... [" + client.getPort() + "]");
            threadPool.submit(new ClientHandler(client, router));
        }
    }

    public void shutdown() {
        running = false;
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

//    public void start() throws IOException {
//        ServerSocket serverSocket = new ServerSocket(port);
//        System.out.println("HTTP server running on port " + port);
//
//        while (true) {
//            Socket client = serverSocket.accept();
//            threadPool.submit(new ClientHandler(client, router));
//        }
//    }

}
