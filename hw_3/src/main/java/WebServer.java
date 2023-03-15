import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class WebServer {
    private ServerSocket serverSocket;
    private ThreadPoolExecutor executors;


    private WebServer(Integer numOfThreads) throws IOException {
        executors = (ThreadPoolExecutor) Executors.newFixedThreadPool(numOfThreads);
        serverSocket = new ServerSocket(8500);
    }

    public void start() {
        try {
            System.out.println("Server started");
            while (true)
                executors.execute(new ClientHandler(serverSocket.accept()));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            stop();
        }
    }

    public void stop() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws IOException {
        int numOfThreads = 5;
        if (args.length > 0) {
            numOfThreads = Integer.parseInt(args[0]);
        }
        WebServer server = new WebServer(numOfThreads);
        server.start();
    }
}

class ClientHandler implements Runnable {

    private Socket clientSocket;
    private OutputStream out;
    private BufferedReader in;
    private final Path dirPath = Paths.get("src/main/resources/");
    private final File rootDir = new File(String.valueOf(dirPath));
    private String CRLF = "\r\n";

    public ClientHandler(Socket socket) {
        clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            System.out.println("Got a client");
            out = clientSocket.getOutputStream();
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String inputLine;
            StringBuilder input = new StringBuilder();
            System.out.println("Waiting for request");
            inputLine = in.readLine();
            String[] tokens = inputLine.toString().split(" ");
            if (tokens[0].equals("GET")) {
                System.out.println("Response to " + tokens[0]);
                System.out.println(tokens[1]);
                if (tokens[1].equals("/favicon.ico")) {
                    return;
                }
                String fileName = tokens[1].split("=")[1];
                String statusLine = null;
                String contentTypeLine = null;
                String entityBody = null;
                for (String file : Objects.requireNonNull(rootDir.list())) {
                    if (file.equals(fileName)) {
                        statusLine = "HTTP/1.0 200 OK" + CRLF;
                        contentTypeLine = "Content-Type: " + "text" + CRLF;
                        entityBody = dirPath + "/" + file;
                       // System.out.println(entityBody);
                       // ff.transferTo(out);
                        break;
                    }
                }
                // file not found
                if(statusLine == null) {
                    statusLine = "HTTP/1.0 404 Not Found" + CRLF;
                    contentTypeLine = "Content-Type: text/html" + CRLF;
                }
                StringBuilder builder = new StringBuilder();
                builder.append(statusLine);
                builder.append(contentTypeLine);
                builder.append(CRLF);

                out.write(builder.toString().getBytes());

                if(entityBody == null) {
                    entityBody = "<HTML>" +
                            "<HEAD><TITLE>" + fileName + " Not Found</TITLE></HEAD>" +
                            "<BODY>Not Found</BODY></HTML>";
                    out.write(entityBody.getBytes());
                } else {
                    File f = new File(entityBody);
                    FileInputStream fstream = new FileInputStream(f);
                    fstream.transferTo(out);
                }

                out.flush();
            }
            in.close();
            out.close();
            clientSocket.close();
            System.out.println("Finish response");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
