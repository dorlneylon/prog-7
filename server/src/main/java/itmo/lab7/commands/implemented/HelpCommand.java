package itmo.lab7.commands.implemented;

import itmo.lab7.commands.Action;
import itmo.lab7.commands.CommandType;
import itmo.lab7.server.response.MessagePainter;
import itmo.lab7.server.response.Response;
import itmo.lab7.server.response.ResponseType;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * HelpCommand class implements the Action interface and is used to print out the description of all the commands
 * available in the game.
 */
public final class HelpCommand implements Action {
    /**
     * Runs the command.
     *
     * @return The response of the command.
     */
    @Override
    public Response run() {
        return new Response(MessagePainter.ColoredInfoMessage(Arrays.stream(CommandType.values()).
                map(CommandType::getDescription).
                filter(description -> !description.isEmpty()).
                collect(Collectors.joining("\n"))), ResponseType.INFO);
    }
}
