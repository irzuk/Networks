import java.io.*;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Scanner;

public class WebClient {

    private Socket clientSocket;
    private PrintWriter out;
    private InputStream in;
    private String fileName;


    public WebClient(String ip, int port, String file) throws IOException {
        fileName = file;
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = clientSocket.getInputStream();
    }

    public void start() throws IOException {
        System.out.println("Start client");
        out.println("GET /?file=" + fileName + " HTTP/1.1");
        out.flush();
        in.transferTo(System.out);
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.out.println("Not enough arguments, try again: <host> <port> <fileName>");
            return;
        }
        WebClient client;
        client = new WebClient(args[0], Integer.parseInt(args[1]), args[2]);
        client.start();
    }
}
