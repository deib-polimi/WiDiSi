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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import peersim.config.Configuration;
import peersim.core.Linkable;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDProtocol;
import peersim.transport.Transport;
import wifidirect.p2pcore.WifiP2pManager.*;


// TODO: Auto-generated Javadoc
/**
 * The Class eventListeners.
 * This class keeps the list of all listeners. This class is an intermediate class for callback messages. All listeners who are intersted to get notified
 * whenever an event has been performed would register itself in this intermidiate class. Then All other classes who are resposnible 
 * to detect events will send their results to this class and this class inform all listeners and boradcast receivers through a callback message.  
 */
public class eventListeners implements EDProtocol {

	/**  transport protocol for event-based simulation *. */
	private static final String PAR_TRASP2 = "transport2";	

	/** The transport id2. */
	private int transportId2;

	/**  Linkable protocol for event-based simulation *. */
	private static final String PAR_LINK = "Linkable";

	/** The linkable id. */
	private int linkableId;

	/**  manager protocol for event-based simulation *. */
	private static final String PAR_MANAGE = "p2pmanager";

	/** The p2pmanager id. */
	private int p2pmanagerId;

	/**  Node Info protocol for event-based simulation *. */
	private static final String PAR_P2PINFO = "p2pinfo";

	/** The p2p info pid. */
	public int p2pInfoPid;

	////////////////////////////////////////////////////////////////
	/** the list of  Wi-Fi P2P PeerList listeners. Any listeners who are interested to receive callbacks of {@link WifiP2pDeviceList} whenever requested will be added to this list *. */
	private Set<PeerListListener> pListeners;

	/** The Wi-Fi P2P Connection-Info Listener-list. Any listeners who are interested to receive callbacks of {@link WifiP2pInfo} whenever requested will be added to this list *. */
	private Set<ConnectionInfoListener> cListeners;

	/** The g listeners. */
	private Set<GroupInfoListener> gListeners;

	/** The dns listeners. */
	private Set<DnsSdServiceResponseListener> dnsListeners;

	/** The txt listeners. */
	private Set<DnsSdTxtRecordListener> txtListeners;

	/** The broadcast receivers. */
	private Set<BroadcastReceiver> broadcastReceivers;
	////////////////////////////////////////////////////////////////

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

	/** The this node. */
	private Node thisNode; 

	/** The this pid. */
	public int thisPid;

	/**
	 * Instantiates a new event listeners.
	 *
	 * @param prefix the prefix
	 */
	public eventListeners (String prefix){
		transportId2 		= Configuration.getPid(prefix + "." + PAR_TRASP2);
		linkableId			= Configuration.getPid(prefix + "." + PAR_LINK);
		p2pmanagerId 		= Configuration.getPid(prefix + "." + PAR_MANAGE);
		p2pInfoPid 			= Configuration.getPid(prefix + "." + PAR_P2PINFO);
		pListeners 			= new HashSet<PeerListListener>();
		cListeners 			= new HashSet<ConnectionInfoListener>();
		gListeners 			= new HashSet<GroupInfoListener>();
		dnsListeners 		= new HashSet<DnsSdServiceResponseListener>();
		txtListeners 		= new HashSet<DnsSdTxtRecordListener>();
		broadcastReceivers 	= new HashSet<BroadcastReceiver>();
		thisNode			= Network.get(0);	// initialize with an arbitrary node to prevent null return
		thisPid				= 0;
	}

