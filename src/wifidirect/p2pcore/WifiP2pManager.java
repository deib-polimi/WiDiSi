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

import java.util.EventListener;
import java.util.Map;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Linkable;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDProtocol;
import peersim.transport.Transport;
import wifi.WifiManager;


// TODO: Auto-generated Javadoc
/**
 * The Class WifiP2pManager.
 */
public class WifiP2pManager implements EDProtocol{

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

	/** The Constant PAR_LISTENER. */
	private static final String PAR_LISTENER = "listeners"; /**  eventDetection protocol for event-based simulation *. */
	private static final String PAR_PROTOCOL = "linkable";

	/** The Constant PAR_P2PINFO. */
	private static final String PAR_P2PINFO 	= "p2pinfo";	  /**  P2PInfo protocol for event-based simulation *. */
	private static final String PAR_TRASP0 	= "transport0"; // Zero delay Zero Drop Rate

	/** The Constant PAR_TRASP1. */
	private static final String PAR_TRASP1 	= "transport1"; // Peer Discovery and so on

	/** The Constant PAR_TRASP2. */
	private static final String PAR_TRASP2 	= "transport2"; // Service Discovery and so on

	/** The Constant PAR_TRASP3. */
	private static final String PAR_TRASP3 	= "transport3"; // Socket Message Delivery

	/** The Constant PAR_TRASP4. */
	private static final String PAR_TRASP4 	= "transport4"; // Group Invitation

	/** The Constant PAR_TRASP5. */
	private static final String PAR_TRASP5 	= "transport5"; // Internal delivery

	/** The transport id0. */
	private int transportId0;

	/** The transport id1. */
	private int transportId1;

	/** The transport id2. */
	private int transportId2;

	/** The transport id3. */
	private int transportId3;

	/** The transport id4. */
	private int transportId4;

	/** The transport id5. */
	private int transportId5;

	/** The linkable id. */
	private int linkableId;

	/** The listener pid. */
	private int listenerPid;

	/** The p2p info pid. */
	private int p2pInfoPid;

	private int wifimanagerpid;

	/**
	 * The lookup key for an int that indicates whether Wi-Fi p2p is enabled or disabled.
	 * Retrieve it with {@link android.content.Intent#getIntExtra(String,int)}.
	 *
	 * @see #WIFI_P2P_STATE_DISABLED
	 * @see #WIFI_P2P_STATE_ENABLED
	 */
	public static final String EXTRA_WIFI_STATE = "wifi_p2p_state";
	/**
	 * Wi-Fi p2p is disabled.
	 *
	 * @see #WIFI_P2P_STATE_CHANGED_ACTION
	 * @see #getWifiP2pState()
	 */
	public static final int WIFI_P2P_STATE_DISABLED = 1;
	/**
	 * Wi-Fi p2p is enabled.
	 *
	 * @see #WIFI_P2P_STATE_CHANGED_ACTION
	 * @see #getWifiP2pState()
	 */
	public static final int WIFI_P2P_STATE_ENABLED = 2;

	/** The this node. */
	public Node thisNode;
	/** The this pid. */
	public int thisPid;

	/** The msg handler. */
	private Callback msgHandler;

	/**
	 * Instantiates a new wifi p2p manager.
	 *
	 * @param prefix the prefix
	 */
	public WifiP2pManager(String prefix) {
		linkableId 	 = Configuration.getPid(prefix + "." + PAR_PROTOCOL);
		transportId0 = Configuration.getPid(prefix + "." + PAR_TRASP0);
		transportId1 = Configuration.getPid(prefix + "." + PAR_TRASP1);
		transportId2 = Configuration.getPid(prefix + "." + PAR_TRASP2);
		transportId3 = Configuration.getPid(prefix + "." + PAR_TRASP3);
		transportId4 = Configuration.getPid(prefix + "." + PAR_TRASP4);
		transportId5 = Configuration.getPid(prefix + "." + PAR_TRASP5);
		p2pInfoPid 	 = Configuration.getPid(prefix + "." + PAR_P2PINFO);
		listenerPid  = Configuration.getPid(prefix + "." + PAR_LISTENER);
		wifimanagerpid = Configuration.getPid(prefix + "." + "wifimanager");
		thisNode 	 = Network.get(0);	// initiate with some node just to avoid "if (thisNode!=null)"
		thisPid 	 = 0;
		msgHandler 	 = null;
	}

