/*
 * 
 */
package wifidirect.p2pcore;

import peersim.core.Node;

// TODO: Auto-generated Javadoc
/**
 * The Class Message.
 */
public class Message {

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
	 * Instantiates a new message.
	 */
	public Message() {
		// initialized with a non valid protocol ID
		srcPid = 10000;
		destPid = 10000;
		event = "";
		srcNode = null;
		destNode = null;
		//newNode = null;
		object = null;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Message [srcPid=" + srcPid + ", destPid=" + destPid
				+ ", event=" + event + ", srcNode=" + srcNode.getID() + ", destNode="
				+ destNode.getID() + ", object=" + object
				+ "]";
	}

}
