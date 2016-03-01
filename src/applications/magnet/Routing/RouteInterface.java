package applications.magnet.Routing;

public interface RouteInterface extends NetInterface.NetHandler{

	 /**
     * Called by the network layer for every incoming packet. A routing
     * implementation may wish to look at these packets for informational
     * purposes, but should not change their contents.
     * 
     * @param msg
     *            incoming packet
     * @param interfaceId
     *            the interface that received the msg
     * @param lastHop
     *            last link-level hop of incoming packet
     */
    void peek(NetMessage msg, byte interfaceId, MacAddress lastHop);

    /**
     * Called by the network layer to request transmission of a packet that
     * requires routing. It is the responsibility of the routing layer to
     * provide a best-effort transmission of this packet to an appropriate next
     * hop by calling the network layer sending routines once this routing
     * information becomes available.
     * 
     * @param msg
     *            outgoing packet
     */
    void send(NetMessage msg);

    /**
     * Called by the network layer which forwards notifications of dropped
     * packets from the link layer. Not all MAC implementations support this
     * feature!
     * 
     * @param packet
     *            dropped network packet
     * @param packetNextHop
     *            link-level destination of droped packet
     */
    void dropNotify(Message packet, MacAddress packetNextHop);
    
    /**
     * AODV routing entity interface.
     */
    public static interface Aodv extends RouteInterface
    {
        /**
         * AODV Timeout event, which gets called periodically.
         * 
         * Clears expired RREQ buffer entries. Sends hello messages. Updates
         * wait counters, and checks for idle outgoing-nodes
         */
        void timeout();

        /**
         * This event is called periodically after a route request is
         * originated, until a route has been found.
         * 
         * Each time it is called, it rebroadcasts the route request message
         * with a new rreq id and incremented TTL.
         * 
         * @param rreqObj
         *            RouteRequest object
         */
        void RREQtimeout(Object rreqObj);

        /**
         * Sends IP message after transmission delay, and renews precursor list
         * entry.
         * 
         * @param ipMsg
         *            IP message to send
         * @param destMacAddr
         *            next hop mac address
         */
        void sendIpMsg(NetMessage.Ip ipMsg, MacAddress destMacAddr);

    } // class: AODV
    
}
