/*
 * Copyright (c) 2014-2015 SCUBE Joint Open Lab
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 2 as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 * 
 * Author: Naser Derakhshan
 * Politecnico di Milano
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
