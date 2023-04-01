package itmo.lab6.commands.implemented;

import itmo.lab6.basic.baseclasses.Movie;
import itmo.lab6.commands.Action;
import itmo.lab6.server.response.Response;
import itmo.lab6.server.response.ResponseType;

import static itmo.lab6.server.UdpServer.collection;

public final class InsertCommand implements Action {
    private final Movie movie;

    public InsertCommand(Movie movie) {
        this.movie = movie;
    }

    @Override
    public Response run() {
        if (collection.insert(movie)) return new Response("Insert was completed successfully", ResponseType.SUCCESS);
        return new Response("Insertion failed due to existence", ResponseType.INFO);
    }
}