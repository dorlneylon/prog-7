package itmo.lab7.commands.implemented;

import itmo.lab7.basic.baseclasses.Movie;
import itmo.lab7.commands.Action;
import itmo.lab7.server.response.MessagePainter;
import itmo.lab7.server.response.Response;
import itmo.lab7.server.response.ResponseType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static itmo.lab7.server.UdpServer.collection;
import static java.lang.Math.min;

public class ShowCommand implements Action {

    private final int ind;
    private boolean isScript = false;

    public ShowCommand(Integer ind) {
        this.ind = ind;
    }

    public ShowCommand() {
        this.ind = 0;
    }

    public ShowCommand(Boolean isScript) {
        this.ind = 0;
        this.isScript = isScript;
    }

    @Override
    public Response run() {
        if (collection.size() == 0)
            return new Response("Collection is empty", ResponseType.SUCCESS);

        String test = MessagePainter.ColoredInfoMessage(Arrays.stream(collection.values()).toList().toString().replace("., ", ",\n"));
        if (isScript) return new Response(test.substring(1, test.length()-1), ResponseType.INFO);

        String message = MessagePainter.ColoredInfoMessage(Arrays.stream(collection.values()).toList()
                .subList(ind, min(ind+20, collection.size())).toString()
                .replace("., ", ",\n"));
        return new Response(message.substring(1, message.length()-1), ResponseType.INFO);
    }
}