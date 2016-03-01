package applications.magnet.Routing;

public class MacAddress implements Comparable{
	 // ////////////////////////////////////////////////
    // constants
    //

    /**
     * Broadcast Mac address.
     */
    public static final MacAddress ANY  = new MacAddress(-1);

    /**
     * Loopback Mac address.
     */
    public static final MacAddress LOOP = new MacAddress(-2);

    /**
     * Null/invalid Mac address.
     */
    public static final MacAddress NULL = new MacAddress(0);

    // ////////////////////////////////////////////////
    // internals
    //

    /**
     * address data.
     */
    private int                    addr;

    /**
     * Create a new Mac address object.
     * 
     * @param addr
     *            address data
     */
    public MacAddress(int addr) {
        this.addr = addr;
    }

    /**
     * Compute hash code for mac address.
     * 
     * @return hash code of address
     */
    public int hashCode() {
        return addr;
    }

    /**
     * Return whether this mac address is equal to another object.
     * 
     * @param o
     *            object to test equality against
     * @return whether object provided is equal to this mac address
     */
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (!(o instanceof MacAddress))
            return false;
        MacAddress ma = (MacAddress) o;
        if (this.addr != ma.addr)
            return false;
        return true;
    }

    /** {@inheritDoc} */
    public String toString() {
        if (equals(ANY)) {
            return "ANY";
        } else if (equals(LOOP)) {
            return "LOOP";
        } else {
            return "" + addr;
        }
    }

    /** {@inheritDoc} */
    public int compareTo(Object o) {
        if (!(o instanceof MacAddress))
            throw new ClassCastException();
        MacAddress mac = (MacAddress) o;
        return addr < mac.addr ? -1 : addr == mac.addr ? 0 : 1;
    }

}
