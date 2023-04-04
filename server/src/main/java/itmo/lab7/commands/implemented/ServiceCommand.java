package itmo.lab7.commands.implemented;

import itmo.lab7.commands.Action;
import itmo.lab7.server.response.Response;
import itmo.lab7.server.response.ResponseType;

import static itmo.lab7.server.UdpServer.collection;

/**
 * This class is used to execute commands from the service.
 */
public final class ServiceCommand implements Action {
    private final String command;

    /**
     * Constructor for ServiceCommand
     *
     * @param command The command to be executed
     */
    public ServiceCommand(String command) {
        this.command = command;
    }

    /**
     * Executes the command given by the user.
     *
     * @return Response object with the result of the command execution
     */
    @Override
    public Response run() {
        String[] splitCommand = command.split(" ");
        String commandPart = splitCommand[0];
        String arg = null;
        if (splitCommand.length > 1) {
            arg = splitCommand[1];
        }
        return switch (commandPart) {
            case "check_id" -> {
                assert arg != null;
                Long id = Long.parseLong(arg);
                boolean isPresented = collection.isKeyPresented(id);
                yield new Response(Boolean.toString(isPresented), ResponseType.INFO);
            }
            case "get_collection_size" -> new Response(Integer.toString(collection.size()), ResponseType.INFO);
            default -> new Response("", ResponseType.INFO);
        };
    }
}
