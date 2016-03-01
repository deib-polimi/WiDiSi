package applications.magnet.Routing;

import peersim.core.Node;

public class eventMessage {
	/** The src pid. */
	public int srcPid;                	// the Protocol ID that sends this message
	
	/** The dest pid. */
	public int destPid;                	// the Protocol ID that receives this message
	
	/** The event. */
	public String event;				// the event we want to transfer
	
	/** The src node. */
	public Node srcNode;				// the node that sends this message
	
	/** The dest node. */
	public Node destNode;				// the node that should receive this message
	
	/** The object. */
	public Object object;				// an arbitrary object we want to send (optional)
	
    /**
     * additional fieled that I addedto this class -- this is the last hop mac address
     * Naser
     */
    public MacAddress lastHopMacAddr;
    
	/**
	 * Instantiates a new message.
	 */
	public eventMessage() {
		// initialized with a non valid protocol ID
		srcPid = 10000;
		destPid = 10000;
		event = "";
		srcNode = null;
		destNode = null;
		//newNode = null;
		object = null;
		lastHopMacAddr = null;
	}
	

}
