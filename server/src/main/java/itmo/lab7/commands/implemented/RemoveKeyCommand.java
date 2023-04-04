package itmo.lab7.commands.implemented;

import itmo.lab7.basic.baseclasses.Movie;
import itmo.lab7.commands.Action;
import itmo.lab7.server.UdpServer;
import itmo.lab7.server.response.Response;
import itmo.lab7.server.response.ResponseType;

/**
 * This class implements the {@link Action} interface and is used to remove a {@link Movie} from the {@link UdpServer#collection}.
 *
 * @author Yurii Shyrma
 */
public final class RemoveKeyCommand implements Action {
    private final Long key;

    /**
     * Constructor for the RemoveKeyCommand class.
     *
     * @param key The key to be removed.
     */
    public RemoveKeyCommand(Long key) {
        this.key = key;
    }

    /**
     * This method is used to delete a movie from the collection by its key.
     *
     * @return a {@link Response} object with a message and a {@link ResponseType}
     */
    @Override
    public Response run() {
        if (UdpServer.collection.removeByKey(key)) {
            return new Response("Movie with key %d deleted successfully".formatted(key), ResponseType.SUCCESS);
        }
        return new Response("It is not possible to delete a Movie with key=%d, because there is no Movie with this key.".formatted(key), ResponseType.ERROR);
    }
}
