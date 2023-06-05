package server;

public class Main {
    public static void main(String[] args) {
        UDPServer server = new UDPServer(Double.class, 150, 5000);
        server.start();
    }
}
