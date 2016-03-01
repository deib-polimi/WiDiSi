package applications.magnet.Routing;



public class MessageBytes implements Message{

    /** empty byte array. */
    public static final byte[] EMPTY = new byte[0];

    /**
     * Message (byte-array) payload.
     */
    protected byte[]           data;

    /**
     * Data offset within byte-array.
     */
    protected int              offset;

    /**
     * Data length within byte-array.
     */
    protected int              length;

    /**
     * Create new message object.
     * 
     * @param data
     *            message payload
     * @param offset
     *            offset index within data array
     * @param length
     *            length of data within array
     */
    public MessageBytes(byte[] data, int offset, int length) {
        this.data = data == null ? EMPTY : data;
        this.offset = offset;
        this.length = length;
    }

    /**
     * Create new message object.
     * 
     * @param data
     *            message payload
     */
    public MessageBytes(byte[] data) {
        this(data, 0, data == null ? 0 : data.length);
    }

    /**
     * Create a new message object.
     * 
     * @param data
     *            message pyaload
     */
    public MessageBytes(String data) {
        this(data.getBytes());
    }

    /**
     * Return message payload.
     * 
     * @return message payload
     */
    public byte[] getBytes() {
        return data;
    }

    /**
     * Return offset of data within array.
     * 
     * @return data offset
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Return length of data within array.
     * 
     * @return data length
     */
    public int getLength() {
        return length;
    }

    /** {@inheritDoc} */
    public String toString() {
        return "" + getLength() + "-bytes";
    }

    // ////////////////////////////////////////////////
    // Message interface
    //

    /** {@inheritDoc} */
    public int getSize() {
        return this.length;
    }

    /** {@inheritDoc} */
    public void getBytes(byte[] msg, int offset) {
        System.arraycopy(data, this.offset, msg, offset, this.length);
    }
}
