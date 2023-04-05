package itmo.lab7.commands.implemented;

import itmo.lab7.commands.Action;
import itmo.lab7.server.UdpServer;
import itmo.lab7.server.response.MessagePainter;
import itmo.lab7.server.response.Response;
import itmo.lab7.server.response.ResponseType;

/**
 * InfoCommand class implements Action interface and is used to return information about the collection.
 */
public final class InfoCommand implements Action {

    /**
     * @return a {@link Response} containing the information about the collection
     */
    @Override
    public Response run(String username) {
        return new Response(MessagePainter.ColoredInfoMessage(UdpServer.collection.info()), ResponseType.INFO);
    }
}