	/** (non-Javadoc)
	 * @see peersim.edsim.EDProtocol#processEvent(peersim.core.Node, int, java.lang.Object)
	 * This method will be called by event-driven engine whenever an event is available for a particular Protocol ({@value pid}) in a specific Node ({@value node}) in the network. This method has to be public; however, an application must not call this method since it is an internal method for the simulator.
	 */
	@Override
	public void processEvent(Node node, int pid, Object event) {
		thisNode = node;
		thisPid = pid;

		Message 	message 	= (Message) 	event;
		nodeP2pInfo nodeInfo 	= (nodeP2pInfo) thisNode.getProtocol(p2pInfoPid);
		nodeP2pInfo senderInfo 	= (nodeP2pInfo) message.srcNode.getProtocol(p2pInfoPid);
		Linkable 	neighbors 	= (Linkable) 	thisNode.getProtocol(linkableId);
		Transport 	transport0 	= (Transport) 	thisNode.getProtocol(transportId0);
		Transport 	transport2 	= (Transport) 	thisNode.getProtocol(transportId2);
		Transport 	transport5 	= (Transport) 	thisNode.getProtocol(transportId5);

		switch (message.event){
		// The request for connect will ask the receiving node in order to connect
		// We are facing 4 different scenarios:
		// 1- An unconnected peer send reques to another unconnected peer: Standard group formation
		// 2- A connected peer (not group owner) send invitation to unconnected peer to join the same group
		// 3- A group owner send invitation to unconnected peer
		// 4- An unconnected peer send join reuqest to a group owner
		case "REQUEST_FOR_CONNECT":

			// Check if the sender of the request is inside the proximity of this Node
			// If the sender of the request is not inside the proximity nothing will happen
			boolean avl=false;
			for(int i=0; i<neighbors.degree(); i++){
				if(neighbors.getNeighbor(i).getID()==message.srcNode.getID()){
					avl=true;
					break;
				}
			}
			if(avl){
				// Sender node is not a connected node
				if(senderInfo.getStatus()==AVAILABLE){
					// Receiver node is not connected - They will negotiate (Standard group formation)
					if(nodeInfo.getStatus()==AVAILABLE){

						nodeInfo.setStatus(INVITED);
						nodeInfo.setInvitedBy(message.srcNode);
						nodeInfo.setInvitationTime(0);

						Message newMessage 	= new Message();
						newMessage.destNode = message.srcNode;
						newMessage.destPid 	= message.srcPid;
						newMessage.srcNode 	= thisNode;
						newMessage.srcPid 	= thisPid;
						newMessage.event 	= "NEGOTIATE_INTENT";
						newMessage.object 	= String.valueOf(nodeInfo.getGoIntentionValue());
						transport2.send(newMessage.srcNode, newMessage.destNode, newMessage, newMessage.destPid);

						// Receiver node is Group Owner -- accept the connection if there is free space inside the group
					}else if(nodeInfo.getStatus()==CONNECTED && nodeInfo.isGroupOwner() && 
							nodeInfo.currentGroup.isGroupValid() &&		
							(nodeInfo.currentGroup.getGroupSize()<nodeInfo.getGroupLimit())){   
						nodeInfo.currentGroup.addNode(message.srcNode);
						Message newMessage 	= new Message();
						newMessage.destNode = message.srcNode;
						newMessage.destPid 	= message.srcPid;
						newMessage.srcNode 	= thisNode;
						newMessage.srcPid 	= thisPid;
						newMessage.event 	= "CONNECTION_REQUEST_ACCEPTED";
						// When the sender is not connected and we actually accept its connect request we have to pay attention to this fact
						// that the CONNECTION_REQUEST_ACCEPTED event which is sent back to the requester should be transfered using Zero delay zero drop rate mechanism
						// the reason is that: the MAC layer which is not actually implemented here is resposnsible to take care of undelivered messages
						// We are actually mimic the behaviour of the mac and phy and wireless channel in the transport 1-5 protocols
						// so we should be sure that when we accept a connection request, both parties will at the end get connected
						// therefore a connection should not get accepted or if got accepted, the sender must get the acknowledge
						transport0.send(newMessage.srcNode, newMessage.destNode, newMessage, newMessage.destPid);
					}				

					// sender node is group owner
				}else if(senderInfo.getStatus()==CONNECTED && senderInfo.isGroupOwner()){ 
					if(nodeInfo.getStatus()==AVAILABLE || (nodeInfo.getStatus()==INVITED && nodeInfo.getInvitedBy().getID() == message.srcNode.getID())){ // can accept the connect request
						nodeInfo.setGroupOwner(message.srcNode);
						nodeInfo.setStatus(CONNECTED);
						stopPeerDiscovery();

						Message newMessage 	= new Message();
						newMessage.destNode = message.srcNode;
						newMessage.destPid 	= message.srcPid;
						newMessage.srcNode 	= thisNode;
						newMessage.srcPid 	= thisPid;
						newMessage.event 	= "CONNECTION_REQUEST_ACCEPTED";
						transport0.send(newMessage.srcNode, newMessage.destNode, newMessage, newMessage.destPid);				
					}

					// sender node is connected but not group owner
				}else if(senderInfo.getStatus()==CONNECTED && !senderInfo.isGroupOwner()){ 
					if(nodeInfo.getStatus() == AVAILABLE){

						nodeInfo.setGroupOwner(senderInfo.getGroupOwner());
						nodeInfo.setStatus(CONNECTED);
						stopPeerDiscovery();

						// send acknowledgment to the group owner
						Message newMessage 	= new Message();
						newMessage.destNode = senderInfo.getGroupOwner(); 
						newMessage.destPid 	= message.srcPid;
						newMessage.srcNode 	= thisNode;
						newMessage.srcPid 	= thisPid;
						newMessage.event 	= "CONNECTION_REQUEST_ACCEPTED";
						transport0.send(newMessage.srcNode, newMessage.destNode, newMessage, newMessage.destPid);
					}
				}
			}
			break;

		case "NEGOTIATE_INTENT":
			if(nodeInfo.getStatus()==AVAILABLE || (nodeInfo.getStatus()==INVITED && nodeInfo.getInvitedBy().getID() == message.srcNode.getID())){
				// If this Intention is larger than source intention => creat a group and invite the node
				if(nodeInfo.getGoIntentionValue() >= Integer.parseInt((String)message.object)){

					createGroup();

					Message newMessage 	= new Message();
					newMessage.destNode = message.srcNode;
					newMessage.destPid 	= message.srcPid;
					newMessage.srcNode 	= thisNode;
					newMessage.srcPid 	= thisPid;
					newMessage.event 	= "REQUEST_FOR_CONNECT";
					transport0.send(newMessage.srcNode, newMessage.destNode, newMessage, newMessage.destPid);
				}else { // if source intention is higher than this send your intention with NEGOTIATE_INTENT event
					// at the other side the above condition will meet
					nodeInfo.setStatus(INVITED);
					nodeInfo.setInvitedBy(message.srcNode);
					nodeInfo.setInvitationTime(0);

					Message newMessage 	= new Message();
					newMessage.destNode = message.srcNode;
					newMessage.destPid 	= message.srcPid;
					newMessage.srcNode 	= thisNode;
					newMessage.srcPid 	= thisPid;
					newMessage.event 	= "NEGOTIATE_INTENT";
					newMessage.object 	= String.valueOf(nodeInfo.getGoIntentionValue());
					transport0.send(newMessage.srcNode, newMessage.destNode, newMessage, newMessage.destPid);
				}
			}
			break;

		case "CONNECTION_REQUEST_ACCEPTED":
			// if this node is GO
			if(nodeInfo.isGroupOwner() && nodeInfo.currentGroup.isGroupValid() && nodeInfo.getStatus()==CONNECTED){
				// if there is free space for new client
				if (nodeInfo.currentGroup.getGroupSize()<nodeInfo.getGroupLimit()){ 
					nodeInfo.currentGroup.addNode(message.srcNode);

					// if there is not free space for new client
				}else{
					Message newMessage 	= new Message();
					newMessage.destNode = message.srcNode;
					newMessage.destPid 	= message.srcPid;
					newMessage.srcNode 	= thisNode;
					newMessage.srcPid 	= thisPid;
					newMessage.event 	= "REQUEST_CANCEL_CONNECT";
					transport5.send(newMessage.srcNode, newMessage.destNode, newMessage, newMessage.destPid);
				}

				// If sender is GO and this node is not connected
			}else if((nodeInfo.getStatus()==AVAILABLE ||  (nodeInfo.getStatus()==INVITED && nodeInfo.getInvitedBy().getID() == message.srcNode.getID()))&& senderInfo.isGroupOwner()){
				//nodeInfo.currentGroup.resetGroup(); // no need to keep a group class inside a client
				nodeInfo.setGroupOwner(message.srcNode);
				nodeInfo.setStatus(CONNECTED);
			}else { // otherwise there is not possible  (idealy should never happen)
				Message newMessage 	= new Message();
				newMessage.destNode = message.srcNode;
				newMessage.destPid 	= message.srcPid;
				newMessage.srcNode 	= thisNode;
				newMessage.srcPid 	= thisPid;
				newMessage.event 	= "REQUEST_CANCEL_CONNECT";
				transport5.send(newMessage.srcNode, newMessage.destNode, newMessage, newMessage.destPid);
			}
			break;

		case "REQUEST_CANCEL_CONNECT":
			// This node is Group Owner
			if(nodeInfo.isGroupOwner()){
				Node targetNode = null;
				for(Node tempNode: nodeInfo.currentGroup.getNodeList()){
					if(tempNode.getID()==message.srcNode.getID()){
						targetNode = tempNode;
						break;
					}
				}
				if(targetNode!=null){
					nodeInfo.currentGroup.removeNode(targetNode);
				}
				// if we are group owner and the last node inside the group sent its cancel request then the group should be removed
				// and the status should go to UNAVAILABLe and the discovery should be stopped
				// note that the discovery of Group owner never stops after CONNECTED unless in this case
				if(nodeInfo.currentGroup.getNodeList().isEmpty()){
					nodeInfo.currentGroup.resetGroup();
					nodeInfo.setStatus(AVAILABLE);
					nodeInfo.setGroupOwner(null);
					stopPeerDiscovery();
				}
			}// the sender is Group Owner and this node is a client
			else if(nodeInfo.getGroupOwner()!=null){
				if(nodeInfo.getGroupOwner().getID() == message.srcNode.getID() && nodeInfo.getStatus()==CONNECTED){ 
					nodeInfo.currentGroup.resetGroup();
					nodeInfo.setStatus(AVAILABLE);
					nodeInfo.setGroupOwner(null);
				}
			}
			break;

		case "SOCKET_DELIVERY":
			if(nodeInfo.getGroupOwner()!=null && senderInfo.getGroupOwner()!=null){
				if((nodeInfo.isGroupOwner() && senderInfo.getGroupOwner().getID()==thisNode.getID() 
						&& nodeInfo.currentGroup.getNodeList().contains(message.srcNode)) ||
						(senderInfo.isGroupOwner() && nodeInfo.getGroupOwner().getID() == message.srcNode.getID() &&
						senderInfo.currentGroup.getNodeList().contains(thisNode))){

					callbackMessage cMessage = (callbackMessage) message.object;
					if(msgHandler!=null){
						msgHandler.handleMessage(cMessage);
					}
				}
			}

			//considering delivery from WiFi Interface
			WifiManager senderWifiManager = (WifiManager) message.srcNode.getProtocol(wifimanagerpid);
			if(senderWifiManager.BSSID!=null){
				if(nodeInfo.isGroupOwner() && nodeInfo.currentGroup.getNodeList().contains(message.srcNode)
						&& senderWifiManager.BSSID.equals(String.valueOf(thisNode.getID()))){
					callbackMessage cMessage = (callbackMessage) message.object;
					if(msgHandler!=null){
						msgHandler.handleMessage(cMessage);
					}
				}
			}
			break;

		case "REQUEST_WIFI_CONNECT":
			if(nodeInfo.isGroupOwner() && nodeInfo.currentGroup.getGroupSize()<=WifiP2pGroup.groupCapacity){
				nodeInfo.currentGroup.addNode(message.srcNode);
				Message newMessage 	= new Message();
				newMessage.destNode = message.srcNode;
				newMessage.destPid 	= message.srcPid;
				newMessage.srcNode 	= thisNode;
				newMessage.srcPid 	= thisPid;
				newMessage.event 	= "CONNECTION_REQUEST_ACCEPTED";
				transport5.send(newMessage.srcNode, newMessage.destNode, newMessage, newMessage.destPid);
			}
			break;
		}
	}

