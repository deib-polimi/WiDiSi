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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Linkable;
import peersim.core.Network;
import peersim.core.Node;
import peersim.transport.Transport;
import wifidirect.nodemovement.CoordinateKeeper;

// TODO: Auto-generated Javadoc
/**
 * The Class nodeInfoObs.
 */
public class nodeInfoObs implements Control {

	/** The Constant PAR_PROT. */
	private static final String PAR_PROT = "protocol";
	
	/** The Constant INF_PROT. */
	private static final String INF_PROT = "infoprotocol";
	
	/** The Constant CORD_PROT. */
	private static final String CORD_PROT = "cordprotocol";
	
	/** The linkable id. */
	private static int linkableId;
	
	/** The nodeinfo pid. */
	private static int nodeinfoPid;
	
	/** The cordprotocol pid. */
	private static int cordprotocolPid;
	
	/**  transport protocol for event-based simulation *. */
	public static final String PAR_TRASP = "transport";

	/**  Transport protocol identifier for event-based simulation *. */
	public int transportId;
	
	/**  eventDetection protocol for event-based simulation *. */
	public static final String PAR_LISTENER = "listeners";

	/**  eventDetection protocol identifier for event-based simulation *. */
	public int listenerPid;
	
	/**
	 * Instantiates a new node info obs.
	 *
	 * @param prefix the prefix
	 */
	public nodeInfoObs (String prefix){
		linkableId = Configuration.getPid(prefix + "." + PAR_PROT);
		nodeinfoPid = Configuration.getPid(prefix + "." + INF_PROT);
		transportId = Configuration.getPid(prefix + "." + PAR_TRASP);
		listenerPid = Configuration.getPid(prefix + "." + PAR_LISTENER);
		cordprotocolPid = Configuration.getPid(prefix + "." + CORD_PROT);
	}
	
	/** The cycle. */
	private int cycle = 0;
	
	/** The writer. */
	private PrintWriter writer = null;
	
	/** The writer2. */
	private PrintWriter writer2 = null;
	
	/* (non-Javadoc)
	 * @see peersim.core.Control#execute()
	 */
	public boolean execute() {
		// TODO Auto-generated method stub
		Node node;
		Linkable neighbors;
		nodeP2pInfo nodeInfo;
		CoordinateKeeper inetCord;
		try{
			writer = new PrintWriter(new BufferedWriter(new FileWriter("log/nodeInfoObs.txt", true)));
		} catch (IOException e) {
		    System.out.println("File nodeInfoObs.txt not found");
		}
		
		try{
			writer2 = new PrintWriter(new BufferedWriter(new FileWriter("log/group.txt", true)));
		} catch (IOException e) {
		    System.out.println("File group.txt not found");
		}
			
		writer.println("Cycle: " + cycle + " Current Time: " + CommonState.getTime());
		writer.println("Network Size: " + Network.size());
		for(int i=0; i<Network.size(); i++){
			node=Network.get(i);
			neighbors = (Linkable) node.getProtocol(linkableId);
			inetCord = (CoordinateKeeper) node.getProtocol(cordprotocolPid);
			//System.out.println("Node: " + node.getID() + " Neighbors:");
			writer.println("Node: " + node.getID() + " Coordination: X: " + inetCord.getX() + " Y: "+ inetCord.getY()  + " Neighbors:" + " Current Time: " + CommonState.getTime());
			for (int n=0; n<neighbors.degree(); n++){
				nodeInfo = (nodeP2pInfo) neighbors.getNeighbor(n).getProtocol(nodeinfoPid);
				///System.out.println(neighbors.getNeighbor(n).getID() + ": " + nodeInfo.toString());
				writer.println(neighbors.getNeighbor(n).getID() + ": " + nodeInfo.toString());
			}
			
			writer.println();
		}
		writer.close();
		
		writer2.println("Cycle: " + cycle);
		writer2.println("Network Size: " + Network.size());
		for(int i=0; i<Network.size(); i++){
			node=Network.get(i);
			neighbors = (Linkable) node.getProtocol(linkableId);
			nodeInfo = (nodeP2pInfo) node.getProtocol(nodeinfoPid);
			writer2.println();
			writer2.println("Node: " + node.getID());
			writer2.print("Neighbors: ");
			for (int n=0; n<neighbors.degree(); n++){				
				writer2.print("Node: " + neighbors.getNeighbor(n).getID() + " , ");
			}
//			writer2.println();
//			writer2.print("Group: ");
//			for (Node newNode: nodeInfo.currentGroup.getNodeList()){				
//				writer2.print("Node " + newNode.getID() + " , ");
//			}
//			writer2.println();
//			if(nodeInfo.currentGroup.getGroupOwner()!= null){
//				writer2.println("Group Owner: Node " + nodeInfo.currentGroup.getGroupOwner().getID());
//			}
			Transport transport = (Transport) node.getProtocol(transportId);
			Message message = new Message();
			message.destNode = node;
			message.destPid = listenerPid;
			message.srcNode = node;
			message.srcNode = null;
			message.event = "GROUP_INFO_REQUEST";
			transport.send(message.srcNode, message.destNode, message, message.destPid);
			writer2.println();
		}
		writer2.close();		
		cycle++;
		return false;
	}

}
