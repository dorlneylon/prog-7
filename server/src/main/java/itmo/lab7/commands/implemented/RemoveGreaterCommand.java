package itmo.lab7.commands.implemented;

import itmo.lab7.commands.Action;
import itmo.lab7.server.UdpServer;
import itmo.lab7.server.response.Response;
import itmo.lab7.server.response.ResponseType;

/**
 * This class implements the {@link Action} interface and is used to remove all elements from the collection that are greater than the given key.
 */
public final class RemoveGreaterCommand implements Action {

    private final Long key;

    /**
     * Constructor for RemoveGreaterCommand.
     *
     * @param key - the key to be used for comparison
     */
    public RemoveGreaterCommand(Long key) {
        this.key = key;
    }

    /**
     * Removes elements from the collection that are greater than the given key
     *
     * @return Response object with the result of the operation
     */
    @Override
    public Response run(String username) {
        if (UdpServer.collection.removeGreater(key)) {
            UdpServer.getDatabase().removeGreater(Math.toIntExact(key), username);
            return new Response("Greater elements have been successfully deleted", ResponseType.SUCCESS);
        }
        return new Response("There are no items in the collection larger than a given", ResponseType.ERROR);
    }
}