	/**
	 * Register handler.
	 * Should be called by any class that implements Callback interface
	 *
	 * @param callback the callback
	 */
	public void registerHandler (Callback callback){
		msgHandler = callback;
	}
	/**
	 *  Interface for callback invocation when peer list is changed.
	 *  This interface should be implemented by any class which are eager to receive call back information whenever PeerList has been updated. The class is needed to register by invoking {@link registerPeerListener}
	 *
	 * @see PeerListEvent
	 */
	public interface PeerListListener extends EventListener{

		/**
		 * Whenever the PeerList has changed, the application will receive the list of new Peers in {@link WifiP2pDeviceList} format 
		 * via the this method. This would be an Asynchronous call.
		 *
		 * @param peers the peers
		 */
		public void onPeersAvailable(WifiP2pDeviceList peers);
	}

	/**
	 * Register peer listener. Before an application is able to receive callback messages for new available peers, it should register itself by invoking this method. If the same class imlements the {@link PeerListListener}, the application should pass "this" as the obj to the {@link registerPeerListener (PeerListListener obj)}.
	 *
	 * @param obj the obj
	 */
	public void registerPeerListener (PeerListListener obj){
		eventListeners eventlist = (eventListeners) thisNode.getProtocol(listenerPid);
		eventlist.addPeerListListener(obj);
	}

