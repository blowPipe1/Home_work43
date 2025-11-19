import Handler.FileHandler;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import server.Server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        try {
            HttpServer server = Server.makeServer();
            initRoutes(server);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void initRoutes(HttpServer server) {
        server.createContext("/", new FileHandler());
        server.createContext("/apps/", Main::handleAppsRequest);
        server.createContext("/apps/profile", Main::handleProfileRequest);
        server.createContext("/classwork", Main::handleRequest);
    }

    private static void handleRequest(HttpExchange exchange) {
        try {
            StringWriter sw = new StringWriter();
            try (PrintWriter writer = new PrintWriter(sw)) {
                String method = exchange.getRequestMethod();
                URI uri = exchange.getRequestURI();
                String path = exchange.getHttpContext().getPath();

                write(writer, "HTTP Method", method);
                write(writer, "Request", uri.toString());
                write(writer, "Handler Path", path);
                writeHeaders(writer, "Request Headers", exchange.getRequestHeaders());
            }

            String response = sw.toString();
            byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
            int responseLength = responseBytes.length;
            int responseCode = 200;

            exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
            exchange.sendResponseHeaders(responseCode, responseLength);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            exchange.close();
        }
    }

    private static void writeHeaders(PrintWriter writer, String type, Headers requestHeaders) {
        write(writer, type, "");
        requestHeaders.forEach((k, v) -> write(writer, "\t" + k, v.toString()));
    }

    private static void write(PrintWriter writer, String label, String value) {
        String data = String.format("%s : %s%n", label, value);
        writer.write(data);
    }

    private static void handleRootRequest(HttpExchange exchange) throws IOException {
        String response = "Это корневая страница (/)";
        sendResponse(exchange, response);
    }

    private static void handleAppsRequest(HttpExchange exchange) throws IOException {
        String response = "Это страница приложений (/apps/)";
        sendResponse(exchange, response);
    }

    private static void handleProfileRequest(HttpExchange exchange) throws IOException {
        String response = "Это страница профиля (/apps/profile)";
        sendResponse(exchange, response);
    }

    private static void sendResponse(HttpExchange exchange, String response) throws IOException {
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
        exchange.sendResponseHeaders(200, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
        exchange.close();
    }

}


