package itmo.lab7.commands.implemented;

import itmo.lab7.commands.Action;
import itmo.lab7.server.UdpServer;
import itmo.lab7.server.response.Response;
import itmo.lab7.server.response.ResponseType;

/**
 * ClearCommand class implements Action interface and is used to clear the collection.
 */
public final class ClearCommand implements Action {
    /**
     * Clears the collection.
     *
     * @return A {@link Response} object with a message indicating the success of the operation.
     */
    @Override
    public Response run() {
        UdpServer.collection.clear();
        return new Response("Collection cleaned successfully", ResponseType.SUCCESS);
    }
}