	/**
	 *  Interface for callback invocation when connection info is available.
	 *  Before an application is able to receive callback messages for new changes in the connection, it should register itself by invoking this method. If the same class imlements the {@link PeerListListener}, the application should pass "this" as the obj to the {@link registerPeerListener (PeerListListener obj)}.
	 *
	 * @see ConnectionInfoEvent
	 */
	public interface ConnectionInfoListener extends EventListener{

		/**
		 * The requested connection info is available.
		 *
		 * @param wifiInfo the wifi info
		 */
		public void onConnectionInfoAvailable(WifiP2pInfo wifiInfo);
	}

	/**
	 * Register connection info listener.
	 * This method should be called to register a listener for receiving events regarding the WiFi P2P Connection Info
	 *
	 * @param obj the obj
	 */
	public void registerConInfoListener (ConnectionInfoListener obj){
		eventListeners eventlist;
		eventlist = (eventListeners) thisNode.getProtocol(listenerPid);
		eventlist.addConInfoListener(obj);
	}

	/**
	 *  Interface for callback invocation when group info is available.
	 *
	 * @see GroupInfoEvent
	 */
	public interface GroupInfoListener extends EventListener{

		/**
		 * The requested p2p group info is available.
		 *
		 * @param groupInfo the group info
		 */
		public void onGroupInfoAvailable(WifiP2pGroup groupInfo);
	}

