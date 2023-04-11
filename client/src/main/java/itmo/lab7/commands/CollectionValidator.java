package itmo.lab7.commands;

import itmo.lab7.basic.utils.serializer.CommandSerializer;
import itmo.lab7.connection.Connector;

/**
 * This class is used to store collection data validators
 */
public final class CollectionValidator {

    private static Connector connector;

    public static void setConnector(Connector connector) {
        CollectionValidator.connector = connector;
    }

    /**
     * Метод проверяет:
     * 1. Если команда INSERT, то проверяет, не существует ли ключ в коллекции. Если существует, то возвращает false.
     * 2. Если команда UPDATE или REPLACE_IF_LOWER, то проверяет, существует ли ключ в коллекции. Если не существует, то возвращает false.
     */
    public static Boolean checkIfExists(Long key) throws Exception {
        connector.send(CommandSerializer.serialize(new Request(new Command(CommandType.SERVICE, "check_id %d".formatted(key)), null)));
        return Boolean.parseBoolean(connector.receive());
    }

    public static int getCollectionSize() throws Exception {
        connector.send(CommandSerializer.serialize(new Request(new Command(CommandType.SERVICE, "get_collection_size"), null)));
        return Integer.parseInt(connector.receive());
    }

    private static boolean isCommandLegit(String name, CommandType commandType, Long key) throws Exception {
        if (commandType.equals(CommandType.INSERT)) {
            return !checkIfExists(key);
        } else if (commandType.equals(CommandType.UPDATE) || commandType.equals(CommandType.REPLACE_IF_LOWER)) {
            return checkIfExists(key) && isUserCreator(name, key);
        }
        return true;
    }

    public static Boolean isMovieValid(CommandType type, String name, String[] args) {
        if (args.length < 1) {
            System.err.println("Not enough arguments for command " + type.name());
            return null;
        }
        long key;
        try {
            key = Long.parseLong(args[0]);
            if (!isCommandLegit(name, type, key)) {
                System.err.println("Key " + key + " is not compatible with the command " + type.name() + " for you.");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Invalid argument for command " + type.name());
            return false;
        }
        return true;
    }

    public static boolean isUserCreator(String name, long key) throws Exception {
        connector.send(CommandSerializer.serialize(new Request(new Command(CommandType.SERVICE, "is_user_creator %d %s".formatted(key, name)))));
        return Boolean.parseBoolean(connector.receive());
    }
}

