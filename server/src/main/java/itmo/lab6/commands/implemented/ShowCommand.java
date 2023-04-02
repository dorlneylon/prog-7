package itmo.lab6.commands.implemented;

import itmo.lab6.basic.baseclasses.Movie;
import itmo.lab6.commands.Action;
import itmo.lab6.server.response.MessagePainter;
import itmo.lab6.server.response.Response;
import itmo.lab6.server.response.ResponseType;

import java.util.Arrays;
import java.util.stream.Collectors;

import static itmo.lab6.server.UdpServer.collection;

public class ShowCommand implements Action {

    @Override
    public Response run() {
        String responseText = Arrays.stream(collection.values()).map(Movie::toString).collect(Collectors.joining("\n"));
        if (responseText.isEmpty()) {
            return new Response("Collection is empty", ResponseType.SUCCESS);
        }
        return new Response(MessagePainter.ColoredInfoMessage(responseText), ResponseType.INFO);
    }
}