	/**
	 * Register group info listener.
	 *
	 * @param obj the obj
	 */
	public void registerGroupInfoListener (GroupInfoListener obj){
		eventListeners eventlist;
		eventlist = (eventListeners) thisNode.getProtocol(listenerPid);
		eventlist.addGroupInfoListener(obj);
	}

	/**
	 * Register DNS Service discovery response listeners.
	 *
	 * @param obj the obj
	 */
	public void registerDnsSdResponseListeners(DnsSdServiceResponseListener obj) {
		eventListeners eventlist;
		eventlist = (eventListeners) thisNode.getProtocol(listenerPid);
		eventlist.addDnsSdResponseListener(obj);
	}


	/**
	 * Register DNS Service Discovery text record listener.
	 *
	 * @param obj the obj
	 */
	public void registerDnsSdTxtRecordListener (DnsSdTxtRecordListener obj){
		eventListeners eventlist;
		eventlist = (eventListeners) thisNode.getProtocol(listenerPid);
		eventlist.addDnsSdTxtRecordListener(obj);
	}

	/**
	 * Register broadcast receiver. 
	 *
	 * @param receiver the receiver
	 */
	public void registerBroadcastReceiver (BroadcastReceiver receiver){
		eventListeners eventlist;
		eventlist = (eventListeners) thisNode.getProtocol(listenerPid);
		eventlist.addBroadcastReceiver(receiver);
	}

	/**
	 * Unregister broadcast receiver.
	 *
	 * @param receiver the receiver
	 */
	public void unRegisterBroadcastReceiver (BroadcastReceiver receiver){
		eventListeners eventlist;
		eventlist = (eventListeners) thisNode.getProtocol(listenerPid);
		eventlist.removeBroadcastReceiver(receiver);
	}

	/**
	 * Interface for callback invocation when Bonjour service discovery response
	 * is received.
	 *
	 * @see DnsSdServiceResponseEvent
	 */
	public interface DnsSdServiceResponseListener extends EventListener{

		/**
		 * The requested Bonjour service response is available.
		 * 
		 * <p>This function is invoked when the device with the specified Bonjour
		 * registration type returned the instance name.
		 *
		 * @param instanceName the instance name
		 * @param registrationType the registration type
		 * @param srcDevice the src device
		 */
		public void onDnsSdServiceAvailable(String instanceName,
				String registrationType, WifiP2pDevice srcDevice);

	}

	/**
	 * Interface for callback invocation when Bonjour TXT record is available
	 * for a service.
	 *
	 * @see DnsSdTxtRecordEvent
	 */
	public interface DnsSdTxtRecordListener extends EventListener{

		/**
		 * On dns sd txt record available.
		 *
		 * @param fullDomainName the full domain name
		 * @param record the record
		 * @param srcDevice the src device
		 */
		public void onDnsSdTxtRecordAvailable(String fullDomainName, Map<String, String> record, WifiP2pDevice srcDevice);
	}

