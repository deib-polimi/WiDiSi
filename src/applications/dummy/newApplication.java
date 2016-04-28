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
 * 
 *
 */
package applications.dummy;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Node;
import peersim.edsim.EDProtocol;
import visualization.Visualizer;
import wifi.ScanResult;
import wifi.WifiManager;
import wifidirect.p2pcore.BroadcastReceiver;
import wifidirect.p2pcore.Callback;
import wifidirect.p2pcore.WifiP2pConfig;
import wifidirect.p2pcore.WifiP2pDevice;
import wifidirect.p2pcore.WifiP2pDeviceList;
import wifidirect.p2pcore.WifiP2pGroup;
import wifidirect.p2pcore.WifiP2pInfo;
import wifidirect.p2pcore.WifiP2pManager;
import wifidirect.p2pcore.callbackMessage;
import wifidirect.p2pcore.nodeP2pInfo;
import wifidirect.p2pcore.wifiP2pEvent;
import wifidirect.p2pcore.wifiP2pService;
import wifidirect.p2pcore.WifiP2pManager.*;
// TODO: Auto-generated Javadoc
/**
 * The Class newApplication.
 * It should be set in the configuration file as well like any other protocol
 */
public class newApplication implements EDProtocol, PeerListListener, CDProtocol, ConnectionInfoListener, 
GroupInfoListener, DnsSdServiceResponseListener, DnsSdTxtRecordListener, BroadcastReceiver, Callback{

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

	/** The Constant MESSAGE_READ. */
	public static final int 	MESSAGE_READ 			= 0x400 + 1;		// Reserved Code for Message Handler 
	
	/** The Constant MY_HANDLE. */
	public static final int 	MY_HANDLE 				= 0x400 + 2;		// Reserved Code for Message Handler

	/**  transport protocol for event-based simulation *. */
	public static final String PAR_MANAGE = "p2pmanager";

	/**  Transport protocol identifier for event-based simulation *. */
	public int p2pmanagerId;

	/**  eventDetection protocol for event-based simulation *. */
	public static final String PAR_P2PINFO = "p2pinfo";

	/**  eventDetection protocol identifier for event-based simulation *. */
	public int p2pInfoPid;
	public int wifimanagerPid;

	/** The cycle. */
	private long cycle = 0;

	/** The manager. */
	private WifiP2pManager manager;

	/** The node info. */
	private nodeP2pInfo nodeInfo;

	/** The peer list. */
	private List<WifiP2pDevice> peerList = new ArrayList<WifiP2pDevice>();

	/** The is connected. */
	public boolean isConnected = false;
	
	/** The is groupe owner. */
	public boolean isGroupeOwner = false;
	
	/** The value. */
	public double value = 0;
	
	/**
	 *  The this node.
	 *
	 * @param prefix the prefix
	 */
	private WifiManager wifiManager;
	
	private Node thisNode;

	/**
	 * Instantiates a new new application.
	 *
	 * @param prefix the prefix
	 */
	public newApplication (String prefix){
		p2pmanagerId = Configuration.getPid(prefix + "." + PAR_MANAGE);
		p2pInfoPid = Configuration.getPid(prefix + "." + PAR_P2PINFO);
		wifimanagerPid = Configuration.getPid(prefix + "." + "wifimanager");
	}

	// Since it is a Event-Driven engine this method will be called once at the beggining. 
	// We need to register the receivers once at the beggining
	/* (non-Javadoc)
	 * @see peersim.cdsim.CDProtocol#nextCycle(peersim.core.Node, int)
	 */
	@Override
	public void nextCycle(Node node, int protocolID) {
		manager = (WifiP2pManager) node.getProtocol(p2pmanagerId);
		wifiManager = (WifiManager) node.getProtocol(wifimanagerPid);
		nodeInfo = (nodeP2pInfo) node.getProtocol(p2pInfoPid);
		thisNode = node;

		if(cycle==1){	
			manager.registerBroadcastReceiver(this);
			manager.registerPeerListener(this);
			manager.registerConInfoListener(this);
			manager.registerGroupInfoListener(this);
			manager.registerDnsSdResponseListeners(this);
			manager.registerDnsSdTxtRecordListener(this);
			manager.registerHandler(this);
			value = CommonState.r.nextDouble();
			
			wifiManager.startScan();
			//Visualizer.print(String.valueOf(value));
		}
		if (cycle==2){	
			// add a Local service with random values
			HashMap<String, String> record = new HashMap<String, String>();
			record.put(node.getID()+": "+String.valueOf(CommonState.r.nextDouble()), String.valueOf(CommonState.r.nextDouble()));
			wifiP2pService service = new wifiP2pService("Magnet", "TCP", record);
			manager.addLocalService(service);
		}

		if(!nodeInfo.isPeerDiscoveryStarted() && cycle==5){
			manager.discoverServices();
		}else if(nodeInfo.isPeerDiscoveryStarted() && cycle%30==0){
			manager.requestPeers();
		}

		// Standard Group Formation
				if(cycle%30 ==0 && cycle >20){
					if(!peerList.isEmpty()){
						WifiP2pConfig config = new WifiP2pConfig();
						config.deviceAddress = peerList.get(CommonState.r.nextInt(peerList.size())).deviceAddress;
						manager.connect(config);
					}
				}

		// Autonamous Group Formation
//		if(node.getID()%10==0 && !isGroupeOwner && cycle>20){
//			manager.createGroup();
//		}
//
//		if(isConnected && isGroupeOwner && !peerList.isEmpty()){
//			for(WifiP2pDevice peer:peerList){
//				if(peer.status==AVAILABLE){
//					manager.connect(peer.deviceAddress);
//				}
//			}
//		}

		if(isConnected && nodeInfo.getGroupOwner()!=null){
			callbackMessage cMessage = new callbackMessage(MESSAGE_READ, String.valueOf(value));

			if(isGroupeOwner){
				for(WifiP2pDevice peer: peerList){
					manager.send(cMessage, peer.deviceAddress);
					Visualizer.print("Node: " + node.getID() + " A message send to client: " + peer.deviceAddress, Color.blue);
				}
			}else{
				manager.send(cMessage, String.valueOf(nodeInfo.getGroupOwner().getID()));
				Visualizer.print("Node: " + node.getID() + " A message send to GO: " + nodeInfo.getGroupOwner().getID(), Color.blue);
			}
		}
		cycle++;	
	}

	/* (non-Javadoc)
	 * @see peersim.edsim.EDProtocol#processEvent(peersim.core.Node, int, java.lang.Object)
	 */
	@Override
	public void processEvent(Node node, int pid, Object event) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public newApplication clone(){
		newApplication nw = null;
		try { nw = (newApplication) super.clone(); }
		catch( CloneNotSupportedException e ) {} // never happen
		nw.p2pmanagerId = p2pmanagerId;
		nw.p2pInfoPid = p2pInfoPid;
		nw.wifimanagerPid = wifimanagerPid;
		return nw;	
	}

	/* (non-Javadoc)
	 * @see peersim.wifidirect.p2pcore.WifiP2pManager.PeerListListener#onPeersAvailable(peersim.wifidirect.p2pcore.wifiP2pEvent)
	 */
	@Override
	public void onPeersAvailable(WifiP2pDeviceList peers) {
		peerList.clear();
		peerList.addAll(peers.getDeviceList());

	}

	/* (non-Javadoc)
	 * @see peersim.wifidirect.p2pcore.WifiP2pManager.ConnectionInfoListener#onConnectionInfoAvailable(peersim.wifidirect.p2pcore.wifiP2pEvent)
	 */
	@Override
	public void onConnectionInfoAvailable(WifiP2pInfo wifiInfo) {
		Visualizer.print("Node: " + thisNode.getID() +" / onConnectionInfoAvailable", Color.blue);
		isConnected = wifiInfo.groupFormed;
		isGroupeOwner = wifiInfo.isGroupOwner;
	}

	/* (non-Javadoc)
	 * @see peersim.wifidirect.p2pcore.WifiP2pManager.GroupInfoListener#onGroupInfoAvailable(peersim.wifidirect.p2pcore.wifiP2pEvent)
	 */
	@Override
	public void onGroupInfoAvailable(WifiP2pGroup groupInfo) {
		Visualizer.print("Node: " + thisNode.getID() +" / onGroupInfoAvailable", Color.blue);
	}

	/* (non-Javadoc)
	 * @see peersim.wifidirect.p2pcore.WifiP2pManager.DnsSdTxtRecordListener#onDnsSdTxtRecordAvailable(peersim.wifidirect.p2pcore.wifiP2pEvent)
	 */
	@Override
	public void onDnsSdTxtRecordAvailable(String fullDomainName, Map<String, String> record, WifiP2pDevice srcDevice) {
	if(thisNode!=null)
		Visualizer.print("Node: " + thisNode.getID() +" / onDnsSdTxtRecordAvailable: " + record, Color.blue);
	}

	/* (non-Javadoc)
	 * @see peersim.wifidirect.p2pcore.WifiP2pManager.DnsSdServiceResponseListener#onDnsSdServiceAvailable(peersim.wifidirect.p2pcore.wifiP2pEvent)
	 */
	@Override
	public void onDnsSdServiceAvailable(String instanceName,
			String registrationType, WifiP2pDevice srcDevice) {
		if(thisNode!=null)
			Visualizer.print("Node: " + thisNode.getID() +" / onDnsSdServiceAvailable", Color.blue);	
	}

	/* (non-Javadoc)
	 * @see peersim.wifidirect.p2pcore.BroadcastReceiver#onReceive(peersim.wifidirect.p2pcore.wifiP2pEvent)
	 */
	@Override
	public void onReceive(wifiP2pEvent wifip2pevent) {
		// TODO Auto-generated method stub
		switch ((String)wifip2pevent.getObj())
		{
		case "WIFI_P2P_PEERS_CHANGED_ACTION":
			manager.requestPeers();
			break;
		case "WIFI_P2P_STATE_CHANGED_ACTION":

			break;
		case "WIFI_P2P_CONNECTION_CHANGED_ACTION":
			if(nodeInfo.getStatus()==AVAILABLE && !nodeInfo.isPeerDiscoveryStarted()){
				manager.discoverServices();
			}
			manager.requestConnectionInfo();

			if(nodeInfo.getStatus()==CONNECTED){
				manager.requestGroupInfo();
			}
			break;
		case "WIFI_P2P_THIS_DEVICE_CHANGED_ACTION":
			break;
			
		case "SCAN_RESULTS_AVAILABLE_ACTION":
			
			List<ScanResult> ScanResultList = new ArrayList<ScanResult>();
			ScanResultList.addAll(wifiManager.getScanResults());
			Visualizer.print(ScanResultList, Color.blue);
		}

	}

	// will be called if we are connected and there is message to read
	// the message may be sent by a client in this group or the group owner
	// message will not be re routed to the other clients inside the group if this is GO
	/* (non-Javadoc)
	 * @see peersim.wifidirect.p2pcore.WifiP2pManager.Callback#handleMessage(peersim.wifidirect.p2pcore.callbackMessage)
	 */
	// GO is responsible for that
	@Override
	public void handleMessage(callbackMessage msg) {
		// TODO Auto-generated method stub
		switch (msg.what)
		{
		case MESSAGE_READ:
			double readValue = Double.parseDouble((String) msg.obj);
			//System.out.println("Value Received: " + readValue);

			value = (Math.abs(readValue-sDeviation.meanValue)>=Math.abs(value-sDeviation.meanValue))? value:readValue;
			//System.out.println("New value: " + value);
			break;
		case MY_HANDLE:
			Visualizer.print("Node: " + thisNode.getID() + " MY_HANDLE", Color.blue);
			break;
		}
	}
}

