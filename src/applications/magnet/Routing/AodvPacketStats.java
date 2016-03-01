package applications.magnet.Routing;

public class AodvPacketStats {
	/** Sum of RREQ, RREP, RERR, and HELLO packets. */
	public long aodvPackets;
	/** HELLO packets. */
	public long helloPackets;
	/** RREQ packets. */
	public long rreqPackets;
	/** RREP packets. */
	public long rrepPackets;
	/** RERR packets. */
	public long rerrPackets;
	/** Route available Packets packets. */
	public long ravaPackets;

	public AodvPacketStats(){
		clear();
	}
	/** Reset statistics. */
	public void clear() {
		aodvPackets = 0;
		helloPackets = 0;
		rreqPackets = 0;
		rrepPackets = 0;
		rerrPackets = 0;
		ravaPackets = 0;
	}
}