	/**
	 * Discover peers.
	 * As Android rules suggests: the discovery will continue as long as the connection is established or the user manually stop peer discovery
	 * The Peer Discovery of Group Owner will never stop.
	 */
	public void discoverPeers() {
		nodeP2pInfo nodeInfo = (nodeP2pInfo) thisNode.getProtocol(p2pInfoPid);
		nodeInfo.setPeerDiscoveryStarted(true);
	}

	/**
	 * Stop peer discovery.
	 * @see discoverPeers()
	 */
	public void stopPeerDiscovery() {
		nodeP2pInfo nodeInfo = (nodeP2pInfo) thisNode.getProtocol(p2pInfoPid);
		nodeInfo.setPeerDiscoveryStarted(false);
		nodeInfo.setServicediscoveryStarted(false);
	}

	/**
	 * Connect.
	 * After peers have been discovered, the device can connect to another device by passing the {@link WifiP2pConfig} to this method.
	 * The Mac address of the other device are the {@link Node} ID in WiDiSi
	 *
	 * @param MacAddress the mac address
	 */
	public void connect(WifiP2pConfig config) {
		//nodeP2pInfo nodeInfo = (nodeP2pInfo) thisNode.getProtocol(p2pInfoPid);
		//nodeP2pInfo destInfo = (nodeP2pInfo) dest.getProtocol(p2pInfoPid);
		// May connect only if the destination Node is insidethe proximity of thisNode
		Node dest = null;
		String MacAddress = config.deviceAddress;
		for(int i=0; i<Network.size(); i++){
			if(Network.get(i).getID()==Long.parseLong(MacAddress)){
				dest = Network.get(i);
				break;
			}
		}
		if(dest!=null){
			boolean tempB = false;
			Linkable neighbor = (Linkable) thisNode.getProtocol(linkableId);
			for(int i=0; i<neighbor.degree(); i++){
				if (neighbor.getNeighbor(i).getID()==dest.getID()){
					tempB=true;
					break;
				}
			}
			if(thisNode.getID()!=dest.getID() && tempB){
				Transport transport4 = (Transport) thisNode.getProtocol(transportId1);
				Message message = new Message();
				message.destNode = dest;
				message.destPid = thisPid;
				message.srcNode = thisNode;
				message.srcPid = thisPid;
				message.event = "REQUEST_FOR_CONNECT";
				transport4.send(message.srcNode, message.destNode, message, message.destPid);
			}
		}
	}

	/**
	 * Cancel connect. In case if group owner, calling this method would result in group termination.
	 */
	public void cancelConnect() {
		nodeP2pInfo nodeInfo = (nodeP2pInfo) thisNode.getProtocol(p2pInfoPid);
		Transport transport5 = (Transport) thisNode.getProtocol(transportId5);

		Message message 	= new Message();
		message.srcNode 	= thisNode;
		message.srcPid 		= thisPid;
		message.destPid 	= thisPid;
		message.event 		= "REQUEST_CANCEL_CONNECT";

		// If thisNode is Group Owner this will remove the group and should inform all the clients
		if(nodeInfo.getStatus() == CONNECTED && nodeInfo.isGroupOwner()){			
			for(Node destination: nodeInfo.currentGroup.getNodeList()){
				message.destNode=destination;
				// We are using transport 5 because of couple of reasons
				// first and the most because we do not want package drop
				transport5.send(message.srcNode, message.destNode, message, message.destPid);
			}
			// if thisNode is a client just cancel the currecnt connection with the Group Owner	
		}else if(nodeInfo.getStatus() == CONNECTED && !nodeInfo.isGroupOwner() && nodeInfo.getGroupOwner()!=null){
			message.destNode=nodeInfo.getGroupOwner();
			transport5.send(message.srcNode, message.destNode, message, message.destPid);
		}

		nodeInfo.setStatus(AVAILABLE);
		nodeInfo.currentGroup.resetGroup();
		nodeInfo.setGroupOwner(null);		
	}

	/**
	 * Creates the group. 
	 */
	public void createGroup() {
		nodeP2pInfo nodeInfo = (nodeP2pInfo) thisNode.getProtocol(p2pInfoPid);
		nodeInfo.setGroupOwner(thisNode);
		nodeInfo.currentGroup.resetGroup();
		nodeInfo.currentGroup.setGroupValid(true);
		nodeInfo.currentGroup.setGroupOwner(thisNode);
		nodeInfo.currentGroup.setmInterface("Node_" + thisNode.getID() + "_AP");
		nodeInfo.currentGroup.BSSID = String.valueOf(thisNode.getID());
		nodeInfo.currentGroup.setmPassphrase(String.valueOf(CommonState.r.nextLong()));
		nodeInfo.currentGroup.setmNetId(CommonState.r.nextInt());
		nodeInfo.setStatus(CONNECTED);
	}

