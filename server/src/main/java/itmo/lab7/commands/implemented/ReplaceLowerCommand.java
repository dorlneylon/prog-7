package itmo.lab7.commands.implemented;

import itmo.lab7.basic.baseclasses.Movie;
import itmo.lab7.commands.Action;
import itmo.lab7.server.response.Response;
import itmo.lab7.server.response.ResponseType;

import static itmo.lab7.server.UdpServer.collection;

public final class ReplaceLowerCommand implements Action {
    private final Movie movie;

    public ReplaceLowerCommand(Movie movie) {
        this.movie = movie;
    }

    @Override
    public Response run() {
        if (collection.replaceLower(movie.getId(), movie)) return new Response("Element has been successfully replaced", ResponseType.SUCCESS);
        return new Response("Element either doesn't exist or has less oscars.", ResponseType.SUCCESS);
    }
}
