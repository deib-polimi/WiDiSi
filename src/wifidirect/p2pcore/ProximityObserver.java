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

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.core.Linkable;
import peersim.core.Node;
import peersim.edsim.EDProtocol;
import peersim.transport.Transport;
import visualization.Visualizer;
import wifi.WifiManager;
import wifidirect.nodemovement.NodeMovement;


// TODO: Auto-generated Javadoc
/**
 * An asynchronous update interface for receiving notifications
 * about Proximity information as the Proximity is constructed.
 */
public class ProximityObserver implements EDProtocol, CDProtocol{

	/** The Constant CONNECTED. */
	public static final int CONNECTED   = 0;

	/** The Constant INVITED. */
	public static final int INVITED     = 1;

	/** The Constant FAILED. */
	public static final int FAILED      = 2;

	/** The Constant AVAILABLE. */
	public static final int AVAILABLE   = 3;

	/** The Constant UNAVAILABLE. */
	public static final int UNAVAILABLE = 4;

	/** The Constant PARLINK. */
	private static final String PARLINK 	= "linkable";    // NodeP2P Info set in the configuration

	/** The Constant PAR_TRASP1. */
	public static final String PAR_TRASP1 	= "transport1";  // PeerDiscovery Transport

	/** The Constant PAR_TRASP2. */
	public static final String PAR_TRASP2 	= "transport2";  //ServiceDiscovery Transport

	/** The Constant PAR_LISTENER. */
	public static final String PAR_LISTENER = "listeners";

	/** The Constant PAR_P2PINFO. */
	public static final String PAR_P2PINFO 	= "p2pinfo";

	/** The Constant PAR_TRASP0. */
	public static final String PAR_TRASP0 	= "transport0"; // Zero delay Zero Drop Rate

	/** The Constant PAR_MANAGE. */
	public static final String PAR_MANAGE 	= "p2pmanager";


	/** The linkable id. */
	private int linkableID;

	/** The transport id1. */
	public int transportId1;

	/** The transport id2. */
	public int transportId2;

	/** The listener pid. */
	public int listenerPid;

	/** The p2p info pid. */
	public int p2pInfoPid;

	/** The transport id0. */
	public int transportId0;

	/** The p2pmanager id. */
	public int p2pmanagerId;

	private int wifimanager;

	/** The a15 seconds. */
	public long a15Seconds;


	/** The cycle. */
	private int cycle=0;

	/** The pre neighbor list. */
	private ArrayList<Node> preNeighborList;
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */

