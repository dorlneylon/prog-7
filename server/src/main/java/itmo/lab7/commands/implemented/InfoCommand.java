package itmo.lab7.commands.implemented;

import itmo.lab7.commands.Action;
import itmo.lab7.server.UdpServer;
import itmo.lab7.server.response.MessagePainter;
import itmo.lab7.server.response.Response;
import itmo.lab7.server.response.ResponseType;

public final class InfoCommand implements Action {

    @Override
    public Response run() {
        return new Response(MessagePainter.ColoredInfoMessage(UdpServer.collection.info()), ResponseType.INFO);
    }
}
