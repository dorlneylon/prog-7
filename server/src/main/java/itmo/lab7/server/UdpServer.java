package itmo.lab7.server;

import itmo.chunker.ChuckReceiver;
import itmo.lab7.basic.moviecollection.MovieCollection;
import itmo.lab7.database.Database;
import itmo.lab7.utils.types.SizedStack;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static itmo.lab7.commands.CommandHandler.setChannel;

/**
 * UdpServer class is responsible for handling the UDP connections.
 * It contains a static MovieCollection object, a static HashMap object to store the command history of each client,
 * an integer to store the port number, a DatagramChannel object, a Selector object, a static Map object to store the chunk lists of each client,
 * and an ExecutorService object.
 *
 * @author kxrxh
 */
public class UdpServer {
    //Declare a static MovieCollection object
    public static MovieCollection collection;

    //Declare a static HashMap object to store the command history of each client
    public static HashMap<ClientAddress, SizedStack<String>> commandHistory = new HashMap<>();

    //Declare an integer to store the port number
    private final int port;

    //Declare a DatagramChannel object
    private DatagramChannel datagramChannel;

    // Remote database
    private static Database database = null;

    //Declare a Selector object
    private Selector selector;

    //Declare a static Map object to store the chunk lists of each client
    private static final Map<InetSocketAddress, ChuckReceiver> chunkLists = new HashMap<>();

    //Declare an ExecutorService object
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     * Constructor for the UdpServer class.
     *
     * @param database   The database to be used by the server.
     * @param collection The collection of movies to be used by the server.
     * @param port       The port to be used by the server.
     */
    public UdpServer(Database database, MovieCollection collection, int port) {
        UdpServer.database = database;
        this.port = port;
        UdpServer.collection = collection;
    }

    /**
     * This method is used to run the server.
     */
    public void run() {
        // Create a new thread to handle the exit of the server
        ExitThread exitThread = new ExitThread();
        // Start the exit thread
        exitThread.start();
        try {
            // Open a datagram channel
            datagramChannel = DatagramChannel.open();
            // Set the datagram channel to non-blocking
            datagramChannel.configureBlocking(false);
            // Bind the datagram channel to the specified port
            datagramChannel.socket().bind(new InetSocketAddress(port));
            // Set the receive buffer size of the datagram channel to 65536
            datagramChannel.setOption(StandardSocketOptions.SO_RCVBUF, 65536);
            // Open a selector
            selector = Selector.open();
            // Register the datagram channel to the selector for read operations
            datagramChannel.register(selector, SelectionKey.OP_READ);
            // Log that the server is starting on the specified port
            ServerLogger.getLogger().info("Starting server on port " + port);
            // Loop until the server is stopped
            while (true) {
                // Wait for an event to occur
                selector.select();
                // Get the iterator of the selected keys
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                // Loop through the selected keys
                while (keys.hasNext()) {
                    // Get the selection key
                    SelectionKey key = keys.next();
                    // Remove the selection key from the iterator
                    keys.remove();
                    // Check if the selection key is valid
                    if (!key.isValid()) {
                        // Skip to the next selection key
                        continue;
                    }
                    // Check if the selection key is readable
                    if (key.isReadable()) {
                        // Get the datagram channel from the selection key
                        DatagramChannel keyChannel = (DatagramChannel) key.channel();
                        // Set the datagram channel
                        setChannel(keyChannel);
                        // Allocate a buffer of size 1028
                        ByteBuffer buffer = ByteBuffer.allocate(1028);
                        // Receive data from the datagram channel
                        InetSocketAddress inetSocketAddress = (InetSocketAddress) keyChannel.receive(buffer);
                        // Submit a new UDP handler to the executor service
                        executorService.submit(new UdpHandler(keyChannel, inetSocketAddress, buffer));
                    }
                }
            }
        } catch (IOException e) {
            // Log the exception
            ServerLogger.getLogger().warning("Exception: " + e.getMessage());
        } finally {
            try {
                // Close the selector
                selector.close();
                // Close the datagram channel
                datagramChannel.close();
            } catch (IOException e) {
                // Log the exception
                ServerLogger.getLogger().warning("Exception while closing channel or selector: " + e.getMessage());
            }
        }
    }

    /**
     * Gets the map of InetSocketAddress and ChuckReceiver objects.
     *
     * @return The map of InetSocketAddress and ChuckReceiver objects.
     */
    public synchronized static Map<InetSocketAddress, ChuckReceiver> getChunkLists() {
        return chunkLists;
    }

    /**
     * Returns the Database instance
     *
     * @return the Database instance
     */
    public static Database getDatabase() {
        return database;
    }
}