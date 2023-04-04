package itmo.lab7.commands;

import itmo.lab7.server.response.Response;

/**
 * Action interface
 * <p>
 * This interface is used to define the run() method which is used to execute an action.
 *
 * @author kxrxh
 */
public interface Action {

    Response run();
}
