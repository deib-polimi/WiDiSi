package applications.magnet.Routing;

import java.net.InetAddress;

public class NetAddress {
	
	  // ////////////////////////////////////////////////
    // constants
    //

    /**
     * Loopback network address.
     */
    public static final NetAddress   LOCAL       = new NetAddress(new byte[] { 127, 0, 0, 1 });

    /**
     * Null/invalid network address.
     */
    public static final NetAddress   NULL        = new NetAddress(new byte[] { -1, -1, -1, -2 });

    /**
     * Broadcast network address.
     */
    public static final NetAddress   ANY         = new NetAddress(new byte[] { -1, -1, -1, -1 });

    /**
     * Zero-length array of NetAddress type.
     */
    public static final NetAddress[] EMPTY_ARRAY = new NetAddress[0];

    // ////////////////////////////////////////////////
    // address
    //

    /**
     * address data.
     */
    private InetAddress              ip;

    /**
     * Create a new network address object.
     * 
     * @param ip
     *            address data
     */
    public NetAddress(InetAddress ip) {
        this.ip = ip;
    }

    /**
     * Create a new network address object.
     * 
     * @param addr
     *            address data
     */
    public NetAddress(byte[] addr) {
        this.ip = Pickle.arrayToInetAddress(addr, 0);
    }

    /**
     * Create a new network address object.
     * 
     * @param i
     *            address data
     */
    public NetAddress(int i) {
        this(intToByteArray(i));
    }

    /**
     * Compute hash code for network address.
     * 
     * @return hash code of address
     */
    public int hashCode() {
        return ip.hashCode();
    }

    /**
     * Return whether this network address is equal to another object.
     * 
     * @param o
     *            object to test equality against
     * @return whether object provided is equal to this network address
     */
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (!(o instanceof NetAddress))
            return false;
        NetAddress na = (NetAddress) o;
        if (!this.ip.equals(na.ip))
            return false;
        return true;
    }

    /**
     * Return IP address information.
     * 
     * @return IP address information
     */
    public InetAddress getIP() {
        return ip;
    }

    /**
     * Convert an integer into a byte array.
     * 
     * @param i
     *            input integer to convert
     * @return corresponding byte array
     */
    private static byte[] intToByteArray(int i) {
        byte[] b = new byte[4];
        b[3] = (byte) (i & 0xff);
        b[2] = (byte) ((i >> 8) & 0xff);
        b[1] = (byte) ((i >> 16) & 0xff);
        b[0] = (byte) ((i >> 24) & 0xff);
        return b;
    }

    /** {@inheritDoc} */
    public String toString() {
        if (equals(ANY)) {
            return "ANY";
        } else if (equals(LOCAL)) {
            return "LOCAL";
        } else if (equals(NULL)) {
            return "NULL";
        } else {
            return ip.toString().substring(1);
        }
    }

    /**
     * Return size in bytes (round up if necessary)
     * 
     * @return size in bytes
     */
    public int getSize() {
        return 4;
    }
}
