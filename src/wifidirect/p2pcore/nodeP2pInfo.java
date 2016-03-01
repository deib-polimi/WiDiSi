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

import peersim.config.Configuration;
import peersim.core.Linkable;
import peersim.core.Network;
import peersim.core.Node;
import peersim.core.Protocol;
import peersim.transport.Transport;

// TODO: Auto-generated Javadoc
/**
 * The Class nodeP2pInfo.
 */
public class nodeP2pInfo implements Protocol{

	/** A simple protocol which keeps information of a P2P Node. */
	private String deviceName;

	/**The Group owner Intention Value. between 0 to 15 - 0 means no Intention and 15 means always want to be the Group Owner*/
	private int goIntentionValue = 10; 

	/**
	 * The level of Battery on the device. The simulator would not change the battery level automatically.
	 * It should be set during runtime by a new control
	 * between 0 to 100
	 */
	private int batteryLevel = 100; 

	/**
	 * The device's processing capability. This would be set once based on the device model. It could be anything between 0 and 100. 
	 * 0 means very slow processor and 100 means very fast processor
	 * Processing Capability between 0 to 100
	 */
	private int processingCap = 100; 

	/**
	 * The current available memory remained. This would not be set by the simulator.
	 * A new control should be developed to simulate the current memory status.
	 * The number is between 0 to 100
	 */
	private int memoryRemained = 80; 

	/** List of current Services which are registered in this Device. */
	private ArrayList<wifiP2pService> wifiP2pServiceList;

	/** A boolean which shows whether Wifi P2P has been enabled or not. */
	private boolean wifiP2pEnabled;

	/** This will be set by the wifi P2P manager when the user request start Peer discovery. */
	private boolean peerDiscoveryStarted;

	/** This will be set by the wifi P2P manager when the user request start service discovery. */
	private boolean serviceDiscoveryStarted;

	/**
	 * The Pwifi p2p enabled.
	 *
	 * @hide these field keep the previous status of the mentioned signals
	 * they are necessary for edge detection
	 */
	private boolean PwifiP2pEnabled;

	/** The Ppeer discovery started. */
	private boolean PpeerDiscoveryStarted;

	/** The Pservicediscovery started. */
	private boolean PservicediscoveryStarted;	

	/** The Current group Only Group Owner keep the current group The clients ask the group owner for group information. */
	public WifiP2pGroup currentGroup;

	/**The maximum number of peers that each device may be able to descover*/
	public static final int maxNumDevices = 50;

	/** The maximum number of services that each device may be able to descover. */
	public static final int maxNumServices = 200;

	/** Connection Status. */
	public static final int CONNECTED   = 0;

	/** The Constant INVITED. */
	public static final int INVITED     = 1;

	/** The Constant FAILED. */
	public static final int FAILED      = 2;

	/** The Constant AVAILABLE. */
	public static final int AVAILABLE   = 3;

	/** The Constant UNAVAILABLE. */
	public static final int UNAVAILABLE = 4;

	/**  Device connection status. */
	private int status;

	/**
	 * The pstatus.
	 *
	 * @hide for signal edge detection
	 */
	private int pstatus;


	/** Linkable protocol *. */
	public static final String PAR_PROTOCOL = "linkable";

	/** The linkable id. */
	public int linkableId;

	/**  transport protocol for event-based simulation *. */
	public static final String PAR_TRASP_5 = "transport5";  //Internal 

	/** The Constant PAR_TRASP_2. */
	public static final String PAR_TRASP_2 = "transport2";	//Service Discovery

	/** The Constant PAR_TRASP_1. */
	public static final String PAR_TRASP_1 = "transport1";	//Peer Discovery

	/**  Transport protocol identifier for event-based simulation *. */
	public int transportId5;

	/** The transport id2. */
	public int transportId2;

	/** The transport id1. */
	public int transportId1;

	/**  eventDetection protocol for event-based simulation *. */
	public static final String PAR_LISTENER = "listeners";

	/** The listener pid. */
	public int listenerPid;

	/** thisNode is the current node which will be set by a Initializer. */
	public Node thisNode;

	/** The this pid. */
	public static int thisPid = 0;

	/** The group owner. */
	private Node groupOwner;

	/** The return state. */
	private boolean returnState;

	/** The extra wifi state. */
	public int EXTRA_WIFI_STATE;

