package applications.magnet.Routing;

public class RouteAvailableMessage implements Message{

	
	/** RREQ message size in bytes. */
	private static final int MESSAGE_SIZE = 1024;

	/** Message identification number. */
	private int              rreqId;
	/** Destination node IP address. */
	private NetAddress       destIp;
	/** Originator node IP address. */
	private NetAddress       origIp;
	
	/** Originator node sequence number. */
	private int              origSeqNum;

	public Message chatString;

	/**
	 * Constructs a new RREQ Message object.
	 * 
	 * @param rreqId
	 *            RREQ message identification number
	 * @param destIp
	 *            Destination node net address
	 * @param origIp
	 *            Originator node net address
	 * @param destSeqNum
	 *            Destination node sequence number
	 * @param origSeqNum
	 *            Originator node sequence number
	 * @param unknownDestSeqNum
	 *            Flag indicating an unknown destination node sequence
	 *            number
	 * @param hopCount
	 *            hop count
	 */
	public RouteAvailableMessage(int rreqId, NetAddress destIp, NetAddress origIp, int origSeqNum,
			 Message chatString) {
		this.rreqId = rreqId;
		this.destIp = destIp;
		this.origIp = origIp;
		this.origSeqNum = origSeqNum;
		this.chatString = chatString;
	}

	/**
	 * Constructs a copy of an existing RREQ message object.
	 * 
	 * @param rreq
	 *            An existing RREQ message
	 */
	public RouteAvailableMessage(RouteAvailableMessage rreq) {
		this(rreq.getRreqId(), rreq.getDestIp(), rreq.getOrigIp(), rreq.getOrigSeqNum(), rreq.chatString);
	}

	/**
	 * Returns RREQ id.
	 * 
	 * @return RREQ id
	 */
	public int getRreqId() {
		return rreqId;
	}

	/**
	 * Returns destination net address.
	 * 
	 * @return Destination net address
	 */
	public NetAddress getDestIp() {
		return destIp;
	}

	/**
	 * Returns originator net address.
	 * 
	 * @return Originator node net address
	 */
	public NetAddress getOrigIp() {
		return origIp;
	}

	/**
	 * Returns originator sequence number.
	 * 
	 * @return Originator sequence number
	 */
	public int getOrigSeqNum() {
		return origSeqNum;
	}


	/**
	 * Return packet size.
	 * 
	 * @return packet size
	 */
	public int getSize() {
		return MESSAGE_SIZE;
	}

	/**
	 * Store packet into byte array.
	 * 
	 * @param msg
	 *            destination byte array
	 * @param offset
	 *            byte array starting offset
	 */
	public void getBytes(byte[] msg, int offset) {
	}
}
