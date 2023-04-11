package itmo.lab7.commands.implemented;

import itmo.lab7.commands.Action;
import itmo.lab7.server.UdpServer;
import itmo.lab7.server.response.Response;
import itmo.lab7.server.response.ResponseType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static itmo.lab7.server.UdpServer.collection;
import static itmo.lab7.server.UdpServer.getDatabase;

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
    public Response run(String username) {
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
            case "sign_up" -> {
                assert arg != null;
                Matcher matcher = Pattern.compile("(.*):(.*)$").matcher(arg);
                if (!matcher.find()) {
                    yield new Response("Wrong format of the command", ResponseType.INFO);
                }

                yield getDatabase().addNewUser(matcher.group(1), matcher.group(2)) ?
                        new Response("OK", ResponseType.INFO) :
                        new Response("Something happened during signing. Try again", ResponseType.INFO);
            }
            case "sign_in" -> {
                assert arg != null;
                Matcher matcher = Pattern.compile("(.*):(.*)$").matcher(arg);
                if (!matcher.find()) {
                    yield new Response("Wrong format of the command", ResponseType.INFO);
                }

                yield getDatabase().userSignIn(matcher.group(1), matcher.group(2)) ?
                        new Response("OK", ResponseType.INFO) :
                        new Response("Something happened during signing. Try again", ResponseType.INFO);
            }
            case "is_user_creator" ->
                    new Response(Boolean.toString(UdpServer.getDatabase().isUserEditor(splitCommand[2], Integer.parseInt(arg))), ResponseType.INFO);
            default -> new Response("", ResponseType.INFO);
        };
    }
}
