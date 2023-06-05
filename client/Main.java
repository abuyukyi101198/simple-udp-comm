package client;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        for (int i=0; i<4; i++) {
            new UDPClient(Double.class, 150).start();
            Thread.sleep(100);
        }
    }
}
