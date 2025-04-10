import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.concurrent.*;

public class MestreServer {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/processar", new ProcessarHandler());
        server.setExecutor(Executors.newFixedThreadPool(4)); // Executor para lidar com threads
        System.out.println("Servidor Mestre rodando na porta 8080");
        server.start();
    }

    static class ProcessarHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            // Lê o corpo da requisição
            InputStream is = exchange.getRequestBody();
            StringBuilder sb = new StringBuilder();
            int i;
            while ((i = is.read()) != -1) {
                sb.append((char) i);
            }
            String texto = sb.toString();

            // Envia texto aos escravos em paralelo
            ExecutorService executor = Executors.newFixedThreadPool(2);
            Future<String> letrasFuture = executor.submit(() -> enviarParaEscravo("http://escravo1:8081/letras", texto));
            Future<String> numerosFuture = executor.submit(() -> enviarParaEscravo("http://escravo2:8082/numeros", texto));


            try {
                String resultadoLetras = letrasFuture.get();
                String resultadoNumeros = numerosFuture.get();
                String respostaFinal = resultadoLetras + " | " + resultadoNumeros;

                exchange.sendResponseHeaders(200, respostaFinal.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(respostaFinal.getBytes());
                os.close();
            } catch (Exception e) {
                String erro = "Erro ao processar dados: " + e.getMessage();
                exchange.sendResponseHeaders(500, erro.getBytes().length);
                exchange.getResponseBody().write(erro.getBytes());
                exchange.close();
            }
        }

        private String enviarParaEscravo(String urlStr, String texto) {
            try {
                URL url = new URL(urlStr);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoOutput(true);
                OutputStream os = con.getOutputStream();
                os.write(texto.getBytes());
                os.flush();
                os.close();

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                return content.toString();
            } catch (IOException e) {
                return "Erro ao contatar " + urlStr + ": " + e.getMessage();
            }
        }
    }
}