	/** The Constant WIFI_P2P_STATE_DISABLED. */
	public static final int WIFI_P2P_STATE_DISABLED = 1;

	/** The Constant WIFI_P2P_STATE_ENABLED. */
	public static final int WIFI_P2P_STATE_ENABLED = 2;

	/** The invited by. */
	private Node invitedBy;
	
	/** The invitation time. */
	private long invitationTime;
	/**
	 * Instantiates a new node p2p info.
	 *
	 * @param prefix the prefix
	 */
	public nodeP2pInfo (String prefix){
		linkableId 				= Configuration.getPid(prefix + "." + PAR_PROTOCOL);
		transportId5 			= Configuration.getPid(prefix + "." + PAR_TRASP_5);
		transportId2 			= Configuration.getPid(prefix + "." + PAR_TRASP_2);
		transportId1 			= Configuration.getPid(prefix + "." + PAR_TRASP_1);
		listenerPid 			= Configuration.getPid(prefix + "." + PAR_LISTENER);
		currentGroup 			= new WifiP2pGroup();
		wifiP2pServiceList 		= new ArrayList<wifiP2pService>();
		invitationTime			= 0;
		invitedBy				= null;
		EXTRA_WIFI_STATE		= 2;
		returnState				= false;
		groupOwner				= null;
		thisNode				= Network.get(0);
		pstatus					= AVAILABLE;
		status					= AVAILABLE;
		PservicediscoveryStarted= false;
		PpeerDiscoveryStarted 	= false;
		PwifiP2pEnabled			= true;
		wifiP2pEnabled			= true;
		peerDiscoveryStarted	= false;
		serviceDiscoveryStarted = false;
		
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final int maxLen = 10;
		return "nodeP2pInfo [deviceName="
		+ deviceName
		+ ", goIntentionValue="
		+ goIntentionValue
		+ ", batteryLevel="
		+ batteryLevel
		+ ", processingCap="
		+ processingCap
		+ ", memoryRemained="
		+ memoryRemained
		+ ", wifiP2pServiceList="
		+ (wifiP2pServiceList != null ? wifiP2pServiceList.subList(0,
				Math.min(wifiP2pServiceList.size(), maxLen)) : null)
				+ ", wifiP2pEnabled=" + wifiP2pEnabled
				+ ", peerDiscoveryStarted=" + peerDiscoveryStarted
				+ ", servicediscoveryStarted=" + serviceDiscoveryStarted
				+ ", currentGroup=" + currentGroup + ", groupLimit="
				+ getGroupLimit() + ", status=" + status + ", isGroupOwner="
				+ isGroupOwner() + "]";
	}

	/**
	 * Gets the go intention value.
	 *
	 * @return the go intention value
	 */
	public int getGoIntentionValue() {
		return goIntentionValue;
	}

	/**
	 * Sets the go intention value.
	 *
	 * @param goIntentionValue the new go intention value
	 */
	public void setGoIntentionValue(int goIntentionValue) {
		this.goIntentionValue = goIntentionValue;
	}

	/**
	 * Gets the battery level.
	 *
	 * @return the battery level
	 */
	public int getBatteryLevel() {
		return batteryLevel;
	}

	/**
	 * Sets the battery level.
	 *
	 * @param batteryLevel the new battery level
	 */
	public void setBatteryLevel(int batteryLevel) {
		this.batteryLevel = batteryLevel;
	}

	/**
	 * Gets the processing cap.
	 *
	 * @return the processing cap
	 */
	public int getProcessingCap() {
		return processingCap;
	}

	/**
	 * Sets the processing cap.
	 *
	 * @param processingCap the new processing cap
	 */
	public void setProcessingCap(int processingCap) {
		this.processingCap = processingCap;
	}

	/**
	 * Gets the memory remained.
	 *
	 * @return the memory remained
	 */
	public int getMemoryRemained() {
		return memoryRemained;
	}

	/**
	 * Sets the memory remained.
	 *
	 * @param memoryRemained the new memory remained
	 */
	public void setMemoryRemained(int memoryRemained) {
		this.memoryRemained = memoryRemained;
	}

	/**
	 * Gets the wifi p2p service list.
	 *
	 * @return the wifi p2p service list
	 */
	public ArrayList<wifiP2pService> getWifiP2pServiceList() {
		return wifiP2pServiceList;
	}

