/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Modified by: Naser Derakhshan
 * Politecnico di Milano
 */

package wifi;
import java.util.ArrayList;
import java.util.List;

import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.core.Linkable;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDProtocol;
import peersim.transport.Transport;
import wifidirect.macphy.LowLayerModel;
import wifidirect.p2pcore.Callback;
import wifidirect.p2pcore.Message;
import wifidirect.p2pcore.callbackMessage;
import wifidirect.p2pcore.nodeP2pInfo;

/**
 * This class provides the primary API for managing all aspects of Wi-Fi
 * connectivity. Get an instance of this class by calling
 * {@link android.content.Context#getSystemService(String) Context.getSystemService(Context.WIFI_SERVICE)}.

 * It deals with several categories of items:
 * <ul>
 * <li>The list of configured networks. The list can be viewed and updated,
 * and attributes of individual entries can be modified.</li>
 * <li>The currently active Wi-Fi network, if any. Connectivity can be
 * established or torn down, and dynamic information about the state of
 * the network can be queried.</li>
 * <li>Results of access point scans, containing enough information to
 * make decisions about what access point to connect to.</li>
 * <li>It defines the names of various Intent actions that are broadcast
 * upon any sort of change in Wi-Fi state.
 * </ul>
 * This is the API to use when performing Wi-Fi specific operations. To
 * perform operations that pertain to network connectivity at an abstract
 * level, use {@link android.net.ConnectivityManager}.
 */
public class WifiManager implements EDProtocol, CDProtocol{

	private int 	linkableId;
	private int 	p2pInfoPid;
	private int 	llmodelPid;
	private int 	listenerPid;
	private int 	transportId1;
	private int		p2pmanagerId;
	private int 	transportId4;
	private int 	transportId5;

	private static final int CONNECTED   = 0;
	private static final int AVAILABLE   = 3;

	public List<ScanResult> ScanResultList;

	/*
	 * This field indicates whether the WiFi scan has been initiated or not
	 * This class check for changes in Wifi APs in the proximity if this field is true
	 */
	public 	boolean 	WifiScanEnable;
	private boolean 	wifiEnabled;// indicates whether the wifi is enabled or disabled
	private long 		cycle;
	public String 		apSSID;
	public String 		BSSID;
	private Node 		thisNode;
	private int 		thisPid;
	private int 		wifiStatus;

	/** The msg handler. */
	private Callback msgHandler;

	public WifiManager(String prefix) {
		linkableId 	 	= Configuration.getPid(prefix + "." + "linkable");
		p2pInfoPid 	 	= Configuration.getPid(prefix + "." + "p2pinfo");
		llmodelPid 	 	= Configuration.getPid(prefix + "." + "llmodel");
		listenerPid  	= Configuration.getPid(prefix + "." + "listeners");
		transportId1	= Configuration.getPid(prefix + "." + "transport1");
		transportId4 	= Configuration.getPid(prefix + "." + "transport4");
		transportId5 	= Configuration.getPid(prefix + "." + "transport5");
		p2pmanagerId 	= Configuration.getPid(prefix + "." + "p2pmanager");
		ScanResultList 	= new ArrayList<ScanResult>();
		WifiScanEnable 	= false;
		wifiEnabled 	= true;
		cycle 			= 0;
		apSSID			= null;
		BSSID 			= null;
		thisNode 		= Network.get(0);
		thisPid 		= 0;
		wifiStatus		= 3;
		msgHandler 	 	= null;

	}

	@Override
	public void nextCycle(Node node, int pid) {
		thisNode = node;
		thisPid = pid;
		if(WifiScanEnable && cycle%20==0){
			Transport transport1 = (Transport) node.getProtocol(transportId1);
			ScanResultList.clear();
			Linkable neighbor = (Linkable) node.getProtocol(linkableId);
			LowLayerModel llmodel = (LowLayerModel) node.getProtocol(llmodelPid);
			for(int i=0; i<neighbor.degree(); i++){
				nodeP2pInfo neighborInfo = (nodeP2pInfo) neighbor.getNeighbor(i).getProtocol(p2pInfoPid);
				if (neighborInfo.isGroupOwner() && neighborInfo.getStatus()==CONNECTED){
					ScanResult accessPoint = new ScanResult();
					accessPoint.BSSID = neighborInfo.getMacAddress();
					accessPoint.frequency = 2400;
					accessPoint.SSID = neighborInfo.currentGroup.getSSID();
					accessPoint.timestamp = System.currentTimeMillis();					
					accessPoint.level = llmodel.getRSSIdbm(node, neighbor.getNeighbor(i));   // RSSI in dbm
					ScanResultList.add(accessPoint);
					//Visualizer.print("AP: " + accessPoint.SSID + " added to the list of: " + thisNode.getID());
				}
			}
			// now we have to check if any new thing is added to the scan list
			if(!ScanResultList.isEmpty()){
				// A new AP has been detected, infor the eventListeners to broadcst it
				// this action should be performned for each newly found AP
				Message newMessage 	= new Message();
				newMessage.destNode = node;
				newMessage.destPid 	= listenerPid;
				newMessage.srcNode 	= node;
				newMessage.srcPid 	= pid;
				newMessage.event 	= "SCAN_RESULTS_AVAILABLE_ACTION";
				newMessage.object 	= ScanResultList;
				transport1.send(newMessage.srcNode, newMessage.destNode, newMessage, newMessage.destPid);					
			}
		}
		cycle++;
	}

