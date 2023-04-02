package itmo.lab6.commands;

import itmo.chunker.Chunker;
import itmo.lab6.ServerMain;
import itmo.lab6.server.ClientAddress;
import itmo.lab6.server.ServerLogger;
import itmo.lab6.xml.Xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.logging.Level;

import static itmo.lab6.server.UdpServer.collection;
import static itmo.lab6.server.UdpServer.commandHistory;

public class CommandHandler {
    private static DatagramChannel channel;
    private static final int chunkSize = 1024;

    public static void setChannel(DatagramChannel channel) {
        CommandHandler.channel = channel;
    }

    public static void handlePacket(InetSocketAddress sender, byte[] bytes) throws Exception {
        ObjectInputStream objectInputStream2 = new ObjectInputStream(new ByteArrayInputStream(bytes));
        Command command = (Command) objectInputStream2.readObject();

        ServerLogger.getLogger().log(Level.INFO, "Received command %s from %s".formatted(command.getCommandType(), sender));
        byte[] output = command.execute().getMessage().getBytes();
        Chunker chunker = new Chunker(output, chunkSize);
        var chunkIterator = chunker.newIterator();
        while (chunkIterator.hasNext()) {
            byte[] chunk = chunkIterator.next();
            channel.send(ByteBuffer.wrap(chunk), sender);
        }

        if (!command.getCommandType().equals(CommandType.SERVICE)) {
            commandHistory.get(new ClientAddress(sender.getAddress(), sender.getPort())).push(command.getCommandType().toString());
        }

        new Xml(new File(ServerMain.collectionFileName), true).newWriter().writeCollection(collection);
    }
}
