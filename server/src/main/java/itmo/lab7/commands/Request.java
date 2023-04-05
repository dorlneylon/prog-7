package itmo.lab7.commands;

import itmo.lab7.server.UdpServer;

import java.io.Serial;
import java.io.Serializable;

public class Request implements Serializable {
    @Serial
    private static final long serialVersionUID = -7403418586909795322L;
    private final Command command;
    private final String login;

    public Request(Command command, String login) {
        this.command = command;
        this.login = login;
    }

    public Command getCommand() {
        return command;
    }

    public String getUserName() {
        return login;
    }

    public boolean isUserAuthorized() {
        return UdpServer.getDatabase().isUserExist(login);
    }
}
