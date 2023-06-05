package client;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Supplier;


public class UDPClient extends Thread {
    private final int packetSize;
    private final byte[] buffer;
    private final ByteBuffer byteBuffer;
    private final Supplier<? extends Number> generator;
    private final Consumer<Number> converter;


    public UDPClient(Class<? extends Number> dataType, int packetSize) {
        this.packetSize = packetSize;


        int byteSize;
        Random random = new Random();
        if (Integer.class.equals(dataType)) {
            byteSize = Integer.BYTES;
            this.generator = random::nextInt;
        }
        else if (Long.class.equals(dataType)) {
            byteSize = Long.BYTES;
            this.generator = random::nextLong;
        }
        else if (Float.class.equals(dataType)) {
            byteSize = Float.BYTES;
            this.generator = random::nextFloat;
        }
        else {
            byteSize = Double.BYTES;
            this.generator = random::nextDouble;
        }


        this.buffer = new byte[this.packetSize * byteSize];
        this.byteBuffer = ByteBuffer.wrap(this.buffer);


        if (Integer.class.equals(dataType))
            this.converter = (number) -> byteBuffer.putInt(number.intValue());
        else if (Long.class.equals(dataType))
            this.converter = (number) -> byteBuffer.putLong(number.longValue());
        else if (Float.class.equals(dataType))
            this.converter = (number) -> byteBuffer.putFloat(number.floatValue());
        else
            this.converter = (number) -> byteBuffer.putDouble(number.doubleValue());
    }


    @Override
    public void run() {
        send(5000);
    }


    public void send(int serverPort) {
        try (DatagramSocket serverSocket = new DatagramSocket()) {
            InetAddress serverInetAddress = InetAddress.getLocalHost();
            Number[] data = generateData();
            prepareByteArray(data);


            DatagramPacket packet = new DatagramPacket(this.buffer, this.buffer.length, serverInetAddress, serverPort);


            while (true) {
                serverSocket.send(packet);
                printData(data);
            }




        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private Number[] generateData() {
        Number[] data = new Number[this.packetSize];


        for (int i = 0; i < data.length; i++) {
            data[i] = this.generator.get();
        }


        return data;
    }


    private void prepareByteArray(Number[] data) {
        for (Number value : data) {
            this.converter.accept(value);
        }
        this.byteBuffer.clear();
    }


    private void printData(Number[] data) {
        System.out.println("\nPacket sent at " + new Timestamp(System.currentTimeMillis()));
        System.out.println("Data: " + data[0] + ", " + data[1] + ", " + data[2] + "...");
    }
}