	/**
	 * Removes the group. The effect of removing group is the same as cancelling a connection. 
	 */
	public void removeGroup() {
		cancelConnect();
	}

	/**
	 * Adds the local service.
	 * by calling this method and passing a service to it, it will add the service to the serviceList in nodeP2pInfo class. It will return immidiately
	 *
	 * @param service the service
	 */
	public void addLocalService(wifiP2pService service) {
		service.serviceHolder = thisNode;
		nodeP2pInfo nodeInfo;
		nodeInfo = (nodeP2pInfo) thisNode.getProtocol(p2pInfoPid);
		nodeInfo.addWifiP2pService(service);
	}

	/**
	 * Removes the local service.
	 *
	 * @param service the service
	 */
	public void removeLocalService(wifiP2pService service) {
		nodeP2pInfo nodeInfo;
		nodeInfo = (nodeP2pInfo) thisNode.getProtocol(p2pInfoPid);
		nodeInfo.removeWifiP2pService(service);
	}

	/**
	 * Clear local services.
	 */
	public void clearLocalServices() {
		nodeP2pInfo nodeInfo;
		nodeInfo = (nodeP2pInfo) thisNode.getProtocol(p2pInfoPid);
		nodeInfo.clearLocalServices();
	}

	/**
	 * Discover services.
	 */
	public void discoverServices() {
		nodeP2pInfo nodeInfo = (nodeP2pInfo) thisNode.getProtocol(p2pInfoPid);
		nodeInfo.setPeerDiscoveryStarted(true);
		nodeInfo.setServicediscoveryStarted(true);
	}

	/**
	 * Request peers.
	 */
	public void requestPeers() {
		Transport transport5 = (Transport) thisNode.getProtocol(transportId5);
		Message message 	= new Message();
		message.destNode 	= thisNode;
		message.destPid 	= listenerPid;
		message.srcNode 	= thisNode;
		message.srcPid 		= thisPid;
		message.event 		= "REQUEST_PEERS";
		transport5.send(message.srcNode, message.destNode, message, message.destPid);
	}

	/**
	 * Request connection info.
	 */
	public void requestConnectionInfo() {
		Transport transport5 = (Transport) thisNode.getProtocol(transportId5);
		Message message 	= new Message();
		message.destNode 	= thisNode;
		message.destPid 	= listenerPid;
		message.srcNode 	= thisNode;
		message.srcPid 		= thisPid;
		message.event 		= "REQUEST_CONNECTION_INFO";
		transport5.send(message.srcNode, message.destNode, message, message.destPid);
	}

	/**
	 * Request group info.
	 */
	public void requestGroupInfo() {
		Transport transport5 = (Transport) thisNode.getProtocol(transportId5);
		Message message 	= new Message();
		message.destNode 	= thisNode;
		message.destPid 	= listenerPid;
		message.srcNode 	= thisNode;
		message.srcPid 		= thisPid;
		message.event 		= "GROUP_INFO_REQUEST";
		transport5.send(message.srcNode, message.destNode, message, message.destPid);
	}

	/**
	 * Sets the device name.
	 *
	 * @param name the new device name
	 */
	public void setDeviceName(String name) {
		nodeP2pInfo nodeInfo = (nodeP2pInfo) thisNode.getProtocol(p2pInfoPid);
		nodeInfo.setDeviceName(name);
	}

