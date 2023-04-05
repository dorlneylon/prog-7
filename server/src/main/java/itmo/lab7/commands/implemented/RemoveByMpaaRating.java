package itmo.lab7.commands.implemented;

import itmo.lab7.basic.baseenums.MpaaRating;
import itmo.lab7.commands.Action;
import itmo.lab7.server.UdpServer;
import itmo.lab7.server.response.Response;
import itmo.lab7.server.response.ResponseType;

/**
 * This class implements the Action interface and is used to remove elements from the collection by their MPAA rating.
 */
public final class RemoveByMpaaRating implements Action {
    private final MpaaRating mpaaRating;

    /**
     * Constructor for RemoveByMpaaRating
     *
     * @param mpaaRating The MpaaRating to be removed
     */
    public RemoveByMpaaRating(MpaaRating mpaaRating) {
        this.mpaaRating = mpaaRating;
    }

    /**
     * Removes a movie from the collection based on its MPAA rating
     *
     * @return A {@link Response} object with a message and a {@link ResponseType}
     */
    @Override
    public Response run(String username) {
        if (UdpServer.collection.removeByRating(mpaaRating)) {
            UdpServer.getDatabase().removeByMpaaRating(username, mpaaRating);
            return new Response("Successfully deleted", ResponseType.SUCCESS);
        }
        return new Response("No such element(s)", ResponseType.ERROR);
    }
}
