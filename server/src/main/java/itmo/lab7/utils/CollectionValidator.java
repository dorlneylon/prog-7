package itmo.lab7.utils;

import itmo.lab7.commands.Command;
import itmo.lab7.commands.CommandType;

import java.util.Arrays;

public class CollectionValidator {

    public static boolean isValidHistoryInput(Command command) {
        CommandType[] showCommands = {CommandType.SHOW, CommandType.PRINT_ASCENDING, CommandType.PRINT_DESCENDING};
        return !command.getCommandType().equals(CommandType.SERVICE)
                || (Arrays.asList(showCommands).contains(command.getCommandType())
                && (command.getArguments() == null || command.getArguments().length == 0
                || (command.getArguments().length == 1 && Integer.parseInt(command.getArguments()[0].toString()) == 0)));
    }
}
