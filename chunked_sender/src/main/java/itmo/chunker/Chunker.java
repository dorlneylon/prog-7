package itmo.chunker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Chunker class used to split a byte array into chunks of a specified size.
 *
 * @author John Doe
 */
public class Chunker {
    private final byte[] data;
    private final List<byte[]> chunked = new ArrayList<>();
    private final int chunkSize;

    /**
     * Constructs a new Chunker object with the given data and a default chunk size of 1024.
     *
     * @param data The data to be chunked.
     */
    public Chunker(byte[] data) {
        this(data, 1024);
    }

    /**
     * Constructor for Chunker class.
     *
     * @param data      The data to be chunked.
     * @param chunkSize The size of each chunk.
     */
    public Chunker(byte[] data, int chunkSize) {
        this.data = data;
        this.chunkSize = chunkSize;
        createChunkedData();
    }

    /**
     * Creates a list of byte arrays, each containing a chunk of the dataBytes.
     */
    private void createChunkedData() {
        // Calculate the total number of chunks needed to send the dataBytes
        short totalChunks = (short) Math.ceil((double) data.length / chunkSize);
        // Create a byte array to store the total number of chunks
        byte[] totalChunksBytes = new byte[2];
        totalChunksBytes[1] = (byte) (totalChunks & 0xff);
        totalChunksBytes[0] = (byte) ((totalChunks >> 8) & 0xff);
        // Iterate through each chunk
        for (short chunkIndex = 0; chunkIndex < totalChunks; chunkIndex++) {
            // Create a byte array to store the current chunk index
            byte[] currentChunkIndexBytes = new byte[2];
            currentChunkIndexBytes[1] = (byte) ((chunkIndex + 1) & 0xff);
            currentChunkIndexBytes[0] = (byte) (((chunkIndex + 1) >> 8) & 0xff);
            // Calculate the offset of the current chunk
            int chunkOffset = chunkIndex * chunkSize;
            // Calculate the length of the current chunk
            int chunkLength = Math.min(data.length - chunkOffset, chunkSize);
            // Create a byte array to store the current chunk
            byte[] chunkBytes = new byte[chunkLength + 4]; // 4 dataBytes for short index and short chunkLength
            // Store the current chunk index in the chunkBytes array
            chunkBytes[chunkLength] = currentChunkIndexBytes[0];
            chunkBytes[chunkLength + 1] = currentChunkIndexBytes[1];
            // Store the total number of chunks in the chunkBytes array
            chunkBytes[chunkLength + 2] = totalChunksBytes[0];
            chunkBytes[chunkLength + 3] = totalChunksBytes[1];
            // Copy the dataBytes into the chunkBytes array
            System.arraycopy(data, chunkOffset, chunkBytes, 0, chunkLength);
            chunked.add(chunkBytes);
        }
    }

    /**
     * Returns an {@link Iterator} over the chunks;
     *
     * @return an {@link Iterator} over the chunks
     */
    public Iterator<byte[]> newIterator() {
        return chunked.iterator();
    }
}
