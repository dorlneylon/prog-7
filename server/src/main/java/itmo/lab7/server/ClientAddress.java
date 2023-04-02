package itmo.lab7.server;

import java.net.InetAddress;

public record ClientAddress(InetAddress address, int port) {
}