	/* (non-Javadoc)
	 * @see peersim.edsim.EDProtocol#processEvent(peersim.core.Node, int, java.lang.Object)
	 */
	public void processEvent(Node node, int pid, Object event) {
		Message message = (Message) event;
		thisNode = node;
		thisPid = pid;
		nodeP2pInfo nodeInfo = (nodeP2pInfo) thisNode.getProtocol(p2pInfoPid);
		//		try{
		//			writer = new PrintWriter(new BufferedWriter(new FileWriter("log/eventListeners.txt", true)));
		//		}
		//		catch (IOException e) {
		//		    System.out.println("File eventListeners.txt not found");
		//		}
		//		writer.println("Node: " + node.getID() + " pid: " + pid + " Message: " + message.event);
		//		writer.close();


		switch (message.event){


		case "GROUP_INFO_REQUEST":


			// if thisNode is Group Owner it has access to the groupInfo class
			if(nodeInfo.getStatus()==CONNECTED && nodeInfo.isGroupOwner()){			
				notifyGroupInfoListeners(nodeInfo.currentGroup);

				// if thisNode is a client then it should ask the groupOwner for the updated version of the groupInfo	
			}else if(nodeInfo.getStatus()==CONNECTED && !nodeInfo.isGroupOwner()){
				Transport transport2 = (Transport) thisNode.getProtocol(transportId2);
				Message newMessage = new Message();
				newMessage.srcNode = thisNode;
				newMessage.srcPid = thisPid;
				newMessage.destPid = thisPid;
				newMessage.event = "CLIENT_GROUP_INFO_REQUEST";
				newMessage.destNode = nodeInfo.getGroupOwner();
				transport2.send(newMessage.srcNode, newMessage.destNode, newMessage, newMessage.destPid);				
			}
			break;

		case "CLIENT_GROUP_INFO_REQUEST":
			if(nodeInfo.isGroupOwner() && nodeInfo.getStatus()==CONNECTED){
				Transport transport2 = (Transport) thisNode.getProtocol(transportId2);
				Message newMessage = new Message();
				newMessage.srcNode = thisNode;
				newMessage.srcPid = thisPid;
				newMessage.destPid = thisPid;
				newMessage.destNode = message.srcNode;
				newMessage.event = "CLIENT_GROUP_INFO_RESPONSE";
				newMessage.object = nodeInfo.currentGroup;
				transport2.send(newMessage.srcNode, newMessage.destNode, newMessage, newMessage.destPid);
			}
			break;

		case "CLIENT_GROUP_INFO_RESPONSE":
			// We have received the groupInfo request result from the GO - Broadcast it to all registedred listeners
			if(!nodeInfo.isGroupOwner() && nodeInfo.getStatus()==CONNECTED){
				notifyGroupInfoListeners((WifiP2pGroup)message.object);
			}
			break;

		case "WIFI_P2P_PEERS_CHANGED_ACTION":
			if(nodeInfo.isWifiP2pEnabled() && nodeInfo.isPeerDiscoveryStarted()){
				notifyBroadcastReceivers("WIFI_P2P_PEERS_CHANGED_ACTION");
			}
			break;

		case "REQUEST_PEERS":
			notifyPeerListListeners("REQUEST_PEERS");
			break;

		case "WIFI_P2P_STATE_CHANGED_ACTION":
			if(nodeInfo.isWifiP2pEnabled())
				notifyBroadcastReceivers("WIFI_P2P_STATE_CHANGED_ACTION");
			break;

		case "REQUEST_CONNECTION_INFO":
			notifyConInfoListeners("WIFI_P2P_CONNECTION_CHANGED_ACTION");
			break;

		case "WIFI_P2P_CONNECTION_CHANGED_ACTION":
			//notifyConInfoListeners("WIFI_P2P_CONNECTION_CHANGED_ACTION");
			if(nodeInfo.isWifiP2pEnabled())
				notifyBroadcastReceivers("WIFI_P2P_CONNECTION_CHANGED_ACTION");
			break;

		case "WIFI_P2P_THIS_DEVICE_CHANGED_ACTION":
			if(nodeInfo.isWifiP2pEnabled())
				notifyBroadcastReceivers("WIFI_P2P_THIS_DEVICE_CHANGED_ACTION");

			break;

		case "onBonjourServiceAvailable":
			if(nodeInfo.isWifiP2pEnabled() && nodeInfo.isServiceDiscoveryStarted()){
				wifiP2pService p2pservice = (wifiP2pService) message.object;
				//System.out.println(p2pservice);
				notifyDnsInfoListeners("onBonjourServiceAvailable", p2pservice);
				if(!p2pservice.serviceRecord.isEmpty()){
					notifyTxtInfoListeners("DNS_TEXT_RECORD_AVAILABLE", p2pservice);
				}
			}
			break;
			
		case "SCAN_RESULTS_AVAILABLE_ACTION":
			notifyBroadcastReceivers("SCAN_RESULTS_AVAILABLE_ACTION");
			break;

		default:
			break;		
		}			
	}	

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public eventListeners clone(){
		eventListeners evl = null;
		try { evl = (eventListeners) super.clone(); }
		catch( CloneNotSupportedException e ) {} // never happen
		evl.transportId2 = transportId2;
		evl.linkableId = linkableId;
		evl.p2pmanagerId = p2pmanagerId;
		evl.p2pInfoPid = p2pInfoPid;
		evl.pListeners = new HashSet<PeerListListener>();
		evl.cListeners = new HashSet<ConnectionInfoListener>();
		evl.gListeners = new HashSet<GroupInfoListener>();
		evl.dnsListeners = new HashSet<DnsSdServiceResponseListener>();
		evl.txtListeners = new HashSet<DnsSdTxtRecordListener>();
		evl.broadcastReceivers = new HashSet<BroadcastReceiver>();
		evl.thisNode	= Network.get(0);
		evl.thisPid = 0;
		return evl;	
	}