	/**
	 * This method is called when information about an Proximity
	 * which was previously requested using an asynchronous
	 * interface becomes available.
	 *
	 * @param prefix the prefix
	 */
	public ProximityObserver (String prefix){
		linkableID 		= Configuration.getPid(prefix + "." + PARLINK);
		transportId0 	= Configuration.getPid(prefix + "." + PAR_TRASP0);
		transportId1 	= Configuration.getPid(prefix + "." + PAR_TRASP1);
		transportId2 	= Configuration.getPid(prefix + "." + PAR_TRASP2);
		listenerPid 	= Configuration.getPid(prefix + "." + PAR_LISTENER);
		p2pInfoPid 		= Configuration.getPid(prefix + "." + PAR_P2PINFO);
		p2pmanagerId 	= Configuration.getPid(prefix + "." + PAR_MANAGE);
		wifimanager 	= Configuration.getPid(prefix + "." + "wifimanager");
		preNeighborList = new ArrayList<Node>();
		cycle			= 0;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public ProximityObserver clone(){
		ProximityObserver PO = null;
		try { PO = (ProximityObserver) super.clone(); }
		catch( CloneNotSupportedException e ) {} // never happen
		PO.linkableID 		= linkableID;
		PO.transportId0 	= transportId0;
		PO.transportId1 	= transportId1;
		PO.transportId2 	= transportId2;
		PO.listenerPid 		= listenerPid;
		PO.p2pInfoPid 		= p2pInfoPid;
		PO.p2pmanagerId		= p2pmanagerId;
		PO.wifimanager		= wifimanager;
		PO.preNeighborList 	= new ArrayList<Node>();
		PO.cycle 			= 0;
		return PO;	
	}

	/* (non-Javadoc)
	 * @see peersim.cdsim.CDProtocol#nextCycle(peersim.core.Node, int)
	 */
	@Override
	public void nextCycle(Node node, int protocolID) {
		a15Seconds = (int) (150000/NodeMovement.CycleLenght);;
		Linkable  		neighbor 	= (Linkable) node.getProtocol(linkableID);
		Transport 		transport1 	= (Transport) node.getProtocol(transportId1);
		Transport 		transport0 	= (Transport) 	node.getProtocol(transportId0);
		nodeP2pInfo 	nodeInfo 	= (nodeP2pInfo) node.getProtocol(p2pInfoPid);
		//at first cycle we just put the current neighbor list inside the preNeighborList
		if(cycle==0){
			preNeighborList.clear();
			for(int i=0; i<neighbor.degree(); i++){
				preNeighborList.add(neighbor.getNeighbor(i));
			}
		}else{

			// Create an arrayList of current neighbor List
			ArrayList<Node> currentNeighborList = new ArrayList<Node>();
			for(int i=0; i<neighbor.degree(); i++){
				currentNeighborList.add(neighbor.getNeighbor(i));
			}

			//if true some changes have happened. send notification
			if(!sameList(preNeighborList, currentNeighborList)){
				Message message = new Message();
				message.event = "WIFI_P2P_PEERS_CHANGED_ACTION";
				message.srcNode = node;
				message.srcPid = protocolID;
				message.destNode = node;
				message.destPid = listenerPid;
				transport1.send(message.srcNode, message.destNode, message , message.destPid);
			}

			// Find new nodes and send thisNode service if conditions met
			if(nodeInfo.isWifiP2pEnabled() && nodeInfo.isPeerDiscoveryStarted() && !nodeInfo.getWifiP2pServiceList().isEmpty()){
				for(Node currentNeighborNode: currentNeighborList){
					if(!preNeighborList.contains(currentNeighborNode)){
						//this means this is a new Node -- if we have services inform the others. they will check thier own conditions themselves
						Message message 	= new Message();
						message.event 		= "onBonjourServiceAvailable";
						message.srcNode 	= node;
						message.srcPid 		= protocolID;
						message.destNode 	= currentNeighborNode;
						message.destPid 	= listenerPid;
						Transport transport2 = (Transport) node.getProtocol(transportId2);
						for(wifiP2pService service:nodeInfo.getWifiP2pServiceList()){
							message.object=service;
							transport2.send(message.srcNode, message.destNode, message , message.destPid);
						}
					}
				}
			}
			preNeighborList.clear();
			preNeighborList.addAll(currentNeighborList);

			// 15 seconds group formation bound

			if(nodeInfo.getStatus() == INVITED){
				if(nodeInfo.getInvitationTime() > a15Seconds){
					Visualizer.print("Node: " + node.getID() + " Group Invitation TimeOut", Color.RED);
					nodeInfo.setStatus(AVAILABLE);
					nodeInfo.setGroupOwner(null);
					nodeInfo.setInvitedBy(null);
					nodeInfo.setInvitationTime(0);

					Visualizer.mDwindow.timeOutInfo.setText(String.valueOf(Integer.parseInt(Visualizer.mDwindow.timeOutInfo.getText())+1));
				}else{
					nodeInfo.setInvitationTime(nodeInfo.getInvitationTime()+1);
				}
			}

			// Check if you are the group Owner and every clients are still at your proximity
			if(nodeInfo.isGroupOwner() && nodeInfo.currentGroup.getSSID()!=null){
				List<Node> nodesToBeRemoved = new ArrayList<Node>();
				for (Node CNode: nodeInfo.currentGroup.getNodeList()){
					if (!currentNeighborList.contains(CNode)){
						nodesToBeRemoved.add(CNode);
					}
				}
				for (Node rNode: nodesToBeRemoved){
					nodeInfo.currentGroup.removeNode(rNode);

					// remove p2p  
					nodeP2pInfo rNodeInfo = (nodeP2pInfo) rNode.getProtocol(p2pInfoPid);
					if (rNodeInfo.getStatus()==CONNECTED && rNodeInfo.getGroupOwner().getID()==node.getID()){						
						Message newMessage 	= new Message();
						newMessage.destNode = rNode;
						newMessage.destPid 	= p2pmanagerId;
						newMessage.srcNode 	= node;
						newMessage.srcPid 	= protocolID;
						newMessage.event 	="REQUEST_CANCEL_CONNECT";
						transport0.send(newMessage.srcNode, newMessage.destNode, newMessage, newMessage.destPid);
					}

					// remove legacy wifi clients
					WifiManager wifiManager = (WifiManager) rNode.getProtocol(wifimanager);
					if(wifiManager.apSSID!=null){
						if(wifiManager.getWifiStatus()==CONNECTED && wifiManager.apSSID.equals(nodeInfo.currentGroup.getSSID())){
							nodeInfo.currentGroup.removeNode(rNode);
							Message newMessage 	= new Message();
							newMessage.destNode = rNode;
							newMessage.destPid 	= wifimanager;
							newMessage.srcNode 	= node;
							newMessage.srcPid 	= protocolID;
							newMessage.event 	="REQUEST_CANCEL_CONNECT";
							transport0.send(newMessage.srcNode, newMessage.destNode, newMessage, newMessage.destPid);
						}
					}
				}
			}
		}
		cycle++;
	}


	// Check if the the current list and previouslist are the same. 
	/**
	 * This method is called when information about an Proximity
	 * which was previously requested using an asynchronous
	 * interface becomes available.
	 *
	 * @param pre the pre
	 * @param current the current
	 * @return true, if the same list
	 */
	// The following function will keep those elements which is available in both list
	private boolean sameList(ArrayList<Node> pre, ArrayList<Node> current){
		boolean past = false;
		boolean post = false;

		for(Node aNode: pre){
			if(!current.contains(aNode)) post = true;
		}

		for(Node aNode: current){
			if(!pre.contains(aNode)) past = true;
		}
		return !(post || past);	
	}


	@Override
	public void processEvent(Node arg0, int arg1, Object arg2) {
		// TODO Auto-generated method stub

	}

}
