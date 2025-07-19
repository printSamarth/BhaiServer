package com.myserver.http;

import java.io.BufferedWriter;
import java.io.IOException;

public class BhaiMila {

    private final BufferedWriter out;

    public BhaiMila(BufferedWriter out) {
        this.out = out;
    }

    public void bhaiBhejDe(int statusCode, String body) throws IOException {
        String statusLine = switch (statusCode) {
            case 200 -> "HTTP/1.1 200 OK";
            case 404 -> "HTTP/1.1 404 Not Found";
            default -> "HTTP/1.1 " + statusCode + " Custom Status";
        };

        out.write(statusLine + "\r\n");
        out.write("Content-Type: text/plain; charset=UTF-8\r\n");
        out.write("Content-Length: " + body.getBytes().length + "\r\n");
        out.write("Connection: keep-alive\r\n");
        out.write("Connection: close\r\n");
        out.write("\r\n");               // <--- End of headers
        out.write(body);                // <--- Body
        out.flush();                    // <--- Ensure data is sent
    }

}
