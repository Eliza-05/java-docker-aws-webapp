import org.junit.jupiter.api.Test;
import edu.eci.tdse.server.HttpServer;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class HttpServerConcurrencyTest {

    private static void startTestServer(int port) {
        HttpServer.port(port);
        HttpServer.get("/test", req -> "ok");
        new Thread(() -> {
            try { HttpServer.start(); }
            catch (Exception ignored) {}
        }).start();

        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
    }

    private String sendRequest(int port, String path) throws IOException {
        try (Socket socket = new Socket("localhost", port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(socket.getInputStream()))) {

            out.print("GET " + path + " HTTP/1.1\r\n");
            out.print("Host: localhost\r\n");
            out.print("Connection: close\r\n");
            out.print("\r\n");
            out.flush();

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line).append("\n");
            }
            return response.toString();
        }
    }

    @Test
    void serverHandlesMultipleConcurrentRequests() throws Exception {
        int testPort = 8181;
        startTestServer(testPort);

        int numRequests = 10;
        ExecutorService clients = Executors.newFixedThreadPool(numRequests);
        List<Future<String>> futures = new ArrayList<>();

        for (int i = 0; i < numRequests; i++) {
            futures.add(clients.submit(() -> sendRequest(testPort, "/test")));
        }

        int successCount = 0;
        for (Future<String> future : futures) {
            String response = future.get(5, TimeUnit.SECONDS);
            if (response.contains("200 OK")) successCount++;
        }

        clients.shutdown();
        assertEquals(numRequests, successCount,
                "All concurrent requests should receive 200 OK");
    }

    @Test
    void serverReturns404ForUnknownPath() throws Exception {
        int testPort = 8182;
        HttpServer.port(testPort);
        new Thread(() -> {
            try { HttpServer.start(); }
            catch (Exception ignored) {}
        }).start();
        Thread.sleep(500);

        String response = sendRequest(testPort, "/no-existe");
        assertTrue(response.contains("404"), "Unknown path should return 404");
    }
}