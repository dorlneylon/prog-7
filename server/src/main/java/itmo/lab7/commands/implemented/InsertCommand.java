package itmo.lab7.commands.implemented;

import itmo.lab7.basic.baseclasses.Movie;
import itmo.lab7.commands.Action;
import itmo.lab7.server.response.Response;
import itmo.lab7.server.response.ResponseType;

import static itmo.lab7.server.UdpServer.collection;

/**
 * InsertCommand class implements Action interface and is used to insert a movie into the collection.
 */
public final class InsertCommand implements Action {
    private final Movie movie;

    /**
     * Constructor for InsertCommand
     *
     * @param movie The movie to be inserted
     */
    public InsertCommand(Movie movie) {
        this.movie = movie;
    }

    /**
     * Runs the insert operation on the collection.
     *
     * @return A {@link Response} object with a message and a {@link ResponseType} indicating the success of the operation.
     */
    @Override
    public Response run() {
        if (collection.insert(movie)) return new Response("Insert was completed successfully", ResponseType.SUCCESS);
        return new Response("Insertion failed due to existence", ResponseType.ERROR);
    }
}