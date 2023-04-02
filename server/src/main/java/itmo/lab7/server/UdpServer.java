package itmo.lab7.server;

import itmo.chunker.ChuckReceiver;
import itmo.lab7.basic.moviecollection.MovieCollection;
import itmo.lab7.utils.types.SizedStack;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static itmo.lab7.commands.CommandHandler.handlePacket;
import static itmo.lab7.commands.CommandHandler.setChannel;

public class UdpServer {
    public static MovieCollection collection;
    public static HashMap<ClientAddress, SizedStack<String>> commandHistory = new HashMap<>();
    private final int port;
    private DatagramChannel datagramChannel;
    private Selector selector;

    public UdpServer(MovieCollection collection, int port) {
        this.port = port;
        UdpServer.collection = collection;
    }

    public void run() {
        ExitThread exitThread = new ExitThread();
        exitThread.start();
        try {
            datagramChannel = DatagramChannel.open();
            datagramChannel.configureBlocking(false);
            datagramChannel.socket().bind(new InetSocketAddress(port));
            selector = Selector.open();
            datagramChannel.register(selector, SelectionKey.OP_READ);
            ServerLogger.getLogger().info("Starting server on port " + port);
            Map<InetSocketAddress, ChuckReceiver> chunkLists = new HashMap<>();
            while (true) {
                selector.select();
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();
                    if (!key.isValid()) {
                        continue;
                    }
                    if (key.isReadable()) {
                        DatagramChannel keyChannel = (DatagramChannel) key.channel();
                        setChannel(keyChannel);
                        ByteBuffer buffer = ByteBuffer.allocate(1028);
                        InetSocketAddress inetSocketAddress = (InetSocketAddress) keyChannel.receive(buffer);
                        ClientAddress clientAddress = new ClientAddress(inetSocketAddress.getAddress(), inetSocketAddress.getPort());
                        if (!commandHistory.containsKey(clientAddress)) {
                            commandHistory.put(clientAddress, new SizedStack<>(7));
                        }

                        ChuckReceiver receiver = chunkLists.get(inetSocketAddress);
                        if (receiver == null) {
                            receiver = new ChuckReceiver();
                            chunkLists.put(inetSocketAddress, receiver);
                        }

                        receiver.add(Arrays.copyOfRange(buffer.array(), 0, buffer.position()));
                        if (receiver.isReceived()) {
                            // We've received the last packet, so handle the message
                            try {
                                handlePacket(inetSocketAddress, receiver.getAllChunks());
                            } catch (Exception e) {
                                keyChannel.send(ByteBuffer.wrap("ERROR: Something went wrong...".getBytes()), inetSocketAddress);
                                ServerLogger.getLogger().warning(e.toString());
                            }
                            chunkLists.remove(inetSocketAddress);
                        }
                    }
                }
            }
        } catch (IOException e) {
            ServerLogger.getLogger().warning("Exception: " + e.getMessage());
        } finally {
            try {
                selector.close();
                datagramChannel.close();
            } catch (IOException e) {
                ServerLogger.getLogger().warning("Exception while closing channel or selector: " + e.getMessage());
            }
        }
    }

}