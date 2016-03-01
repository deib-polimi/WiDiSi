package applications.magnet.Routing;

public class AodvStats {
	/** sent packets. */
	public final AodvPacketStats send = new AodvPacketStats();
	/** received packets. */
	public final AodvPacketStats recv = new AodvPacketStats();
	/** messages sent by transport layer. */
	public long                  netMsgs;
	/** number of total route requests (excluding retransmissions). */
	public long                  rreqOrig;
	/** number of route replies generated. */
	public long                  rrepOrig;
	/** number of new routes formed. */
	public long                  rreqSucc;

	public AodvStats(){
		clear();
	}

	/** Reset statistics. */
	public void clear() {
		send.clear();
		recv.clear();
		netMsgs = 0;
		rreqOrig = 0;
		rrepOrig = 0;
		rreqSucc = 0;
	}
}