	/**
	 * Adds the broadcast receiver. This method will be called by the WifiP2pManager.registerboradcastreceiver
	 *
	 * @param receiver the receiver
	 */
	public void addBroadcastReceiver(BroadcastReceiver receiver) {
		if(!broadcastReceivers.contains(receiver)){
			broadcastReceivers.add(receiver);
		}
	}

	/**
	 * Removes the broadcast receiver.
	 *
	 * @param receiver the receiver
	 */
	public void removeBroadcastReceiver(BroadcastReceiver receiver) {
		if(broadcastReceivers.contains(receiver)){
			broadcastReceivers.remove(receiver);
		}
	}

	/**
	 * Adds the peer list listener.
	 *
	 * @param listener the listener
	 */
	public void addPeerListListener(PeerListListener listener) {
		if(!pListeners.contains(listener)){
			pListeners.add(listener);
		}
	}

	/**
	 * Adds the con info listener.
	 *
	 * @param listener the listener
	 */
	public void addConInfoListener(ConnectionInfoListener listener) {
		if(!cListeners.contains(listener)){
			cListeners.add(listener);
		}
	}

	/**
	 * Adds the group info listener.
	 *
	 * @param listener the listener
	 */
	public void addGroupInfoListener(GroupInfoListener listener) {
		if(!gListeners.contains(listener)){
			gListeners.add(listener);
		}
	}

	/**
	 * Adds the dns sd response listener.
	 *
	 * @param listener the listener
	 */
	public void addDnsSdResponseListener(DnsSdServiceResponseListener listener) {
		if(!dnsListeners.contains(listener)){
			dnsListeners.add(listener);
		}
	}

	/**
	 * Adds the dns sd txt record listener.
	 *
	 * @param listener the listener
	 */
	public void addDnsSdTxtRecordListener(DnsSdTxtRecordListener listener) {
		if(!txtListeners.contains(listener)){
			txtListeners.add(listener);
		}
	}

	/**
	 * Notify broadcast receivers.
	 *
	 * @param action the action
	 */
	public void notifyBroadcastReceivers(String action){
		for (BroadcastReceiver receiver: broadcastReceivers) {
			receiver.onReceive(new wifiP2pEvent(this, action, action));
		}
	}

