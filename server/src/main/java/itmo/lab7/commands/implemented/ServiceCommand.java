package itmo.lab7.commands.implemented;

import itmo.lab7.commands.Action;
import itmo.lab7.server.response.Response;
import itmo.lab7.server.response.ResponseType;

import static itmo.lab7.server.UdpServer.collection;

public final class ServiceCommand implements Action {
    private final String command;

    public ServiceCommand(String command) {
        this.command = command;
    }

    @Override
    public Response run() {
        String[] splitCommand = command.split(" ");
        String commandPart = splitCommand[0];
        String arg;
        try {
            arg = splitCommand[1];
        } catch (Exception e) {
            arg = null;
        }
        return switch (commandPart) {
            case "check_id" -> {
                Long id = Long.parseLong(arg);
                boolean isPresented = collection.isKeyPresented(id);
                yield new Response(Boolean.toString(isPresented), ResponseType.INFO);
            }
            case "get_collection_size" -> new Response(Integer.toString(collection.values().length), ResponseType.INFO);
            default -> new Response("", ResponseType.INFO);
        };
    }
}
