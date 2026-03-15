package edu.eci.tdse.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class HttpServer {

    private static final Map<String, Function<HttpRequest, String>> getRoutes = new HashMap<>();
    private static final StaticFileService staticFiles = new StaticFileService();
    private static int port = 8080;

    private static volatile boolean running = true;

    private static final int THREAD_POOL_SIZE = 10;

    public static void port(int p) { port = p; }

    public static void get(String path, Function<HttpRequest, String> handler) {
        getRoutes.put(path, handler);
    }

    public static void start() throws IOException {
        ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n[ShutdownHook] Graceful shutdown initiated...");
            running = false;
            threadPool.shutdown();
            try {
                if (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
                    System.out.println("[ShutdownHook] Forcing shutdown after timeout...");
                    threadPool.shutdownNow();
                }
            } catch (InterruptedException e) {
                threadPool.shutdownNow();
                Thread.currentThread().interrupt();
            }
            System.out.println("[ShutdownHook] Server stopped gracefully.");
        }));

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setSoTimeout(1000); 
            System.out.println("Server running on http://localhost:" + port);
            System.out.println("Thread pool size: " + THREAD_POOL_SIZE);
            System.out.println("Registered routes: " + getRoutes.keySet());

            
            while (running) {
                try {
                    Socket client = serverSocket.accept();
                    
                    threadPool.submit(() -> handleClient(client));
                } catch (java.net.SocketTimeoutException e) {
                }
            }
        }
    }

    private static void handleClient(Socket client) {
        try (client;
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));
             OutputStream out = client.getOutputStream()) {

            HttpRequest req = HttpParser.parse(in);
            if (req == null) return;

            Function<HttpRequest, String> handler = getRoutes.get(req.path());
            if (handler != null) {
                String body = handler.apply(req);
                if (body == null) body = "";
                writeResponse(out, 200, "text/html; charset=utf-8",
                        body.getBytes(StandardCharsets.UTF_8));
                return;
            }

            StaticFileService.StaticResult file = staticFiles.tryServe(req.path());
            if (file != null) {
                writeResponse(out, file.status(), file.contentType(), file.body());
                return;
            }

            String notFound = "<h1>404 Not Found</h1><p>Path: " + req.path() + "</p>";
            writeResponse(out, 404, "text/html; charset=utf-8",
                    notFound.getBytes(StandardCharsets.UTF_8));

        } catch (Exception e) {
            System.err.println("Error handling client: " + e.getMessage());
        }
    }

    private static void writeResponse(OutputStream out, int status,
                                      String contentType, byte[] body) throws IOException {
        String statusText = switch (status) {
            case 200 -> "OK";
            case 404 -> "Not Found";
            case 500 -> "Internal Server Error";
            default  -> "OK";
        };

        String headers =
                "HTTP/1.1 " + status + " " + statusText + "\r\n" +
                "Content-Type: " + contentType + "\r\n" +
                "Content-Length: " + body.length + "\r\n" +
                "Connection: close\r\n" +
                "\r\n";

        out.write(headers.getBytes(StandardCharsets.UTF_8));
        out.write(body);
        out.flush();
    }
}