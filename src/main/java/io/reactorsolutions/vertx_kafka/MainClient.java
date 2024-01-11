package io.reactorsolutions.vertx_kafka;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;

public class MainClient {

  public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
    HttpClient client = HttpClient.newHttpClient();
    String[] names = {"Juan", "Ana", "Pedro", "María", "Luis", "Elena"};
    for (int i = 0;i < names.length;i++){
      String name = randomName(names);
      HttpRequest request = HttpRequest.newBuilder()
        .uri(new URI("http://localhost:8080/users/"+name))
        .POST(HttpRequest.BodyPublishers.ofString(name))
        .build();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      Thread.sleep(2000);
    }
  }

  private static String randomName(String[] names) {
    Random random = new Random();
    int index = random.nextInt(names.length);
    return names[index];
  }
}
