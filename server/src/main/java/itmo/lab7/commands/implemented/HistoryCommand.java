package itmo.lab7.commands.implemented;

import itmo.lab7.commands.Action;
import itmo.lab7.server.ClientAddress;
import itmo.lab7.server.UdpServer;
import itmo.lab7.server.response.Color;
import itmo.lab7.server.response.Response;
import itmo.lab7.server.response.ResponseType;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Arrays;

import static itmo.lab7.server.UdpServer.commandHistory;

public final class HistoryCommand implements Action {

    private final String username;

    public HistoryCommand(String username) {
        this.username = username;
    }

    @Override
    public Response run(String username) {
        return new Response(Color.GREEN + Arrays.stream(UdpServer.getDatabase().getCommandHistory(username)).reduce("", (s1, s2) -> s1 + "\n" + s2) + Color.RESET, ResponseType.INFO);
    }
}
