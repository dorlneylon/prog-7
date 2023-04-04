package itmo.lab7.commands.implemented;

import itmo.lab7.commands.Action;
import itmo.lab7.server.response.MessagePainter;
import itmo.lab7.server.response.Response;
import itmo.lab7.server.response.ResponseType;

import java.util.Arrays;

import static itmo.lab7.server.UdpServer.collection;
import static java.lang.Math.min;

/**
 * PrintAscendingCommand class implements Action interface and is used to print movies in the collection in ascending order.
 */
public final class PrintAscendingCommand implements Action {
    private final int index;

    /**
     * Constructor for the {@link PrintAscendingCommand} class.
     *
     * @param index The index of the command.
     */
    public PrintAscendingCommand(Integer index) {
        this.index = index;
    }

    /**
     * Constructor for the {@link PrintAscendingCommand} class.
     * Sets the index to 0.
     */
    public PrintAscendingCommand() {
        this(0);
    }

    /**
     * Runs the command and returns the response.
     *
     * @return the response of the command
     */
    @Override
    // Override the run method from the Command interface
    public Response run() {
        // If the collection size is 0, return a success response
        if (collection.size() == 0)
            return new Response("Collection is empty", ResponseType.SUCCESS);

        // Create a message using the MessagePainter class and the collection's sorted movies
        String message = MessagePainter.ColoredInfoMessage(Arrays.stream(collection.getSortedMovies(false)).toList()
                // Get a sublist of the collection from the index to the min of the index + 20 or the collection size
                .subList(index, min(index + 20, collection.size())).toString()
                // Replace the period and comma with a comma and a new line
                .replace("., ", ",\n"));
        // Return an info response with the message, minus the first and last characters
        return new Response(message.substring(1, message.length() - 1), ResponseType.INFO);
    }
}
