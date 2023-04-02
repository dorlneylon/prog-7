package itmo.lab7.commands;

import itmo.lab7.server.response.Response;

public interface Action {
    Response run();
}