	@Override
	public void processEvent(Node node, int pid, Object event) {
		Message 	message 	= (Message) 	event;
		nodeP2pInfo senderInfo 	= (nodeP2pInfo) message.srcNode.getProtocol(p2pInfoPid);

		switch (message.event){
		case "CONNECTION_REQUEST_ACCEPTED":	
			apSSID = senderInfo.currentGroup.getSSID();
			BSSID  = senderInfo.currentGroup.BSSID;
			setWifiStatus(CONNECTED);
			break;

		case "REQUEST_CANCEL_CONNECT":
			setWifiStatus(AVAILABLE);
			apSSID = null;
			BSSID = null;
			break;

		case "SOCKET_DELIVERY":
			if(apSSID!=null && senderInfo.isGroupOwner() && senderInfo.currentGroup !=null){
				if(senderInfo.currentGroup.getSSID().equals(apSSID) && senderInfo.currentGroup.getNodeList().contains(node)){

					callbackMessage cMessage = (callbackMessage) message.object;
					if(msgHandler!=null){

						msgHandler.handleMessage(cMessage);
					}
				}
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

	public WifiManager clone(){
		WifiManager wfm = null;
		try { wfm = (WifiManager) super.clone(); }
		catch( CloneNotSupportedException e ) {} // never happens
		wfm.linkableId = linkableId;
		wfm.p2pInfoPid = p2pInfoPid;
		wfm.llmodelPid = llmodelPid;
		wfm.listenerPid = listenerPid;
		wfm.transportId1 = transportId1;
		wfm.p2pmanagerId = p2pmanagerId;
		wfm.transportId4 = transportId4;
		wfm.transportId5 = transportId5;
		wfm.ScanResultList = new ArrayList<ScanResult>();
		wfm.WifiScanEnable = false;
		wfm.wifiEnabled = true;
		wfm.cycle = 0;
		wfm.apSSID = null;
		wfm.BSSID  = null;
		wfm.thisNode = Network.get(0);
		wfm.thisPid = 0;
		wfm.wifiStatus = 3;
		wfm.msgHandler = null;
		return wfm;
	}


	/**
	 * Request a scan for access points. Returns immediately. The availability
	 * of the results is made known later by means of an asynchronous event sent
	 * on completion of the scan.
	 * @return {@code true} if the operation succeeded, i.e., the scan was initiated
	 */
	public boolean startScan() {
		// Here we will continiuosly check for other APs around.
		// another Control Unit check this boolean field in each round and if it is enabled will update the list of APs
		WifiScanEnable = true;
		return WifiScanEnable;
	}

	/**
	 * Return the results of the latest access point scan.
	 * @return the list of access points found in the most recent scan.
	 */
	public List<ScanResult> getScanResults() {
		return ScanResultList;
	}

	/**
	 * Return the DHCP-assigned addresses from the last successful DHCP request,
	 * if any.
	 * @return the DHCP information
	 */
	public DhcpInfo getDhcpInfo() {
		DhcpInfo newDhcpInfo = new DhcpInfo();
		// this object should be completed later 
		// for now we do not need it since we only want to connect and we do not want to communicate
		// later we will complete this
		return newDhcpInfo;
	}

	/**
	 * Enable or disable Wi-Fi.
	 * @param enabled {@code true} to enable, {@code false} to disable.
	 * @return {@code true} if the operation succeeds (or if the existing state
	 *         is the same as the requested state).
	 */
	public boolean setWifiEnabled(boolean enabled) {
		wifiEnabled = enabled;
		return true;
	}

	public void connect(WifiConfiguration config) {
		if(getWifiStatus()==CONNECTED){
			return;
		}
		Node destNode= null;
		Linkable neighbor = (Linkable) thisNode.getProtocol(linkableId);
		for(int i=0; i<neighbor.degree(); i++){
			nodeP2pInfo neighborInfo = (nodeP2pInfo) neighbor.getNeighbor(i).getProtocol(p2pInfoPid);
			if(neighborInfo.currentGroup!=null && neighborInfo.currentGroup.isGroupValid()){
				if(neighborInfo.currentGroup.getSSID().equals(config.SSID)){
					destNode = neighbor.getNeighbor(i);
					break;
				}
			}
		}
		if(destNode!=null){
			Transport transport4 = (Transport) thisNode.getProtocol(transportId4);
			Message newMessage 	= new Message();
			newMessage.destNode = destNode;
			newMessage.destPid 	= p2pmanagerId;
			newMessage.srcNode 	= thisNode;
			newMessage.srcPid 	= thisPid;
			newMessage.event 	= "REQUEST_WIFI_CONNECT";
			transport4.send(newMessage.srcNode, newMessage.destNode, newMessage, newMessage.destPid);
		}
	}

	public void cancelConnect(){
		if(getWifiStatus()==CONNECTED){
			Node destNode= null;;
			for(int i=0; i<Network.size(); i++){
				nodeP2pInfo neighborInfo = (nodeP2pInfo) Network.get(i).getProtocol(p2pInfoPid);
				if(neighborInfo.currentGroup.getSSID()!=null && neighborInfo.currentGroup.getSSID().equals(apSSID)){
					destNode = Network.get(i);
					break;
				}
			}
			if(destNode!=null){
				Transport transport5 = (Transport) thisNode.getProtocol(transportId5);
				Message newMessage 	= new Message();
				newMessage.destNode = destNode;
				newMessage.destPid 	= p2pmanagerId;
				newMessage.srcNode 	= thisNode;
				newMessage.srcPid 	= thisPid;
				newMessage.event 	= "REQUEST_CANCEL_CONNECT";
				transport5.send(newMessage.srcNode, newMessage.destNode, newMessage, newMessage.destPid);
			}
		}
		setWifiStatus(AVAILABLE);
		apSSID = null;
		BSSID  = null;
	}

	/**
	 * Sending a packet to the other nodes // this method is PeerSim dependant and should
	 * be overided by Socket delivery in real devices
	 * 
	 * @param cMessage
	 * @param rMacAddress
	 * @return
	 */
	public String send(callbackMessage cMessage, String BSSID){
		if(getWifiStatus()!=CONNECTED || !this.BSSID.equals(BSSID)){
			return "Not Connected!";
		}

		//find receiver node by BSSID
		Node receiver = null;
		nodeP2pInfo receiverInfo = null;
		for(int i=0; i<Network.size(); i++){
			receiverInfo = (nodeP2pInfo) Network.get(i).getProtocol(p2pInfoPid);
			if(receiverInfo.isGroupOwner() && receiverInfo.currentGroup!=null){
				if (receiverInfo.currentGroup.BSSID.equals(BSSID)){
					receiver = Network.get(i);
					break;
				}
			}
		}
		if(receiver!=null){
			Transport transport4 = (Transport) thisNode.getProtocol(transportId4);
			Message newMessage 	= new Message();
			newMessage.destNode = receiver;
			newMessage.destPid 	= p2pmanagerId;
			newMessage.srcNode 	= thisNode;
			newMessage.srcPid 	= thisPid;
			newMessage.object 	= cMessage;
			newMessage.event 	= "SOCKET_DELIVERY";
			transport4.send(newMessage.srcNode, newMessage.destNode, newMessage, newMessage.destPid);
			return "The nessage sent!";
		}
		return "Nothing Sent!";
	}

	/**
	 * Return whether Wi-Fi is enabled or disabled.
	 * @return {@code true} if Wi-Fi is enabled
	 * @see #getWifiState()
	 */
	public boolean isWifiEnabled() {
		// return getWifiState() == WIFI_STATE_ENABLED;
		return isWifiEnabled();
	}
	/**
	 * Calculates the level of the signal. This should be used any time a signal
	 * is being shown.
	 *
	 * @param rssi The power of the signal measured in RSSI.
	 * @param numLevels The number of levels to consider in the calculated
	 *            level.
	 * @return A level of the signal, given in the range of 0 to numLevels-1
	 *         (both inclusive).
	 */
	public static int calculateSignalLevel(int rssi, int numLevels) {
		if (rssi <= MIN_RSSI) {
			return 0;
		} else if (rssi >= MAX_RSSI) {
			return numLevels - 1;
		} else {
			float inputRange = (MAX_RSSI - MIN_RSSI);
			float outputRange = (numLevels - 1);
			return (int)((float)(rssi - MIN_RSSI) * outputRange / inputRange);
		}
	}

	/**
	 * Compares two signal strengths.
	 *
	 * @param rssiA The power of the first signal measured in RSSI.
	 * @param rssiB The power of the second signal measured in RSSI.
	 * @return Returns <0 if the first signal is weaker than the second signal,
	 *         0 if the two signals have the same strength, and >0 if the first
	 *         signal is stronger than the second signal.
	 */
	public static int compareSignalLevel(int rssiA, int rssiB) {
		return rssiA - rssiB;
	}

	/**
	 * Return whether Wi-Fi AP is enabled or disabled.
	 * @return {@code true} if Wi-Fi AP is enabled
	 * @see #getWifiApState()
	 *
	 * @hide Dont open yet
	 */
	public boolean isWifiApEnabled() {
		if(wifiEnabled){
			return true;
		}else{
			return false;
		}
	}

	// Supplicant error codes:
	/**
	 * The error code if there was a problem authenticating.
	 */
	public static final int ERROR_AUTHENTICATING = 1;

	/**
	 * Broadcast intent action indicating whether Wi-Fi scanning is allowed currently
	 * @hide
	 */
	public static final String WIFI_SCAN_AVAILABLE = "wifi_scan_available";

	/**
	 * Extra int indicating scan availability, WIFI_STATE_ENABLED and WIFI_STATE_DISABLED
	 * @hide
	 */
	public static final String EXTRA_SCAN_AVAILABLE = "scan_enabled";

	/**
	 * Broadcast intent action indicating that Wi-Fi has been enabled, disabled,
	 * enabling, disabling, or unknown. One extra provides this state as an int.
	 * Another extra provides the previous state, if available.
	 *
	 * @see #EXTRA_WIFI_STATE
	 * @see #EXTRA_PREVIOUS_WIFI_STATE
	 */

	public static final String WIFI_STATE_CHANGED_ACTION =
			"android.net.wifi.WIFI_STATE_CHANGED";
	/**
	 * The lookup key for an int that indicates whether Wi-Fi is enabled,
	 * disabled, enabling, disabling, or unknown.  Retrieve it with
	 * {@link android.content.Intent#getIntExtra(String,int)}.
	 *
	 * @see #WIFI_STATE_DISABLED
	 * @see #WIFI_STATE_DISABLING
	 * @see #WIFI_STATE_ENABLED
	 * @see #WIFI_STATE_ENABLING
	 * @see #WIFI_STATE_UNKNOWN
	 */
	public static final String EXTRA_WIFI_STATE = "wifi_state";
	/**
	 * The previous Wi-Fi state.
	 *
	 * @see #EXTRA_WIFI_STATE
	 */
	public static final String EXTRA_PREVIOUS_WIFI_STATE = "previous_wifi_state";

	/**
	 * Wi-Fi is currently being disabled. The state will change to {@link #WIFI_STATE_DISABLED} if
	 * it finishes successfully.
	 *
	 * @see #WIFI_STATE_CHANGED_ACTION
	 * @see #getWifiState()
	 */
	public static final int WIFI_STATE_DISABLING = 0;
	/**
	 * Wi-Fi is disabled.
	 *
	 * @see #WIFI_STATE_CHANGED_ACTION
	 * @see #getWifiState()
	 */
	public static final int WIFI_STATE_DISABLED = 1;
	/**
	 * Wi-Fi is currently being enabled. The state will change to {@link #WIFI_STATE_ENABLED} if
	 * it finishes successfully.
	 *
	 * @see #WIFI_STATE_CHANGED_ACTION
	 * @see #getWifiState()
	 */
	public static final int WIFI_STATE_ENABLING = 2;
	/**
	 * Wi-Fi is enabled.
	 *
	 * @see #WIFI_STATE_CHANGED_ACTION
	 * @see #getWifiState()
	 */
	public static final int WIFI_STATE_ENABLED = 3;
	/**
	 * Wi-Fi is in an unknown state. This state will occur when an error happens while enabling
	 * or disabling.
	 *
	 * @see #WIFI_STATE_CHANGED_ACTION
	 * @see #getWifiState()
	 */
	public static final int WIFI_STATE_UNKNOWN = 4;

	/**
	 * Broadcast intent action indicating that Wi-Fi AP has been enabled, disabled,
	 * enabling, disabling, or failed.
	 *
	 * @hide
	 */
	public static final String WIFI_AP_STATE_CHANGED_ACTION =
			"android.net.wifi.WIFI_AP_STATE_CHANGED";

	/**
	 * The lookup key for an int that indicates whether Wi-Fi AP is enabled,
	 * disabled, enabling, disabling, or failed.  Retrieve it with
	 * {@link android.content.Intent#getIntExtra(String,int)}.
	 *
	 * @see #WIFI_AP_STATE_DISABLED
	 * @see #WIFI_AP_STATE_DISABLING
	 * @see #WIFI_AP_STATE_ENABLED
	 * @see #WIFI_AP_STATE_ENABLING
	 * @see #WIFI_AP_STATE_FAILED
	 *
	 * @hide
	 */
	public static final String EXTRA_WIFI_AP_STATE = "wifi_state";
	/**
	 * The previous Wi-Fi state.
	 *
	 * @see #EXTRA_WIFI_AP_STATE
	 *
	 * @hide
	 */
	public static final String EXTRA_PREVIOUS_WIFI_AP_STATE = "previous_wifi_state";
	/**
	 * Wi-Fi AP is currently being disabled. The state will change to
	 * {@link #WIFI_AP_STATE_DISABLED} if it finishes successfully.
	 *
	 * @see #WIFI_AP_STATE_CHANGED_ACTION
	 * @see #getWifiApState()
	 *
	 * @hide
	 */
	public static final int WIFI_AP_STATE_DISABLING = 10;
	/**
	 * Wi-Fi AP is disabled.
	 *
	 * @see #WIFI_AP_STATE_CHANGED_ACTION
	 * @see #getWifiState()
	 *
	 * @hide
	 */
	public static final int WIFI_AP_STATE_DISABLED = 11;
	/**
	 * Wi-Fi AP is currently being enabled. The state will change to
	 * {@link #WIFI_AP_STATE_ENABLED} if it finishes successfully.
	 *
	 * @see #WIFI_AP_STATE_CHANGED_ACTION
	 * @see #getWifiApState()
	 *
	 * @hide
	 */
	public static final int WIFI_AP_STATE_ENABLING = 12;
	/**
	 * Wi-Fi AP is enabled.
	 *
	 * @see #WIFI_AP_STATE_CHANGED_ACTION
	 * @see #getWifiApState()
	 *
	 * @hide
	 */
	public static final int WIFI_AP_STATE_ENABLED = 13;
	/**
	 * Wi-Fi AP is in a failed state. This state will occur when an error occurs during
	 * enabling or disabling
	 *
	 * @see #WIFI_AP_STATE_CHANGED_ACTION
	 * @see #getWifiApState()
	 *
	 * @hide
	 */
	public static final int WIFI_AP_STATE_FAILED = 14;

	/**
	 * Broadcast intent action indicating that a connection to the supplicant has
	 * been established (and it is now possible
	 * to perform Wi-Fi operations) or the connection to the supplicant has been
	 * lost. One extra provides the connection state as a boolean, where {@code true}
	 * means CONNECTED.
	 * @see #EXTRA_SUPPLICANT_CONNECTED
	 */
	public static final String SUPPLICANT_CONNECTION_CHANGE_ACTION =
			"android.net.wifi.supplicant.CONNECTION_CHANGE";
	/**
	 * The lookup key for a boolean that indicates whether a connection to
	 * the supplicant daemon has been gained or lost. {@code true} means
	 * a connection now exists.
	 * Retrieve it with {@link android.content.Intent#getBooleanExtra(String,boolean)}.
	 */
	public static final String EXTRA_SUPPLICANT_CONNECTED = "connected";
	/**
	 * Broadcast intent action indicating that the state of Wi-Fi connectivity
	 * has changed. One extra provides the new state
	 * in the form of a {@link android.net.NetworkInfo} object. If the new
	 * state is CONNECTED, additional extras may provide the BSSID and WifiInfo of
	 * the access point.
	 * as a {@code String}.
	 * @see #EXTRA_NETWORK_INFO
	 * @see #EXTRA_BSSID
	 * @see #EXTRA_WIFI_INFO
	 */

	public static final String NETWORK_STATE_CHANGED_ACTION = "android.net.wifi.STATE_CHANGE";
	/**
	 * The lookup key for a {@link android.net.NetworkInfo} object associated with the
	 * Wi-Fi network. Retrieve with
	 * {@link android.content.Intent#getParcelableExtra(String)}.
	 */
	public static final String EXTRA_NETWORK_INFO = "networkInfo";
	/**
	 * The lookup key for a String giving the BSSID of the access point to which
	 * we are connected. Only present when the new state is CONNECTED.
	 * Retrieve with
	 * {@link android.content.Intent#getStringExtra(String)}.
	 */
	public static final String EXTRA_BSSID = "bssid";
	/**
	 * The lookup key for a {@link android.net.wifi.WifiInfo} object giving the
	 * information about the access point to which we are connected. Only present
	 * when the new state is CONNECTED.  Retrieve with
	 * {@link android.content.Intent#getParcelableExtra(String)}.
	 */
	public static final String EXTRA_WIFI_INFO = "wifiInfo";
	/**
	 * Broadcast intent action indicating that the state of establishing a connection to
	 * an access point has changed.One extra provides the new
	 * {@link SupplicantState}. Note that the supplicant state is Wi-Fi specific, and
	 * is not generally the most useful thing to look at if you are just interested in
	 * the overall state of connectivity.
	 * @see #EXTRA_NEW_STATE
	 * @see #EXTRA_SUPPLICANT_ERROR
	 */

	public static final String SUPPLICANT_STATE_CHANGED_ACTION =
			"android.net.wifi.supplicant.STATE_CHANGE";
	/**
	 * The lookup key for a {@link SupplicantState} describing the new state
	 * Retrieve with
	 * {@link android.content.Intent#getParcelableExtra(String)}.
	 */
	public static final String EXTRA_NEW_STATE = "newState";

	/**
	 * The lookup key for a {@link SupplicantState} describing the supplicant
	 * error code if any
	 * Retrieve with
	 * {@link android.content.Intent#getIntExtra(String, int)}.
	 * @see #ERROR_AUTHENTICATING
	 */
	public static final String EXTRA_SUPPLICANT_ERROR = "supplicantError";

	/**
	 * Broadcast intent action indicating that the configured networks changed.
	 * This can be as a result of adding/updating/deleting a network. If
	 * {@link #EXTRA_MULTIPLE_NETWORKS_CHANGED} is set to true the new configuration
	 * can be retreived with the {@link #EXTRA_WIFI_CONFIGURATION} extra. If multiple
	 * Wi-Fi configurations changed, {@link #EXTRA_WIFI_CONFIGURATION} will not be present.
	 * @hide
	 */

	public static final String CONFIGURED_NETWORKS_CHANGED_ACTION =
			"android.net.wifi.CONFIGURED_NETWORKS_CHANGE";
	/**
	 * The lookup key for a (@link android.net.wifi.WifiConfiguration} object representing
	 * the changed Wi-Fi configuration when the {@link #CONFIGURED_NETWORKS_CHANGED_ACTION}
	 * broadcast is sent.
	 * @hide
	 */

	public static final String EXTRA_WIFI_CONFIGURATION = "wifiConfiguration";
	/**
	 * Multiple network configurations have changed.
	 * @see #CONFIGURED_NETWORKS_CHANGED_ACTION
	 *
	 * @hide
	 */

	public static final String EXTRA_MULTIPLE_NETWORKS_CHANGED = "multipleChanges";
	/**
	 * The lookup key for an integer indicating the reason a Wi-Fi network configuration
	 * has changed. Only present if {@link #EXTRA_MULTIPLE_NETWORKS_CHANGED} is {@code false}
	 * @see #CONFIGURED_NETWORKS_CHANGED_ACTION
	 * @hide
	 */

	public static final String EXTRA_CHANGE_REASON = "changeReason";
	/**
	 * The configuration is new and was added.
	 * @hide
	 */

	public static final int CHANGE_REASON_ADDED = 0;
	/**
	 * The configuration was removed and is no longer present in the system's list of
	 * configured networks.
	 * @hide
	 */

	public static final int CHANGE_REASON_REMOVED = 1;
	/**
	 * The configuration has changed as a result of explicit action or because the system
	 * took an automated action such as disabling a malfunctioning configuration.
	 * @hide
	 */

	public static final int CHANGE_REASON_CONFIG_CHANGE = 2;
	/**
	 * An access point scan has completed, and results are available from the supplicant.
	 * Call {@link #getScanResults()} to obtain the results.
	 */

	public static final String SCAN_RESULTS_AVAILABLE_ACTION = "android.net.wifi.SCAN_RESULTS";
	/**
	 * A batch of access point scans has been completed and the results areavailable.
	 * Call {@link #getBatchedScanResults()} to obtain the results.
	 * @hide pending review
	 */

	public static final String BATCHED_SCAN_RESULTS_AVAILABLE_ACTION =
			"android.net.wifi.BATCHED_RESULTS";
	/**
	 * The RSSI (signal strength) has changed.
	 * @see #EXTRA_NEW_RSSI
	 */

	public static final String RSSI_CHANGED_ACTION = "android.net.wifi.RSSI_CHANGED";
	/**
	 * The lookup key for an {@code int} giving the new RSSI in dBm.
	 */
	public static final String EXTRA_NEW_RSSI = "newRssi";

	/**
	 * Broadcast intent action indicating that the link configuration
	 * changed on wifi.
	 * @hide
	 */
	public static final String LINK_CONFIGURATION_CHANGED_ACTION =
			"android.net.wifi.LINK_CONFIGURATION_CHANGED";

	/**
	 * The lookup key for a {@link android.net.LinkProperties} object associated with the
	 * Wi-Fi network. Retrieve with
	 * {@link android.content.Intent#getParcelableExtra(String)}.
	 * @hide
	 */
	public static final String EXTRA_LINK_PROPERTIES = "linkProperties";

	/**
	 * The lookup key for a {@link android.net.NetworkCapabilities} object associated with the
	 * Wi-Fi network. Retrieve with
	 * {@link android.content.Intent#getParcelableExtra(String)}.
	 * @hide
	 */
	public static final String EXTRA_NETWORK_CAPABILITIES = "networkCapabilities";

	/**
	 * The network IDs of the configured networks could have changed.
	 */

	public static final String NETWORK_IDS_CHANGED_ACTION = "android.net.wifi.NETWORK_IDS_CHANGED";

	/**
	 * Activity Action: Show a system activity that allows the user to enable
	 * scans to be available even with Wi-Fi turned off.
	 *
	 * <p>Notification of the result of this activity is posted using the
	 * {@link android.app.Activity#onActivityResult} callback. The
	 * <code>resultCode</code>
	 * will be {@link android.app.Activity#RESULT_OK} if scan always mode has
	 * been turned on or {@link android.app.Activity#RESULT_CANCELED} if the user
	 * has rejected the request or an error has occurred.
	 */

	public static final String ACTION_REQUEST_SCAN_ALWAYS_AVAILABLE =
			"android.net.wifi.action.REQUEST_SCAN_ALWAYS_AVAILABLE";

	/**
	 * Activity Action: Pick a Wi-Fi network to connect to.
	 * <p>Input: Nothing.
	 * <p>Output: Nothing.
	 */

	public static final String ACTION_PICK_WIFI_NETWORK = "android.net.wifi.PICK_WIFI_NETWORK";

	/**
	 * In this Wi-Fi lock mode, Wi-Fi will be kept active,
	 * and will behave normally, i.e., it will attempt to automatically
	 * establish a connection to a remembered access point that is
	 * within range, and will do periodic scans if there are remembered
	 * access points but none are in range.
	 */
	public static final int WIFI_MODE_FULL = 1;
	/**
	 * In this Wi-Fi lock mode, Wi-Fi will be kept active,
	 * but the only operation that will be supported is initiation of
	 * scans, and the subsequent reporting of scan results. No attempts
	 * will be made to automatically connect to remembered access points,
	 * nor will periodic scans be automatically performed looking for
	 * remembered access points. Scans must be explicitly requested by
	 * an application in this mode.
	 */
	public static final int WIFI_MODE_SCAN_ONLY = 2;
	/**
	 * In this Wi-Fi lock mode, Wi-Fi will be kept active as in mode
	 * {@link #WIFI_MODE_FULL} but it operates at high performance
	 * with minimum packet loss and low packet latency even when
	 * the device screen is off. This mode will consume more power
	 * and hence should be used only when there is a need for such
	 * an active connection.
	 * <p>
	 * An example use case is when a voice connection needs to be
	 * kept active even after the device screen goes off. Holding the
	 * regular {@link #WIFI_MODE_FULL} lock will keep the wifi
	 * connection active, but the connection can be lossy.
	 * Holding a {@link #WIFI_MODE_FULL_HIGH_PERF} lock for the
	 * duration of the voice call will improve the call quality.
	 * <p>
	 * When there is no support from the hardware, this lock mode
	 * will have the same behavior as {@link #WIFI_MODE_FULL}
	 */
	public static final int WIFI_MODE_FULL_HIGH_PERF = 3;

	/** Anything worse than or equal to this will show 0 bars. */
	private static final int MIN_RSSI = -100;

	/** Anything better than or equal to this will show the max bars. */
	private static final int MAX_RSSI = -55;

	/**
	 * Number of RSSI levels used in the framework to initiate
	 * {@link #RSSI_CHANGED_ACTION} broadcast
	 * @hide
	 */
	public static final int RSSI_LEVELS = 5;

	/**
	 * Auto settings in the driver. The driver could choose to operate on both
	 * 2.4 GHz and 5 GHz or make a dynamic decision on selecting the band.
	 * @hide
	 */
	public static final int WIFI_FREQUENCY_BAND_AUTO = 0;

	/**
	 * Operation on 5 GHz alone
	 * @hide
	 */
	public static final int WIFI_FREQUENCY_BAND_5GHZ = 1;

	/**
	 * Operation on 2.4 GHz alone
	 * @hide
	 */
	public static final int WIFI_FREQUENCY_BAND_2GHZ = 2;

	/** List of asyncronous notifications
	 * @hide
	 */
	public static final int DATA_ACTIVITY_NOTIFICATION = 1;

	//Lowest bit indicates data reception and the second lowest
	//bit indicates data transmitted
	/** @hide */
	public static final int DATA_ACTIVITY_NONE         = 0x00;
	/** @hide */
	public static final int DATA_ACTIVITY_IN           = 0x01;
	/** @hide */
	public static final int DATA_ACTIVITY_OUT          = 0x02;
	/** @hide */
	public static final int DATA_ACTIVITY_INOUT        = 0x03;

	/** @hide */
	public static final boolean DEFAULT_POOR_NETWORK_AVOIDANCE_ENABLED = false;

	/* Keep this list in sync with wifi_hal.h */
	/** @hide */
	public static final int WIFI_FEATURE_INFRA            = 0x0001;  // Basic infrastructure mode
	/** @hide */
	public static final int WIFI_FEATURE_INFRA_5G         = 0x0002;  // Support for 5 GHz Band
	/** @hide */
	public static final int WIFI_FEATURE_PASSPOINT        = 0x0004;  // Support for GAS/ANQP
	/** @hide */
	public static final int WIFI_FEATURE_P2P              = 0x0008;  // Wifi-Direct
	/** @hide */
	public static final int WIFI_FEATURE_MOBILE_HOTSPOT   = 0x0010;  // Soft AP
	/** @hide */
	public static final int WIFI_FEATURE_SCANNER          = 0x0020;  // WifiScanner APIs
	/** @hide */
	public static final int WIFI_FEATURE_NAN              = 0x0040;  // Neighbor Awareness Networking
	/** @hide */
	public static final int WIFI_FEATURE_D2D_RTT          = 0x0080;  // Device-to-device RTT
	/** @hide */
	public static final int WIFI_FEATURE_D2AP_RTT         = 0x0100;  // Device-to-AP RTT
	/** @hide */
	public static final int WIFI_FEATURE_BATCH_SCAN       = 0x0200;  // Batched Scan (deprecated)
	/** @hide */
	public static final int WIFI_FEATURE_PNO              = 0x0400;  // Preferred network offload
	/** @hide */
	public static final int WIFI_FEATURE_ADDITIONAL_STA   = 0x0800;  // Support for two STAs
	/** @hide */
	public static final int WIFI_FEATURE_TDLS             = 0x1000;  // Tunnel directed link setup
	/** @hide */
	public static final int WIFI_FEATURE_TDLS_OFFCHANNEL  = 0x2000;  // Support for TDLS off channel
	/** @hide */
	public static final int WIFI_FEATURE_EPR              = 0x4000;  // Enhanced power reporting

	private int getSupportedFeatures() {

		// Comment by Naser:
		// We make all feature supported so we will return: 0x7FFF which is the combination of all features above          
		return 32767;
	}

	private boolean isFeatureSupported(int feature) {
		return (getSupportedFeatures() & feature) == feature;
	}
	/**
	 * @return true if this adapter supports 5 GHz band
	 */
	public boolean is5GHzBandSupported() {
		return isFeatureSupported(WIFI_FEATURE_INFRA_5G);
	}

	/**
	 * @return true if this adapter supports passpoint
	 * @hide
	 */
	public boolean isPasspointSupported() {
		return isFeatureSupported(WIFI_FEATURE_PASSPOINT);
	}

	/**
	 * @return true if this adapter supports WifiP2pManager (Wi-Fi Direct)
	 */
	public boolean isP2pSupported() {
		return isFeatureSupported(WIFI_FEATURE_P2P);
	}

	/**
	 * @return true if this adapter supports portable Wi-Fi hotspot
	 * @hide
	 */
	public boolean isPortableHotspotSupported() {
		return isFeatureSupported(WIFI_FEATURE_MOBILE_HOTSPOT);
	}

	/**
	 * @return true if this adapter supports WifiScanner APIs
	 * @hide
	 */
	public boolean isWifiScannerSupported() {
		return isFeatureSupported(WIFI_FEATURE_SCANNER);
	}

	/**
	 * @return true if this adapter supports Neighbour Awareness Network APIs
	 * @hide
	 */
	public boolean isNanSupported() {
		return isFeatureSupported(WIFI_FEATURE_NAN);
	}

	/**
	 * @return true if this adapter supports Device-to-device RTT
	 * @hide
	 */
	public boolean isDeviceToDeviceRttSupported() {
		return isFeatureSupported(WIFI_FEATURE_D2D_RTT);
	}

	/**
	 * @return true if this adapter supports Device-to-AP RTT
	 */
	public boolean isDeviceToApRttSupported() {
		return isFeatureSupported(WIFI_FEATURE_D2AP_RTT);
	}

	/**
	 * @return true if this adapter supports offloaded connectivity scan
	 */
	public boolean isPreferredNetworkOffloadSupported() {
		return isFeatureSupported(WIFI_FEATURE_PNO);
	}

	/**
	 * @return true if this adapter supports multiple simultaneous connections
	 * @hide
	 */
	public boolean isAdditionalStaSupported() {
		return isFeatureSupported(WIFI_FEATURE_ADDITIONAL_STA);
	}

	/**
	 * @return true if this adapter supports Tunnel Directed Link Setup
	 */
	public boolean isTdlsSupported() {
		return isFeatureSupported(WIFI_FEATURE_TDLS);
	}

	/**
	 * @return true if this adapter supports Off Channel Tunnel Directed Link Setup
	 * @hide
	 */
	public boolean isOffChannelTdlsSupported() {
		return isFeatureSupported(WIFI_FEATURE_TDLS_OFFCHANNEL);
	}

	/**
	 * @return true if this adapter supports advanced power/performance counters
	 */
	public boolean isEnhancedPowerReportingSupported() {
		return isFeatureSupported(WIFI_FEATURE_EPR);
	}

	/**
	 * Check if scanning is always available.
	 *
	 * If this return {@code true}, apps can issue {@link #startScan} and fetch scan results
	 * even when Wi-Fi is turned off.
	 *
	 * To change this setting, see {@link #ACTION_REQUEST_SCAN_ALWAYS_AVAILABLE}.
	 */
	public boolean isScanAlwaysAvailable() {
		return true;
	}

	public int getWifiStatus() {
		return wifiStatus;
	}

	public void setWifiStatus(int wifiStatus) {
		this.wifiStatus = wifiStatus;
	}

	private static final int BASE = 0x800;//Protocol.BASE_WIFI_MANAGER;

	/* Commands to WifiService */
	/** @hide */
	public static final int CONNECT_NETWORK                 = BASE + 1;
	/** @hide */
	public static final int CONNECT_NETWORK_FAILED          = BASE + 2;
	/** @hide */
	public static final int CONNECT_NETWORK_SUCCEEDED       = BASE + 3;

	/** @hide */
	public static final int FORGET_NETWORK                  = BASE + 4;
	/** @hide */
	public static final int FORGET_NETWORK_FAILED           = BASE + 5;
	/** @hide */
	public static final int FORGET_NETWORK_SUCCEEDED        = BASE + 6;

	/** @hide */
	public static final int SAVE_NETWORK                    = BASE + 7;
	/** @hide */
	public static final int SAVE_NETWORK_FAILED             = BASE + 8;
	/** @hide */
	public static final int SAVE_NETWORK_SUCCEEDED          = BASE + 9;

	/** @hide */
	public static final int START_WPS                       = BASE + 10;
	/** @hide */
	public static final int START_WPS_SUCCEEDED             = BASE + 11;
	/** @hide */
	public static final int WPS_FAILED                      = BASE + 12;
	/** @hide */
	public static final int WPS_COMPLETED                   = BASE + 13;

	/** @hide */
	public static final int CANCEL_WPS                      = BASE + 14;
	/** @hide */
	public static final int CANCEL_WPS_FAILED               = BASE + 15;
	/** @hide */
	public static final int CANCEL_WPS_SUCCEDED             = BASE + 16;

	/** @hide */
	public static final int DISABLE_NETWORK                 = BASE + 17;
	/** @hide */
	public static final int DISABLE_NETWORK_FAILED          = BASE + 18;
	/** @hide */
	public static final int DISABLE_NETWORK_SUCCEEDED       = BASE + 19;

	/** @hide */
	public static final int RSSI_PKTCNT_FETCH               = BASE + 20;
	/** @hide */
	public static final int RSSI_PKTCNT_FETCH_SUCCEEDED     = BASE + 21;
	/** @hide */
	public static final int RSSI_PKTCNT_FETCH_FAILED        = BASE + 22;

	/**
	 * Passed with {@link ActionListener#onFailure}.
	 * Indicates that the operation failed due to an internal error.
	 * @hide
	 */
	public static final int ERROR                       = 0;

	/**
	 * Passed with {@link ActionListener#onFailure}.
	 * Indicates that the operation is already in progress
	 * @hide
	 */
	public static final int IN_PROGRESS                 = 1;

	/**
	 * Passed with {@link ActionListener#onFailure}.
	 * Indicates that the operation failed because the framework is busy and
	 * unable to service the request
	 * @hide
	 */
	public static final int BUSY                        = 2;

	/* WPS specific errors */
	/** WPS overlap detected */
	public static final int WPS_OVERLAP_ERROR           = 3;
	/** WEP on WPS is prohibited */
	public static final int WPS_WEP_PROHIBITED          = 4;
	/** TKIP only prohibited */
	public static final int WPS_TKIP_ONLY_PROHIBITED    = 5;
	/** Authentication failure on WPS */
	public static final int WPS_AUTH_FAILURE            = 6;
	/** WPS timed out */
	public static final int WPS_TIMED_OUT               = 7;

	/**
	 * Passed with {@link ActionListener#onFailure}.
	 * Indicates that the operation failed due to invalid inputs
	 * @hide
	 */
	public static final int INVALID_ARGS                = 8;

	/**
	 * Passed with {@link ActionListener#onFailure}.
	 * Indicates that the operation failed due to user permissions.
	 * @hide
	 */
	public static final int NOT_AUTHORIZED              = 9;

	/**
	 * Interface for callback invocation on an application action
	 * @hide
	 */
	public interface ActionListener {
		/** The operation succeeded */
		public void onSuccess();
		/**
		 * The operation failed
		 * @param reason The reason for failure could be one of
		 * {@link #ERROR}, {@link #IN_PROGRESS} or {@link #BUSY}
		 */
		public void onFailure(int reason);
	}

	/** Interface for callback invocation on a start WPS action */
	public static abstract class WpsCallback {
		/** WPS start succeeded */
		public abstract void onStarted(String pin);

		/** WPS operation completed succesfully */
		public abstract void onSucceeded();

		/**
		 * WPS operation failed
		 * @param reason The reason for failure could be one of
		 * {@link #WPS_TKIP_ONLY_PROHIBITED}, {@link #WPS_OVERLAP_ERROR},
		 * {@link #WPS_WEP_PROHIBITED}, {@link #WPS_TIMED_OUT} or {@link #WPS_AUTH_FAILURE}
		 * and some generic errors.
		 */
		public abstract void onFailed(int reason);
	}

	/** Interface for callback invocation on a TX packet count poll action {@hide} */
	public interface TxPacketCountListener {
		/**
		 * The operation succeeded
		 * @param count TX packet counter
		 */
		public void onSuccess(int count);
		/**
		 * The operation failed
		 * @param reason The reason for failure could be one of
		 * {@link #ERROR}, {@link #IN_PROGRESS} or {@link #BUSY}
		 */
		public void onFailure(int reason);
	}
}