	/**
	 * Gets the mac address.
	 *
	 * @return the mac address
	 * 	P2p MAC Address. in our design this is the Node ID since it is unique in our world
	 */
	public String getMacAddress() {
		return String.valueOf(thisNode.getID());
	}

	/**
	 * Checks if is wifi p2p enabled.
	 *
	 * @return true, if is wifi p2p enabled
	 */
	public boolean isWifiP2pEnabled() {
		return wifiP2pEnabled;
	}

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * Gets the invitation time.
	 *
	 * @return the invitation time
	 */
	public long getInvitationTime() {
		return invitationTime;
	}

	/**
	 * Sets the invitation time.
	 *
	 * @param invitationTime the new invitation time
	 */
	public void setInvitationTime(long invitationTime) {
		this.invitationTime = invitationTime;
	}

	/**
	 * Gets the invited by.
	 *
	 * @return the invited by
	 */
	public Node getInvitedBy() {
		return invitedBy;
	}

	/**
	 * Sets the invited by.
	 *
	 * @param invitedBy the new invited by
	 */
	public void setInvitedBy(Node invitedBy) {
		this.invitedBy = invitedBy;
	}

	/**
	 * Checks if is peer discovery started.
	 *
	 * @return true, if is peer discovery started
	 */
	public boolean isPeerDiscoveryStarted() {
		return peerDiscoveryStarted;
	}

	/**
	 * Checks if is service discovery started.
	 *
	 * @return true, if is service discovery started
	 */
	public boolean isServiceDiscoveryStarted() {
		return serviceDiscoveryStarted;
	}

	/**
	 * Gets the device name.
	 *
	 * @return the device name
	 */
	public String getDeviceName() {
		return deviceName;
	}

	/**
	 * Gets the group limit.
	 *
	 * @return the group limit
	 */
	public int getGroupLimit() {
		if(currentGroup!=null){
			return WifiP2pGroup.groupCapacity;
		}
		else{
			return 0;
		}
	}

	/**
	 * whenever a @param service is added to this device It will inform event listener about the creation of a new  @param service
	 * eventListener will take care of generating necessary broadcasts for the necessary components. The necessary delays should be implemented
	 * at this stage
	 *
	 * @param service the service
	 * @return true, if successful
	 */
	public boolean addWifiP2pService(wifiP2pService service) {
		// should not add two identical service to the wifiP2pServiceList
		if(wifiP2pServiceList.contains(service)){
			returnState = false;
		}else if((thisNode!=null) && thisNode.isUp()){

			wifiP2pServiceList.add(service);
			Transport transport2 = (Transport) thisNode.getProtocol(transportId2); // service discovery
			Transport transport5 = (Transport) thisNode.getProtocol(transportId5); // internal delay

			// Here we want to send the notification of changes to all neighbors as well
			// we are using the Service Discovery Transport method for this reason
			// Sending notification to neighbors will be performed by means of transport for service discovery

			if(wifiP2pEnabled && (serviceDiscoveryStarted || peerDiscoveryStarted)){
				Linkable neighbors = (Linkable) thisNode.getProtocol(linkableId);
				Message message = new Message();
				message.srcNode = thisNode;
				message.srcPid = thisPid;
				message.destPid = listenerPid;
				message.event = "onBonjourServiceAvailable";
				message.object = service;
				for (int i=0; i<neighbors.degree(); i++){
					message.destNode = neighbors.getNeighbor(i);		
					transport2.send(message.srcNode, message.destNode, message, message.destPid);			
				}
			}

			// notification for itself - WIFI_P2P_THIS_DEVICE_CHANGED_ACTION
			Message message = new Message();
			message.srcNode = thisNode;
			message.destNode = thisNode;
			message.srcPid = thisPid;
			message.destPid = listenerPid;
			message.event = "WIFI_P2P_THIS_DEVICE_CHANGED_ACTION";
			message.object = service;
			transport5.send(message.srcNode, message.destNode, message, message.destPid);
			returnState= true;
		}
		return returnState;
	}

