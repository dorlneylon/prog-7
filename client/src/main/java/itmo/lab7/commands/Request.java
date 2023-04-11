package itmo.lab7.commands;

import java.io.Serial;
import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * This class represents a request sent from the client to the server.
 * It contains the command to be executed and the login of the user who sent the request.
 */
public class Request implements Serializable {
    @Serial
    private static final long serialVersionUID = -7403418586909795322L;
    private final Command command;
    private final String login;


    /**
     * Constructor for Request object
     *
     * @param command the command to be executed
     * @param login   the login of the user making the request
     */
    public Request(Command command, String login) {
        this.command = command;
        this.login = login;
    }

    /**
     * Constructs a new Request object with the given command and no parameters.
     *
     * @param command The command to be executed.
     */
    public Request(Command command) {
        this(command, CommandFactory.getName());
    }

    /**
     * Gets the command associated with this object.
     *
     * @return The command associated with this object.
     */
    public Command getCommand() {
        return command;
    }

    /**
     * Returns the name of the user
     *
     * @return the name of the user
     */
    public String getName() {
        return Pattern.compile("(.*):(.*)$").matcher(login).group(1);
    }

    public boolean isUserAuthorized() {
        return false;
    }
}
