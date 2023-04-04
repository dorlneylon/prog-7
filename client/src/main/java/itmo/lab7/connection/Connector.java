package itmo.lab7.connection;

import itmo.chunker.ChuckReceiver;
import itmo.chunker.Chunker;
import itmo.lab7.basic.utils.terminal.Colors;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Connector is used to connect to a remote server
 */
public class Connector {
    /**
     * Socket for UDP connection
     */
    private static DatagramSocket socket;
    private final InetAddress address;
    private final int port;
    private final static int socketTimeout = 8000;

    private int chunkSize;


    /**
     * Connector constructor with specified server port
     *
     * @param port server port
     * @throws Exception Socket exception
     */
    public Connector(InetAddress address, int port) throws Exception {
        socket = new DatagramSocket();
        socket.setSoTimeout(socketTimeout);
        this.address = address;
        this.port = port;
    }

    /**
     * Return port of client
     *
     * @return localhost port
     */
    public static int getPort() {
        return socket.getLocalPort();
    }

    public void setBufferSize(int size) throws SocketException {
        socket.setReceiveBufferSize(size);
        socket.setSendBufferSize(size);
        this.chunkSize = 1024;
    }

    /**
     * Sends string to remote server
     *
     * @param message string message
     * @throws Exception sending exception
     */
    public void send(String message) throws Exception {
        this.send(message.getBytes());
    }

    /**
     * Sends dataBytes to remote server
     *
     * @throws Exception sending exceptions
     */
    public void send(byte[] dataBytes) throws Exception {
        Chunker dataChunker = new Chunker(dataBytes, chunkSize);
        var chunkIterator = dataChunker.newIterator();
        short totalChunks = (short) ((int) Math.ceil((double) dataBytes.length / (double) this.chunkSize));
        AtomicInteger c = new AtomicInteger(0);
        while (chunkIterator.hasNext()) {
            if (c.incrementAndGet() % 50 == 0) {
                System.out.printf("%sSending chunks:%s %d/%d kb\r", Colors.AsciiPurple, Colors.AsciiReset, c.get(), totalChunks);
                Thread.sleep(100);
            }
            byte[] chunk = chunkIterator.next();
            DatagramPacket dataPacket = new DatagramPacket(chunk, chunk.length, this.address, port);
            socket.send(dataPacket);
        }
    }

//    public void send(byte[] bytes) throws Exception {
//        int numChunks = (int) Math.ceil((double) bytes.length / chunkSize);
//        for (int i = 0; i < numChunks; i++) {
//            int offset = i * chunkSize;
//            int length = Math.min(bytes.length - offset, chunkSize);
//            byte[] chunk = new byte[length + 1];
//            chunk[length] = (numChunks == 1 || i + 1 == numChunks) ? (byte) 0 : (byte) 1; // has next flag
//            System.arraycopy(bytes, offset, chunk, 0, length);
//            DatagramPacket datagramPacket = new DatagramPacket(chunk, length + 1, this.address, port);
//            socket.send(datagramPacket);
//            if (i != 0 && i % 100 == 0) {
//                System.out.printf("%sSending chunks:%s %d/%d kb\r", Colors.AsciiPurple, Colors.AsciiReset, i + 1, numChunks);
//                Thread.sleep(100);
//            }
//        }
//        System.out.print("");
//    }

    /**
     * Receives bytes from remote server and transforms them into string
     *
     * @return string message
     * @throws IOException Receiving exception
     */
    public String receive() throws IOException {
        ChuckReceiver receiver = new ChuckReceiver();
        byte[] buffer = new byte[chunkSize + 4];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        do {
            socket.receive(packet);
            receiver.add(Arrays.copyOf(packet.getData(), packet.getLength()));
        } while (!receiver.isReceived());
        return new String(receiver.getAllChunks(), StandardCharsets.UTF_8);
    }
}
