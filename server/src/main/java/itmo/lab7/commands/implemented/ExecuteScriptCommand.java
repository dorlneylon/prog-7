package itmo.lab7.commands.implemented;

import itmo.lab7.commands.Action;
import itmo.lab7.commands.Command;
import itmo.lab7.server.response.Response;
import itmo.lab7.server.response.ResponseType;

import java.util.ArrayList;

import static java.lang.Math.min;

public final class ExecuteScriptCommand implements Action {

    private final ArrayList<Command> commandQueue;
    private final int index;

    public ExecuteScriptCommand(ArrayList<Command> commandQueue) {
        this.commandQueue = commandQueue;
        this.index = 0;
    }

    @Override
    public Response run() {
        if (commandQueue.isEmpty()) {
            return new Response("The command queue is empty", ResponseType.ERROR);
        }
        StringBuilder output = new StringBuilder();
        commandQueue.forEach(command -> output.append(command.execute().getMessage()).append("\n"));
        return new Response(output.toString().trim(), ResponseType.SUCCESS);
    }
}
