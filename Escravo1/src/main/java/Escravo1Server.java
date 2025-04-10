import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;

public class Escravo1Server {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);
        server.createContext("/isAvailable", new IsAvailableHandler());
        server.createContext("/letras", new LetrasHandler());
        server.setExecutor(null); 
        System.out.println("Escravo1 rodando na porta 8081");
        server.start();
    }


    static class IsAvailableHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            String response = "OK"; 
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    static class LetrasHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            InputStream is = exchange.getRequestBody();
            StringBuilder sb = new StringBuilder();
            int i;
            while ((i = is.read()) != -1) {
                sb.append((char) i);
            }

            String texto = sb.toString();

            long count = texto.chars().filter(c -> Character.isLetter(c) && Character.isAlphabetic(c)).count();

            String response = "Quantidade de letras: " + count;
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
