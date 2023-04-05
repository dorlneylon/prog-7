package itmo.lab7.commands.implemented;

import itmo.lab7.basic.baseclasses.Movie;
import itmo.lab7.commands.Action;
import itmo.lab7.server.response.Response;
import itmo.lab7.server.response.ResponseType;

import static itmo.lab7.server.UdpServer.collection;

/**
 * This class implements the {@link Action} interface and is used to replace an element in the collection with a lower number of Oscars.
 */
public final class ReplaceLowerCommand implements Action {
    private final Movie movie;

    /**
     * Constructor for ReplaceLowerCommand
     *
     * @param movie The movie to be modified
     */
    public ReplaceLowerCommand(Movie movie) {
        this.movie = movie;
    }

    /**
     * Replaces the movie in the collection with the same ID as the given movie, if the given movie has more oscars.
     *
     * @return a {@link Response} object with a message and a {@link ResponseType} indicating the success of the operation
     */
    @Override
    public Response run(String username) {
        if (collection.replaceLower(movie.getId(), movie))
            return new Response("Element has been successfully replaced", ResponseType.SUCCESS);
        return new Response("Element either doesn't exist or has less oscars.", ResponseType.SUCCESS);
    }
}