	/**
	 * Removes the wifi p2p service.
	 *
	 * @param service the service
	 * @return true, if successful
	 */
	public boolean removeWifiP2pService(wifiP2pService service) {
		if (!wifiP2pServiceList.contains(service)){
			returnState = false;
		}else if(thisNode!=null && thisNode.isUp()){
			wifiP2pServiceList.remove(service);
			// Here we do not need to inform other proximal peers about this change since it is not adding a service

			if(wifiP2pEnabled){
				Message message = new Message();
				message.srcNode = thisNode;
				message.destNode = thisNode;
				message.srcPid = thisPid;
				message.destPid = listenerPid;
				message.event = "WIFI_P2P_THIS_DEVICE_CHANGED_ACTION";
				message.object = service;
				// notification for itself
				Transport transport5 = (Transport) thisNode.getProtocol(transportId5);
				transport5.send(message.srcNode, message.destNode, message, message.destPid);
				// ///////////////////////////////////////////////////			
			} 
			returnState=true;
		}
		return returnState;
	}

	/**
	 * Clear local services.
	 *
	 * @return true, if successful
	 */
	public boolean clearLocalServices() {
		returnState=false;
		if (!wifiP2pServiceList.isEmpty() && thisNode != null && thisNode.isUp()){
			wifiP2pServiceList.clear();
			if(wifiP2pEnabled){
				// notification for itself
				Message message = new Message();
				message.srcNode = thisNode;
				message.destNode = thisNode;
				message.srcPid = thisPid;
				message.destPid = listenerPid;
				message.event = "WIFI_P2P_THIS_DEVICE_CHANGED_ACTION";
				message.object = null;
				Transport transport5 = (Transport) thisNode.getProtocol(transportId5);
				transport5.send(message.srcNode, message.destNode, message, message.destPid);
				returnState=true;
			}		
		}
		return returnState;
	}

	/**
	 *  enabling WiFi P2P will initaite "WIFI_P2P_STATE_CHANGED_ACTION" event.
	 *
	 * @param wifiP2pEnabled the wifi p2p enabled
	 * @return true, if successful
	 */
	public boolean setWifiP2pEnabled(boolean wifiP2pEnabled) {
		returnState=false;
		this.wifiP2pEnabled = wifiP2pEnabled;
		if(PwifiP2pEnabled!=wifiP2pEnabled){
			PwifiP2pEnabled = wifiP2pEnabled;
			EXTRA_WIFI_STATE = (wifiP2pEnabled)?  WIFI_P2P_STATE_ENABLED : WIFI_P2P_STATE_DISABLED;
			if(thisNode != null && thisNode.isUp()){
				Message message = new Message();
				message.srcNode = thisNode;
				message.destNode = thisNode;
				message.srcPid = thisPid;
				message.destPid = listenerPid;
				message.event = "WIFI_P2P_STATE_CHANGED_ACTION";
				message.object = null;
				Transport transport5 = (Transport) thisNode.getProtocol(transportId5);
				transport5.send(message.srcNode, message.destNode, message, message.destPid);
				returnState=true;
			}
		}
		return returnState;
	}

	/**
	 * 	public static final int CONNECTED   = 0;
	 * 	public static final int INVITED     = 1;
	 * 	public static final int FAILED      = 2;
	 * 	public static final int AVAILABLE   = 3;
	 * 	public static final int UNAVAILABLE = 4;.
	 *
	 * @param status the status
	 * @return true, if successful
	 */
	public boolean setStatus(int status) {
		returnState=false;
		this.status = status;
		if(pstatus!=status){
			// we should only send "WIFI_P2P_CONNECTION_CHANGED_ACTION" only if the device get connected or disconnected 
			if(wifiP2pEnabled && thisNode!=null && thisNode.isUp() && 
					((status==CONNECTED && pstatus!=CONNECTED) || (status!=CONNECTED && pstatus==CONNECTED))){
				Message message = new Message();
				message.destNode = thisNode;
				message.destPid = listenerPid;
				message.srcNode = thisNode;
				message.srcPid = thisPid;
				message.event = "WIFI_P2P_CONNECTION_CHANGED_ACTION";
				Transport transport5 = (Transport) thisNode.getProtocol(transportId5);
				transport5.send(message.srcNode, message.destNode, message, message.destPid);
				returnState=true;
			}
			pstatus = status;
		}
		return returnState;	
	}