	/**
	 * Gets the device name.
	 *
	 * @return the device name
	 */
	public String getDeviceName() {
		nodeP2pInfo nodeInfo = (nodeP2pInfo) thisNode.getProtocol(p2pInfoPid);
		return nodeInfo.getDeviceName();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public WifiP2pManager clone(){
		WifiP2pManager wpm = null;
		try { wpm = (WifiP2pManager) super.clone(); }
		catch( CloneNotSupportedException e ) {} // never happens
		wpm.linkableId 		= linkableId;
		wpm.listenerPid 	= listenerPid;
		wpm.p2pInfoPid 		= p2pInfoPid;
		wpm.transportId0 	= transportId0;
		wpm.transportId1 	= transportId1;
		wpm.transportId2 	= transportId2;
		wpm.transportId3 	= transportId3;
		wpm.transportId4 	= transportId4;
		wpm.transportId5 	= transportId5;
		wpm.thisNode		= Network.get(0);
		wpm.thisPid			= 0;
		wpm.msgHandler		= null;
		return wpm;	
	}

	/**
	 * Send.
	 *
	 * @param cMessage the c message
	 * @param rMacAddress the r mac address
	 * @return the string
	 */
	public String send(callbackMessage cMessage, String rMacAddress){
		nodeP2pInfo senderInfo = (nodeP2pInfo) thisNode.getProtocol(p2pInfoPid);
		WifiManager senderWifiManager = (WifiManager) thisNode.getProtocol(wifimanagerpid);
		Long receiverAddress = Long.parseLong(rMacAddress);



		//find receiver node by address (nodeID)
		Node receiver = null;
		nodeP2pInfo receiverInfo = null;
		WifiManager receiverWifiManager = null;
		for(int i=0; i<Network.size(); i++){
			if(Network.get(i).getID()==receiverAddress){
				receiver = Network.get(i);
				receiverInfo = (nodeP2pInfo) receiver.getProtocol(p2pInfoPid);
				receiverWifiManager = (WifiManager) receiver.getProtocol(wifimanagerpid);
				break;
			}
		}



		if(receiver!=null && receiverInfo!=null && senderWifiManager!=null && receiverWifiManager!=null){
			if(!(senderInfo.getStatus()==CONNECTED || senderWifiManager.getWifiStatus()==CONNECTED)){
				return "This device is not connected!";
			}else if (!(receiverInfo.getStatus()==CONNECTED || receiverWifiManager.getWifiStatus()==CONNECTED)){
				return "Receiver device is not connected!";			
			}else {

				// first check if this Node is not group owner and is connected to the group owner via wifi direct interface
				if(!senderInfo.isGroupOwner() && receiverInfo.isGroupOwner() && senderInfo.getGroupOwner()!=null && receiverInfo.currentGroup!=null){
					if(senderInfo.getGroupOwner().getID()==receiver.getID() && receiverInfo.currentGroup.getNodeList().contains(thisNode)){
						Transport transport3 = (Transport) thisNode.getProtocol(transportId3);
						Message message 	= new Message();
						message.destNode 	= receiver;
						message.destPid 	= thisPid;
						message.event 		= "SOCKET_DELIVERY";
						message.object 		= cMessage;
						message.srcNode 	= thisNode;
						message.srcPid 		= thisPid;
						transport3.send(message.srcNode, message.destNode, message, message.destPid);	
						return "Message Sent!";
					}
					// if this node is group owner
				}else if (senderInfo.isGroupOwner() && !receiverInfo.isGroupOwner()){
					if(receiverInfo.getGroupOwner()!=null && senderInfo.currentGroup!=null){
						if(receiverInfo.getGroupOwner().getID()==thisNode.getID() && senderInfo.currentGroup.getNodeList().contains(receiver)){

							Transport transport3 = (Transport) thisNode.getProtocol(transportId3);
							Message message 	= new Message();
							message.destNode 	= receiver;
							message.destPid 	= thisPid;
							message.event 		= "SOCKET_DELIVERY";
							message.object 		= cMessage;
							message.srcNode 	= thisNode;
							message.srcPid 		= thisPid;
							transport3.send(message.srcNode, message.destNode, message, message.destPid);	
							return "Message Sent!";
						}
					}
					// extend the above condition to legacy wifi devices
					if(receiverWifiManager.apSSID!=null && senderInfo.currentGroup!=null){
						if(receiverWifiManager.apSSID.equals(senderInfo.currentGroup.getSSID()) && senderInfo.currentGroup.getNodeList().contains(receiver)){
							Transport transport3 = (Transport) thisNode.getProtocol(transportId3);
							Message message 	= new Message();
							message.destNode 	= receiver;
							message.destPid 	= wifimanagerpid;
							message.event 		= "SOCKET_DELIVERY";
							message.object 		= cMessage;
							message.srcNode 	= thisNode;
							message.srcPid 		= thisPid;
							transport3.send(message.srcNode, message.destNode, message, message.destPid);	
							return "Message Sent!";
						}	
					}					
				}else{
					return "Cannot send message to the destination: Destination is not inside the group";
				}
			}
		}else{
			return "Receiver is not recognized";
		}

		return "Message Not Sent";
	}

	public int getExtraSystemInfo(String extra){
		nodeP2pInfo nodeInfo = (nodeP2pInfo) thisNode.getProtocol(p2pInfoPid);
		int returnState = 60000;
		switch (extra){
		case EXTRA_WIFI_STATE:
			if(nodeInfo.isWifiP2pEnabled()){
				returnState =  WIFI_P2P_STATE_ENABLED;
			}else{
				returnState =  WIFI_P2P_STATE_DISABLED;
			}
			break;
		}
		return returnState;
	}
}