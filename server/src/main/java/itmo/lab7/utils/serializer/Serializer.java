package itmo.lab7.utils.serializer;

import itmo.lab7.commands.Command;
import itmo.lab7.server.ServerLogger;

import java.io.*;

/**
 * Serializer is used to serialize a command
 * {@link Command}
 */
public final class Serializer {
    /**
     * Serializes object and returns the serialized object as a byte array
     *
     * @param object object to serialize
     * @param <T>    Type of object to serialize
     * @return bytes of serialized object
     */
    public static <T extends Serializable> byte[] serialize(T object) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream;
        try {
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        } catch (IOException e) {
            System.err.println("Unable to create ObjectOutputStream: " + e);
            return new byte[0];
        }
        try {
            objectOutputStream.writeObject(object);
        } catch (IOException e) {
            System.err.println("Unable to serialize object: " + object.getClass().getSimpleName() + ": " + e);
            return new byte[0];
        }
        try {
            objectOutputStream.close();
        } catch (IOException e) {
            System.err.println("Unable to close ObjectOutputStream");
        }
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * Deserializes a byte array into an object.
     *
     * @param bytes The byte array to deserialize.
     * @return The deserialized object.
     */
    public static Object deserialize(byte[] bytes) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bis);
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            ServerLogger.getLogger().warning("Unable to deserialize " + e.getMessage());
            return null;
        }
    }
}
