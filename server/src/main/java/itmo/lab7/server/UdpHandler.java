package itmo.lab7.server;

import itmo.chunker.ChuckReceiver;
import itmo.lab7.commands.Request;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Arrays;

import static itmo.lab7.commands.CommandHandler.handlePacket;

/**
 * UdpHandler class
 * <p>
 * This class is responsible for handling incoming UDP packets.
 */
public class UdpHandler implements Runnable {

    private final InetSocketAddress clientAddress;
    private final DatagramChannel keyChannel;
    private final ByteBuffer buffer;

    /**
     * Constructor for UdpHandler
     *
     * @param keyChannel The DatagramChannel associated with the UdpHandler
     * @param sender     The InetSocketAddress of the sender
     * @param buffer     The ByteBuffer associated with the UdpHandler
     */
    public UdpHandler(DatagramChannel keyChannel, InetSocketAddress sender, ByteBuffer buffer) {
        this.clientAddress = sender;
        this.buffer = buffer;
        this.keyChannel = keyChannel;
    }

    /**
     * Handles the packet received from the client.
     */
    @Override
    public void run() {
        ChuckReceiver receiver;
        synchronized (UdpServer.getChunkLists()) {
            receiver = UdpServer.getChunkLists().get(clientAddress);
            // If the receiver is null, create a new one and add it to the map
            if (receiver == null) {
                receiver = new ChuckReceiver();
                UdpServer.getChunkLists().put(clientAddress, receiver);
            }
            receiver.add(Arrays.copyOfRange(buffer.array(), 0, buffer.position()));
        }
        if (receiver.isReceived()) {
            try {
                handlePacket(clientAddress, receiver.getAllChunks());
            } catch (Exception e) {
                // If something goes wrong, send an error message to the client
                try {
                    keyChannel.send(ByteBuffer.wrap("ERROR: Something went wrong...".getBytes()), clientAddress);
                } catch (IOException ignore) {
                }
                // Log the error
                ServerLogger.getLogger().warning(e.toString());
            } finally {
                UdpServer.getChunkLists().remove(clientAddress);
            }
        }
    }
}
