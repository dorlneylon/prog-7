package itmo.lab7.commands;

import java.io.Serial;
import java.io.Serializable;
import java.util.regex.Pattern;

public class Request implements Serializable {
    @Serial
    private static final long serialVersionUID = -7403418586909795322L;
    private final Command command;
    private final String login;

    public Request(Command command, String login) {
        this.command = command;
        this.login = login;
    }

    public Request(Command command) {
        this(command, null);
    }

    public Command getCommand() {
        return command;
    }

    public String getName() {
        return Pattern.compile("(.*):(.*)$").matcher(login).group(1);
    }

    public boolean isUserAuthorized() {
        return false;
    }
}