	/**
	 * Notify peer list listeners.
	 *
	 * @param action the action
	 */
	private void notifyPeerListListeners(String action) {
		nodeP2pInfo nodeInfo = (nodeP2pInfo) thisNode.getProtocol(p2pInfoPid);
		if(nodeInfo.isPeerDiscoveryStarted()){
			Linkable idleLink = (Linkable) thisNode.getProtocol(linkableId);
			ArrayList<WifiP2pDevice> neighborList = new ArrayList<WifiP2pDevice>();

			// The number of possible peers should not exceed the maximum number of discoverable devices (limitation)
			int tempMin = (idleLink.degree() <= nodeP2pInfo.maxNumDevices) ? idleLink.degree() : nodeP2pInfo.maxNumDevices;
			for(int i=0; i<idleLink.degree(); i++){
				nodeP2pInfo neighborInfo = (nodeP2pInfo) idleLink.getNeighbor(i).getProtocol(p2pInfoPid);
				if(neighborInfo.isPeerDiscoveryStarted()){
					neighborList.add(new WifiP2pDevice(idleLink.getNeighbor(i), p2pInfoPid));
					if(neighborList.size()>=tempMin) break;
				}
			}
			WifiP2pDeviceList peers = new WifiP2pDeviceList(neighborList);

			// Send the list of Peers to all registedred listeners
			for (PeerListListener peerListener: pListeners) {
				peerListener.onPeersAvailable(peers);
			}
		}
	}

	/**
	 * Notify con info listeners.
	 *
	 * @param action the action
	 */
	private void notifyConInfoListeners(String action) {
		nodeP2pInfo nodeInfo = (nodeP2pInfo) thisNode.getProtocol(p2pInfoPid);
		if(nodeInfo.getGroupOwner()!=null){
			nodeP2pInfo GOInfo = (nodeP2pInfo) nodeInfo.getGroupOwner().getProtocol(p2pInfoPid);
			WifiP2pInfo wifiInfo = new WifiP2pInfo((nodeInfo.getStatus()==CONNECTED), nodeInfo.isGroupOwner(), GOInfo.getMacAddress());
			for (ConnectionInfoListener conListener: cListeners) {
				conListener.onConnectionInfoAvailable(wifiInfo);
			}
		}
	}

	/**
	 * Notify group info listeners.
	 *
	 * @param groupInfo the group info
	 */
	private void notifyGroupInfoListeners(WifiP2pGroup groupInfo) {
		for (GroupInfoListener groupListener: gListeners) {
			groupListener.onGroupInfoAvailable(groupInfo);
		}
	}

	/**
	 * Notify dns info listeners.
	 *
	 * @param action the action
	 * @param p2pservice the p2pservice
	 */
	private void notifyDnsInfoListeners(String action, wifiP2pService p2pservice) {
		WifiP2pDevice srcDevice = new WifiP2pDevice(p2pservice.serviceHolder, p2pInfoPid);
		for (DnsSdServiceResponseListener dnsListener: dnsListeners) {
			// WE SHOULD SEND BACK WHAT IS REQUIRED -- COMPLETE THIS
			dnsListener.onDnsSdServiceAvailable(p2pservice.serviceName,
					p2pservice.serviceType, srcDevice);
		}
	}

	/**
	 * Notify TxtInfoListeners when a newly discovered service contains records. This method will call 
	 * onDnsSdTxtRecordAvailable methods on all listeners and pass the serviceRecord to them.
	 * @param action the action
	 * @param p2pservice the p2pservice
	 */
	private void notifyTxtInfoListeners(String action, wifiP2pService p2pservice) {
		WifiP2pDevice srcDevice = new WifiP2pDevice(p2pservice.serviceHolder, p2pInfoPid);
		for (DnsSdTxtRecordListener txtListener: txtListeners) {
			// WE SHOULD SEND BACK WHAT IS REQUIRED -- COMPLETE THIS
			txtListener.onDnsSdTxtRecordAvailable(p2pservice.serviceName, p2pservice.serviceRecord, srcDevice);
		}
	}

}
