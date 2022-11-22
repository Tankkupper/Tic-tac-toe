package application.client;

import java.io.IOException;
import java.net.Socket;

public class ClientTest {
    public static void main(String[] args) throws IOException, InterruptedException {
        Socket socket = new Socket("localhost", 12324);
        Thread.sleep(100000);
    }
}
