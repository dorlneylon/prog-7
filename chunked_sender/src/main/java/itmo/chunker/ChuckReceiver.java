package itmo.chunker;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * ChuckReceiver class is used to store chunks of data in a sorted order.
 *
 * @author kxrxh
 */
public class ChuckReceiver {
    private static final int LAST_FOUR_BYTES_OFFSET = 4;
    private final ByteBuffer lastFourBytesBuffer = ByteBuffer.allocateDirect(4);
    private int numberOfChunks = 0;
    private final Map<Short, byte[]> chunkMap = new HashMap<>();
    private boolean isReceived = false;

    public ChuckReceiver() {
    }

    /**
     * Adds a chunk to the chunk map.
     *
     * @param chunk The chunk to be added.
     */
    public void add(byte[] chunk) {
        byte[] lastFourBytes = Arrays.copyOfRange(chunk, chunk.length - LAST_FOUR_BYTES_OFFSET, chunk.length);
        // Clear the buffer before each use
        lastFourBytesBuffer.clear();
        // Put the last four bytes into the direct buffer
        lastFourBytesBuffer.put(lastFourBytes);
        // Set the position to zero before reading from it
        lastFourBytesBuffer.position(0);
        // Get the first two bytes as short index
        short index = lastFourBytesBuffer.getShort();
        // Get the last two bytes as short size and assign it to numberOfChunks if it's zero
        numberOfChunks = (numberOfChunks == 0) ? lastFourBytesBuffer.getShort(2) : numberOfChunks;
        chunkMap.put(index, Arrays.copyOfRange(chunk, 0, chunk.length - LAST_FOUR_BYTES_OFFSET));
        if (numberOfChunks == chunkMap.size()) {
            isReceived = true;
        }
    }

    /**
     * Returns true if all chunks were received
     *
     * @return The value of the isReceived field.
     */
    public boolean isReceived() {
        return isReceived;
    }


    /**
     * Returns a byte array containing all chunks in the chunk map is sorted order.
     *
     * @return a byte array containing all chunks in the chunk map
     */
    public byte[] getAllChunks() {
        TreeMap<Short, byte[]> sorted = new TreeMap<>(chunkMap);
        int totalSize = sorted.values().stream().mapToInt(b -> b.length).sum();
        ByteBuffer buffer = ByteBuffer.allocate(totalSize);
        sorted.values().forEach(buffer::put);
        return buffer.array();
    }
}
