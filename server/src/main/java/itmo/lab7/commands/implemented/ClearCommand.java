package itmo.lab7.commands.implemented;

import itmo.lab7.commands.Action;
import itmo.lab7.server.UdpServer;
import itmo.lab7.server.response.Response;
import itmo.lab7.server.response.ResponseType;

public final class ClearCommand implements Action {
    @Override
    public Response run() {
        UdpServer.collection.clear();
        return new Response("Collection cleaned successfully", ResponseType.SUCCESS);
    }
}
