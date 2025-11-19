package server;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.SQLOutput;

public class Server {
    private Server(){}

    public static HttpServer makeServer() throws IOException {
        String host = "localhost";
        InetSocketAddress address = new InetSocketAddress(host, 8089);

        System.out.format("Server started at http://%s:%s%n", address.getHostName(), address.getPort());
        HttpServer server = HttpServer.create(address, 50);
        System.out.println("\t\tDone!");
        return server;
    }
}
