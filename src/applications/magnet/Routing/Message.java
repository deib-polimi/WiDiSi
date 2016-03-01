package applications.magnet.Routing;


public interface Message {
	  /** A null/empty message. */
    Message NULL = new MessageBytes(MessageBytes.EMPTY);

    /**
     * Return packet size or Constants.ZERO_WIRE_SIZE.
     * 
     * @return packet size
     */
    int getSize();

    /**
     * Store packet into byte array.
     * 
     * @param msg
     *            destination byte array
     * @param offset
     *            byte array starting offset
     */
    void getBytes(byte[] msg, int offset);
}
