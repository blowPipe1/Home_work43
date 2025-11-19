import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import server.Server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        try{
            HttpServer server = Server.makeServer();
            initRoutes(server);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void initRoutes(HttpServer server) {
        server.createContext("/", Main::handleRequest);
        server.createContext("/apps/", Main::handleRequest);
        server.createContext("/apps/profile", Main::handleRequest);
    }

    private static void handleRequest(HttpExchange exchange){
        try{
            exchange.getRequestHeaders().add("Content-Type", "text/plain; charset=utf-8");
            int responseCode = 200;
            int length = 0;
            exchange.sendResponseHeaders(responseCode, length);

            try(PrintWriter writer = getWriterFrom(exchange)){
                String method = exchange.getRequestMethod();
                URI uri = exchange.getRequestURI();
                String path = exchange.getHttpContext().getPath();

                write(writer, "HTTP Method", method);
                write(writer, "Request", uri.toString());
                write(writer, "Handler", path);
                writeHeaders(writer, "Request Headers", exchange.getRequestHeaders());
                writer.flush();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private static void writeHeaders(PrintWriter writer, String type, Headers requestHeaders) {
        write(writer,type, "");
        requestHeaders.forEach((k,v) -> write(writer,"\t"+k,v.toString()));
    }

    private static void write(PrintWriter writer, String msg, String method) {
        String data = String.format("%s : %s%n", method, msg);
        writer.write(data);
    }

    private static PrintWriter getWriterFrom(HttpExchange exchange){
        OutputStream os = exchange.getResponseBody();
        Charset charset = StandardCharsets.UTF_8;
        return new PrintWriter(new OutputStreamWriter(os, charset));
    }
}