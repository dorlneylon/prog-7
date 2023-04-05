package itmo.lab7.commands.implemented;

import itmo.lab7.commands.Action;
import itmo.lab7.commands.Command;
import itmo.lab7.server.response.Response;
import itmo.lab7.server.response.ResponseType;

import java.util.ArrayList;

/**
 * ExecuteScriptCommand is a class that implements the Action interface.
 * It is used to execute a list of commands in a queue.
 */
public final class ExecuteScriptCommand implements Action {

    private final ArrayList<Command> commandQueue;

    /**
     * Constructor for ExecuteScriptCommand
     *
     * @param commandQueue The queue of commands to be executed
     */
    public ExecuteScriptCommand(ArrayList<Command> commandQueue) {
        this.commandQueue = commandQueue;
    }

    /**
     * Executes all commands in the command queue.
     *
     * @return A {@link Response} object containing the output of all commands in the command queue,
     * or an error message if the command queue is empty.
     */
    @Override
    public Response run() {
        if (commandQueue.isEmpty()) {
            return new Response("The command queue is empty", ResponseType.ERROR);
        }
        StringBuilder output = new StringBuilder();
        for (Command command : commandQueue) {
            output.append(command.execute().getMessage()).append("\n");
        }
        return new Response(output.toString().trim(), ResponseType.INFO);
    }
}
