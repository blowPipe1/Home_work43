package Handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.URI;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileHandler  implements HttpHandler {
    private static final String dataPath = "src/data";

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        URI uri = exchange.getRequestURI();
        Path filePath = Paths.get(dataPath, uri.getPath()).normalize();
        File file = filePath.toFile();

        if (file.exists() && file.isFile()) {
            String contentType = URLConnection.guessContentTypeFromName(file.getName());
            if (contentType == null) {
                contentType = "text/plain";
            }
            sendResponse(exchange, 200, contentType, new FileInputStream(file));
        } else {
            String response = "404 Not Found: Такого документа нет";
            sendResponse(exchange, 404, "text/plain; charset=UTF-8", new ByteArrayInputStream(response.getBytes(StandardCharsets.UTF_8)));
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String contentType, InputStream is) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", contentType);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int nRead;
        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        byte[] responseBytes = buffer.toByteArray();
        is.close();

        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        } finally {
            exchange.close();
        }
    }
}