	/**
	 * By starting Peerdiscovery or service discovery from the manager class this method will be called.
	 * Seting this method to true will start peerdiscovery.
	 * This means this device should inform all listeners to WIFI_P2P_PEERS_CHANGED_ACTION if there is a "valid" peer around
	 * moreOver it should also inform all proximal devices which are looking for service discovery about the current services in this device by 
	 * sending the service and  onBonjourServiceAvailable to their listeners
	 * Moreover, starting peerdiscovery means receiving the list of proximal devices (which has started discovery)
	 * for this reason we only need to send Peers_Chang_Action event to this listener if there is any active peers around
	 *
	 * @param peerDiscoveryStarted the peer discovery started
	 * @return true, if successful
	 */
	public boolean setPeerDiscoveryStarted(boolean peerDiscoveryStarted) {
		// WifiP2p should be enabled before starting peerdiscovery
		returnState = false;
		if(wifiP2pEnabled){
			this.peerDiscoveryStarted = peerDiscoveryStarted;
			if(PpeerDiscoveryStarted != peerDiscoveryStarted){
				PpeerDiscoveryStarted = peerDiscoveryStarted;
				if(peerDiscoveryStarted && 	thisNode!=null && thisNode.isUp()){

					Linkable neighbors = (Linkable) thisNode.getProtocol(linkableId);
					Transport transport1 = (Transport) thisNode.getProtocol(transportId1);
					Transport transport2 = (Transport) thisNode.getProtocol(transportId2);
					int activePeerCount = 0;
					Message message = new Message();
					message.destPid = listenerPid;
					message.srcNode = thisNode;
					message.srcPid = thisPid;
					int tempMin = (neighbors.degree() <= nodeP2pInfo.maxNumDevices)? neighbors.degree(): nodeP2pInfo.maxNumDevices;
					for (int i=0; i<tempMin; i++){

						nodeP2pInfo neighborInfo = (nodeP2pInfo) neighbors.getNeighbor(i).getProtocol(thisPid);

						// inform only thoes devices in the proximity which has started peerDiscovery
						// note that any service discovery will set peerDiscovery as well
						if(neighborInfo.isPeerDiscoveryStarted()){
							message.destNode = neighbors.getNeighbor(i);
							message.event = "WIFI_P2P_PEERS_CHANGED_ACTION";
							transport1.send(message.srcNode, message.destNode, message, message.destPid);

							// If other proximal devices also looking for services as well and this device has service then
							// send thisNodes services to them one by one
							// this means pushing your services to the interested outside listeners
							if(neighborInfo.isServiceDiscoveryStarted() && !getWifiP2pServiceList().isEmpty()){
								for(wifiP2pService tempService: getWifiP2pServiceList()){
									message.object = tempService;
									message.event = "onBonjourServiceAvailable";
									transport2.send(message.srcNode, message.destNode, message, message.destPid);								
								}
							}
						}

						// also inform your listener if there is active peer around
						if(neighborInfo.isPeerDiscoveryStarted()){
							activePeerCount++;
						}
					}
					if(activePeerCount>0){
						message.destNode = thisNode;
						message.event = "WIFI_P2P_PEERS_CHANGED_ACTION";
						message.object = null;
						transport1.send(message.srcNode, message.destNode, message, message.destPid);
					}
					returnState = true;
				}
			}
		}else{
			returnState = false;
		}
		return returnState;
	}

	/**
	 * By starting Service Discovery from manager class this method will be called
	 * By starting service discovery we are sure that peerdiscovery has already been started and we have informed proximal peers (who are looking for services)
	 * about our current services. Now it is the time to receive their services since we also started service discovery which means we need to see other services as well
	 * This is pulling their services
	 *
	 * @param serviceDiscoveryStarted the service discovery started
	 * @return true, if successful
	 */
	public boolean setServicediscoveryStarted(boolean serviceDiscoveryStarted) {
		returnState = false;
		if(wifiP2pEnabled){
			this.serviceDiscoveryStarted = serviceDiscoveryStarted;
			if(PservicediscoveryStarted!= serviceDiscoveryStarted){
				PservicediscoveryStarted = serviceDiscoveryStarted;
				if(serviceDiscoveryStarted && peerDiscoveryStarted && thisNode!=null && thisNode.isUp()){
					Linkable neighbors = (Linkable) thisNode.getProtocol(linkableId);
					Transport transport2 = (Transport) thisNode.getProtocol(transportId2);
					Message message = new Message();
					message.destPid = listenerPid;
					message.srcNode = thisNode;
					message.srcPid = thisPid;
					message.destNode = thisNode;
					message.event = "onBonjourServiceAvailable";
					int tempMin = (neighbors.degree() <= nodeP2pInfo.maxNumDevices)? neighbors.degree(): nodeP2pInfo.maxNumDevices;

					for (int i=0; i<tempMin; i++){

						nodeP2pInfo neighborInfo = (nodeP2pInfo) neighbors.getNeighbor(i).getProtocol(thisPid);
						// in this level we also need to search for proximal servicesand send onBonjourServiceAvailable to thisNode
						// note that any service discovery will set peerDiscovery as well but not vice versa
						if(neighborInfo.isWifiP2pEnabled() && (neighborInfo.isPeerDiscoveryStarted() || neighborInfo.isServiceDiscoveryStarted()) 
								&& !neighborInfo.getWifiP2pServiceList().isEmpty()){
							for(wifiP2pService tempService: neighborInfo.getWifiP2pServiceList()){
								message.object = tempService;
								transport2.send(message.srcNode, message.destNode, message, message.destPid);
							}
						}
					}
					returnState = true;
				}
			}
		}else{
			returnState = false;
		}
		return returnState;
	}

