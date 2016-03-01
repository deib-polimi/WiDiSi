package applications.magnet.Routing;


public interface NetInterface {

    /**
     * Return local network address.
     * 
     * @return local network address
     * @throws JistAPI.Continuation
     *             never (blocking event)
     */
    NetAddress getAddress();

    /**
     * Receive a message from the link layer.
     * 
     * @param msg
     *            incoming network packet
     * @param lastHop
     *            link-level source of incoming packet
     * @param macId
     *            incoming interface
     * @param promiscuous
     *            whether packet was received in promiscuos mode
     * @param bcast
     *            whether packet was received in brodcast mode
     */
    void receive(Message msg, MacAddress lastHop, byte macId, boolean promiscuos, boolean bcast);

    /**
     * Receive a notification from the link layer.
     * 
     * @param packet
     *            dropped network packet
     * @param packetNextHop
     *            link-level destination of droped packet
     */
    void dropNotify(Message packet, MacAddress packetNextHop);

    /**
     * Route, if necessary, and send a message (from TRANSPORT).
     * 
     * @param msg
     *            packet payload (usually from transport or routing layers)
     * @param dst
     *            packet destination address
     * @param protocol
     *            packet protocol identifier
     * @param priority
     *            packet priority
     * @param ttl
     *            packet time-to-live value
     */
    void send(Message msg, NetAddress dst, short protocol, byte priority, byte ttl);

    /**
     * Send a message along given interface (usually from ROUTING).
     * 
     * @param msg
     *            packet (usually from routing layer)
     * @param interfaceId
     *            interface along which to send packet
     * @param nextHop
     *            packet next hop address
     */
    void send(NetMessage.Ip msg, int interfaceId, MacAddress nextHop);

    /**
     * Request next packet to send, if one exists; indicate that interface has
     * completed processing previous request.
     * 
     * @param netid
     *            interface identifier
     */
    void pump(int netid);

    /**
     * Network layer callback interface.
     * 
     * @author Rimon Barr &lt;barr+jist@cs.cornell.edu&gt;
     * @version $Id: NetInterface.java,v 1.18 2004-04-06 16:07:49 barr Exp $
     * @since SWANS1.0
     */
    public static interface NetHandler
    {
        /**
         * Receive a message from network layer.
         * 
         * @param msg
         *            message received
         * @param src
         *            source network address
         * @param lastHop
         *            source link address
         * @param macId
         *            incoming interface
         * @param dst
         *            destination network address
         * @param priority
         *            packet priority
         * @param ttl
         *            packet time-to-live
         */
        void receive(Message msg, NetAddress src, MacAddress lastHop, byte macId, NetAddress dst, byte priority,
                byte ttl);

    } // interface: NetHandler
}
