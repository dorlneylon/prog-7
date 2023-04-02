package itmo.lab7.commands.implemented;

import itmo.lab7.commands.Action;
import itmo.lab7.server.response.Response;
import itmo.lab7.server.response.ResponseType;

public final class DefaultCommand implements Action {

    @Override
    public Response run() {
        return new Response("Unknown command. To view command list use command 'help'", ResponseType.INFO);
    }
}
