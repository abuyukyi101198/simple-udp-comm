package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.function.Supplier;


public class UDPServer extends Thread {
    private final int packetSize;
    private final int port;
    private final byte[] buffer;
    private final ByteBuffer byteBuffer;
    private final Supplier<? extends Number> extractor;


    public UDPServer(Class<? extends Number> dataType, int packetSize, int port) {
        this.packetSize = packetSize;
        this.port = port;


        int byteSize;
        if (Integer.class.equals(dataType))
            byteSize = Integer.BYTES;
        else if (Long.class.equals(dataType))
            byteSize = Long.BYTES;
        else if (Float.class.equals(dataType))
            byteSize = Float.BYTES;
        else
            byteSize = Double.BYTES;


        this.buffer = new byte[this.packetSize * byteSize];
        this.byteBuffer = ByteBuffer.wrap(this.buffer);


        if (Integer.class.equals(dataType))
            this.extractor = this.byteBuffer::getInt;
        else if (Long.class.equals(dataType))
            this.extractor = this.byteBuffer::getLong;
        else if (Float.class.equals(dataType))
            this.extractor = this.byteBuffer::getFloat;
        else
            this.extractor = this.byteBuffer::getDouble;


        System.out.println("Server is online...");
    }


    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket(this.port)) {
            DatagramPacket packet = new DatagramPacket(this.buffer, this.buffer.length);
            Number[] data = new Number[this.packetSize];
            String clientAddress;
            int clientPort;
            int size;


            while (true) {
                socket.receive(packet);


                clientAddress = packet.getAddress().getHostAddress();
                clientPort = packet.getPort();
                size = packet.getLength();


                extractData(data);
                printData(data, clientAddress, clientPort, size);
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void extractData(Number[] data) {
        for (int i = 0; i < data.length; i++) {
            data[i] = extractor.get();
        }
        this.byteBuffer.clear();
    }


    private void printData(Number[] data, String clientAddress, int clientPort, int size) {
        System.out.println("\nPacket received at " + new Timestamp(System.currentTimeMillis()));
        System.out.println("Sender info: " + clientAddress + ":" + clientPort);
        System.out.println("Data: " + data[0] + ", " + data[1] + ", " + data[2] + "... (" + size + " bytes)");
    }
}

