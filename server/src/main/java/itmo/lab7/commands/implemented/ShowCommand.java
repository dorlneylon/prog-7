package itmo.lab7.commands.implemented;

import itmo.lab7.commands.Action;
import itmo.lab7.server.response.MessagePainter;
import itmo.lab7.server.response.Response;
import itmo.lab7.server.response.ResponseType;

import java.util.Arrays;

import static itmo.lab7.server.UdpServer.collection;
import static java.lang.Math.min;

/**
 * Class that implements the {@link Action} interface and is used to show the elements of the collection.
 */
public class ShowCommand implements Action {

    private final int index;
    private boolean isScript = false;

    /**
     * Constructor for ShowCommand
     *
     * @param index the index of the item to be shown
     */
    public ShowCommand(Integer index) {
        this.index = index;
    }

    /**
     * Constructs a new {@code ShowCommand} with the given {@code id}.
     * Sets index to 0;
     */
    public ShowCommand() {
        this(0);
    }

    /**
     * Constructor for ShowCommand.
     *
     * @param isScript Boolean value indicating whether the command is being executed from a script.
     */
    public ShowCommand(Boolean isScript) {
        this();
        this.isScript = isScript;
    }

    /**
     * Runs the command.
     *
     * @return The response of the command.
     */
    @Override
    public Response run() {
        if (collection.isEmpty()) return new Response("Collection is empty", ResponseType.SUCCESS);

        String test = MessagePainter.ColoredInfoMessage(Arrays.stream(collection.values()).toList().toString().replace("., ", ",\n"));
        if (isScript) return new Response(test.substring(1, test.length() - 1), ResponseType.INFO);

        String message = MessagePainter.ColoredInfoMessage(Arrays.stream(collection.values()).toList().subList(index, min(index + 20, collection.size())).toString().replace("., ", ",\n"));
        return new Response(message.substring(1, message.length() - 1), ResponseType.INFO);
    }
}