package itmo.lab7.server.response;

import java.util.Arrays;

/**
 * This class is used to represent server response.
 */
public class Response {
    private final String responseMessage;
    private final ResponseType responseType;
    private final Object[] objects;

    /**
     * Constructor for Response object
     *
     * @param responseMessage the message to be sent in the response
     * @param responseType    the type of response to be sent
     */
    public Response(String responseMessage, ResponseType responseType) {
        this.responseMessage = responseMessage;
        this.responseType = responseType;
        this.objects = null;
    }

    /**
     * Constructor for Response class.
     *
     * @param objects      An array of objects to be included in the response.
     * @param responseType The type of response.
     */
    public Response(Object[] objects, ResponseType responseType) {
        this.responseMessage = (objects == null) ? "" : Arrays.toString(objects);
        this.responseType = responseType;
        this.objects = objects;
    }

    /**
     * Returns the response message with the appropriate color based on the response type.
     *
     * @return the response message with the appropriate color
     */
    public String getMessage() {
        if (getType() == ResponseType.SUCCESS) {
            return Color.PURPLE + responseMessage + Color.RESET;
        }
        if (getType() == ResponseType.ERROR) {
            return Color.RED + responseMessage + Color.RESET;
        }
        return responseMessage;
    }

    /**
     * Gets the response type of the response
     *
     * @return the response type
     */
    public ResponseType getType() {
        return responseType;
    }
}