	/**
	 * Sets the device name.
	 *
	 * @param deviceName the device name
	 * @return true, if successful
	 */
	public boolean setDeviceName(String deviceName) {
		returnState = false;
		this.deviceName = deviceName;
		if(wifiP2pEnabled && thisNode!=null && thisNode.isUp()){
			Transport transport5 = (Transport) thisNode.getProtocol(transportId5);
			Message message = new Message();
			message.destNode =thisNode;
			message.destPid = listenerPid;
			message.srcNode = thisNode;
			message.srcPid = thisPid;
			message.event = "WIFI_P2P_THIS_DEVICE_CHANGED_ACTION";
			message.object = null;
			transport5.send(message.srcNode, message.destNode, message, message.destPid);
			returnState = true;
		}
		return returnState;
	}

	/**
	 * Gets the group owner.
	 *
	 * @return the group owner
	 */
	public Node getGroupOwner() {
		return groupOwner;
	}

	/**
	 * Sets the group owner.
	 *
	 * @param groupOwner the new group owner
	 */
	public void setGroupOwner(Node groupOwner) {
		this.groupOwner = groupOwner;
	}

	/**
	 * Checks if is group owner.
	 *
	 * @return true, if is group owner
	 */
	public boolean isGroupOwner() {
		if(thisNode!=null && groupOwner!=null){
			return (thisNode.getID()==groupOwner.getID());
		}else{
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public nodeP2pInfo clone(){
		nodeP2pInfo npf = null;
		try { npf = (nodeP2pInfo) super.clone(); }
		catch( CloneNotSupportedException e ) {} // never happens
		npf.transportId2 = transportId2;
		npf.transportId5 = transportId5;
		npf.transportId1 = transportId1;
		npf.linkableId = linkableId;
		npf.listenerPid = listenerPid;
		npf.currentGroup = new WifiP2pGroup();
		npf.wifiP2pServiceList = new ArrayList<wifiP2pService>();
		npf.invitationTime = 0;
		npf.invitedBy = null;
		npf.EXTRA_WIFI_STATE = 2;
		npf.returnState = false;
		npf.groupOwner = null;
		npf.thisNode = Network.get(0);
		npf.pstatus = AVAILABLE;
		npf.status = AVAILABLE;
		npf.PservicediscoveryStarted = false;
		npf.PpeerDiscoveryStarted = false;
		npf.PwifiP2pEnabled = true;
		npf.wifiP2pEnabled = true;
		npf.peerDiscoveryStarted = false;
		npf.serviceDiscoveryStarted = false;		
		return npf;	
	}

/*	//Randomely generate a Mac address
	*//**
	 * Random mac address.
	 *
	 * @return the string
	 *//*
	private String randomMACAddress(){
		Random rand = new Random();
		byte[] macAddr = new byte[6];
		rand.nextBytes(macAddr);

		macAddr[0] = (byte)(macAddr[0] & (byte)254);  //zeroing last 2 bytes to make it unicast and locally adminstrated

		StringBuilder sb = new StringBuilder(18);
		for(byte b : macAddr){

			if(sb.length() > 0)
				sb.append(":");

			sb.append(String.format("%02x", b));
		}
		return sb.toString();
	}*/
}
