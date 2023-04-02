package itmo.lab7;

import itmo.lab7.core.ClientCore;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.NoSuchElementException;

public class ClientMain {
    private static final InetAddress serverAddress;

    static {
        try {
            serverAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    private static final int serverPort = 5050;

    public static void main(String[] args) {
        ClientCore core = new ClientCore(serverAddress, serverPort);
        try {
            core.run();
        } catch (NoSuchElementException e) {
            System.out.println("Ctrl-D detected.\n Shutting the client down...");
        }
    }
}
