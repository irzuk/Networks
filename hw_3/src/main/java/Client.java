import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Scanner;


public class Client {

    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length < 2) {
            System.out.println("Not enough arguments, try again: <host> <port>");
            return;
        }
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(20))
                .proxy(ProxySelector.of(new InetSocketAddress("localhost", 8500)))
                .build();

        Scanner myObj = new Scanner(System.in);  // Create a Scanner object
        String file;
        while (true) {
            if (args.length == 3) {
                file = args[2];
            } else {
                file = myObj.nextLine();
            }
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(args[0] + args[1] + "/example?file=" + file))
                    .timeout(Duration.ofMinutes(2))
                    .header("Content-Type", "application/json")
                    .build();
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());
            System.err.println("Response status: " + response.statusCode());
            System.out.println(response.body());
            if (args.length == 3) {
                return;
            }
        }

    }
}
