package itmo.lab7.commands;

import itmo.chunker.Chunker;
import itmo.lab7.ServerMain;
import itmo.lab7.server.ServerLogger;
import itmo.lab7.xml.Xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.logging.Level;

import static itmo.lab7.server.UdpServer.collection;

/**
 * Handles a packet received from a client.
 */
public class CommandHandler {
    private static DatagramChannel channel;
    private static final int chunkSize = 1024;

    /**
     * Sets the DatagramChannel for the CommandHandler.
     *
     * @param channel The DatagramChannel to be set.
     */
    public static void setChannel(DatagramChannel channel) {
        CommandHandler.channel = channel;
    }

    /**
     * Handles a packet received from a client.
     *
     * @param sender The address of the sender
     * @param bytes  The bytes of the packet
     * @throws Exception If an error occurs while handling the packet
     */
    public static void handlePacket(InetSocketAddress sender, byte[] bytes) throws Exception {
        // Create an ObjectInputStream from the bytes received
        ObjectInputStream objectInputStream2 = new ObjectInputStream(new ByteArrayInputStream(bytes));
        // Read the object from the stream
        Command command = (Command) objectInputStream2.readObject();

        // Log the command type and sender
        ServerLogger.getLogger().log(Level.INFO, "Received command %s from %s".formatted(command.getCommandType(), sender));
        // Get the output message from the command
        byte[] output = command.execute().getMessage().getBytes();
        // Create a Chunker object with the output message and the chunk size
        Chunker chunker = new Chunker(output, chunkSize);
        // Create an iterator for the chunks
        var chunkIterator = chunker.newIterator();
        int c = 0;
        // Iterate through the chunks
        while (chunkIterator.hasNext()) {
            // Get the next chunk
            byte[] chunk = chunkIterator.next();
            if (++c % 50 == 0) {
                Thread.sleep(100);
            }
            // Send the chunk to the sender
            channel.send(ByteBuffer.wrap(chunk), sender);
        }
        // Write the collection to the XML file
        new Xml(new File(ServerMain.collectionFileName), true).newWriter().writeCollection(collection);
    }
}
