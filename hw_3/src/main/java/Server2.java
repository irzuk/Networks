
import com.sun.net.httpserver.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Server2 {

    private static final Path dirPath = Paths.get("src/main/resources/");
    private static final File rootDir = new File(String.valueOf(dirPath));

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8500), 0);
        HttpContext context = server.createContext("/");
        int numOfThreads = 5;
        if (args.length > 0) {
            numOfThreads = Integer.parseInt(args[0]);
        }
        ThreadPoolExecutor executors = (ThreadPoolExecutor) Executors.newFixedThreadPool(numOfThreads);
        server.setExecutor(executors);
        context.setHandler(Server2::handleRequest);
        server.start();
    }

    private static void handleRequest(HttpExchange exchange) throws IOException {
        URI requestURI = exchange.getRequestURI();
        printRequestInfo(exchange);
        String query = requestURI.getQuery();
        for (String file : Objects.requireNonNull(rootDir.list())) {
            if (file.equals(query.substring(5))) {
                File f = new File(dirPath + "/" + file);
                System.out.println(f.getAbsoluteFile());
                FileInputStream ff = new FileInputStream(f);
                byte[] fff = ff.readAllBytes();
                exchange.sendResponseHeaders(200, fff.length);
                OutputStream os = exchange.getResponseBody();
                System.out.println(fff);
                os.write(fff);
                os.close();
                return;
            }
        }

        String response = "File not found " + query.substring(5);
        exchange.sendResponseHeaders(404, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private static void printRequestInfo(HttpExchange exchange) {
        System.out.println("-- headers --");
        Headers requestHeaders = exchange.getRequestHeaders();
        requestHeaders.entrySet().forEach(System.out::println);

        System.out.println("-- principle --");
        HttpPrincipal principal = exchange.getPrincipal();
        System.out.println(principal);

        System.out.println("-- HTTP method --");
        String requestMethod = exchange.getRequestMethod();
        System.out.println(requestMethod);

        System.out.println("-- query --");
        URI requestURI = exchange.getRequestURI();
        String query = requestURI.getQuery();
        System.out.println(query);
    }
}