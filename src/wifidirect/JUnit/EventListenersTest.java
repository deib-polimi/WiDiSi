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
package wifidirect.JUnit;


import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import peersim.core.Network;
import peersim.core.Node;
import wifidirect.p2pcore.*;
import wifidirect.p2pcore.WifiP2pManager.*;

// TODO: Auto-generated Javadoc
/**
 * The Class EventListenersTest.
 */
public class EventListenersTest implements PeerListListener, ConnectionInfoListener, GroupInfoListener, DnsSdServiceResponseListener, DnsSdTxtRecordListener, BroadcastReceiver{
	
	/** The Ev listener. */
	private eventListeners EvListener;
	
	/** The new pid. */
	private int newPid;
	
	/** The connotgo. */
	private Node CONGO, CONNOTGO; // CONGO: CONNECTED and GO ------ CONNOTGO: CONNECTED but not GO
	
	/** The new event. */
	private Message newEvent;

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
	
	
	/** The node info. */
	private nodeP2pInfo nodeInfo;
	
	/** The broadcast event. */
	private String broadcastEvent = "";
	
	/** The peer list recieved. */
	private boolean peerListRecieved 		= false;
	
	/** The group info received. */
	private boolean groupInfoReceived 		= false;
	
	/** The connection info received. */
	private boolean connectionInfoReceived 	= false;
//	private boolean	txtRecordReceived 		= false;
//	private boolean	dnsServiceReceived 		= false;

	/**
 * Sets the up.
 *
 * @throws Exception the exception
 */
@Before
	public void setUp() throws Exception {
		EvListener = new eventListeners("protocol.lst");
		EvListener.addGroupInfoListener(this);
		EvListener.addBroadcastReceiver(this);
		EvListener.addConInfoListener(this);
		EvListener.addDnsSdResponseListener(this);
		EvListener.addDnsSdTxtRecordListener(this);
		EvListener.addPeerListListener(this);
		
		// find a node which is group owner
		for (int i=0; i<Network.size(); i++){
			nodeInfo = (nodeP2pInfo) Network.get(i).getProtocol(EvListener.p2pInfoPid);
			if(nodeInfo.isGroupOwner() && nodeInfo.getStatus()==CONNECTED){
				CONGO = Network.get(i);
				break;
			}
		}

		// find a node which is connected but not GO
		for (int i=0; i<Network.size(); i++){
			nodeInfo = (nodeP2pInfo) Network.get(i).getProtocol(EvListener.p2pInfoPid);
			if(!nodeInfo.isGroupOwner() && nodeInfo.getStatus()==CONNECTED){
				CONNOTGO = Network.get(i);
				break;
			}
		}

		newPid = EvListener.thisPid;
		newEvent = new Message();	
	}

	/**
	 * Test.
	 */
	@Test
	public void test() {
		
		nodeInfo = (nodeP2pInfo) CONNOTGO.getProtocol(EvListener.p2pInfoPid);
		
		// GROUP_INFO_REQUEST
		newEvent.event = "GROUP_INFO_REQUEST";
		EvListener.processEvent(CONGO, newPid, newEvent);
		assertTrue("group_Info_Reuqest - Connected and GO - checking final result", groupInfoReceived);
		
		// CLIENT_GROUP_INFO_RESPONSE
		newEvent.event = "CLIENT_GROUP_INFO_RESPONSE";
		groupInfoReceived = false;
		WifiP2pGroup p2pGroup = new WifiP2pGroup();
		p2pGroup.setGroupValid(true);
		p2pGroup.setGroupOwner(CONGO);
		newEvent.object = p2pGroup;
		EvListener.processEvent(CONNOTGO, newPid, newEvent);
		assertTrue("group_Info_Reuqest - Connected and not GO - checking final result", groupInfoReceived);

		//WIFI_P2P_PEERS_CHANGED_ACTION
		newEvent.event = "WIFI_P2P_PEERS_CHANGED_ACTION";
		nodeInfo.setPeerDiscoveryStarted(true);
		broadcastEvent = "";
		EvListener.processEvent(CONNOTGO, newPid, newEvent);
		assertEquals("WIFI_P2P_PEERS_CHANGED_ACTION - Connected and not GO - checking final result", "WIFI_P2P_PEERS_CHANGED_ACTION", broadcastEvent);
		
		//WIFI_P2P_THIS_DEVICE_CHANGED_ACTION
		newEvent.event = "WIFI_P2P_THIS_DEVICE_CHANGED_ACTION";
		broadcastEvent = "";
		EvListener.processEvent(CONNOTGO, newPid, newEvent);
		assertEquals("WIFI_P2P_PEERS_CHANGED_ACTION - Connected and not GO - checking final result", "WIFI_P2P_THIS_DEVICE_CHANGED_ACTION", broadcastEvent);

		//REQUEST_PEERS
		newEvent.event = "REQUEST_PEERS";
		peerListRecieved = false;
		EvListener.processEvent(CONNOTGO, newPid, newEvent);
		assertTrue("REQUEST_PEERS - Connected and not GO - checking final result", peerListRecieved);
		
		//WIFI_P2P_STATE_CHANGED_ACTION
		newEvent.event = "WIFI_P2P_STATE_CHANGED_ACTION";
		broadcastEvent = "";
		EvListener.processEvent(CONNOTGO, newPid, newEvent);
		assertEquals("WIFI_P2P_PEERS_CHANGED_ACTION - Connected and not GO - checking final result", "WIFI_P2P_STATE_CHANGED_ACTION", broadcastEvent);
		
		//REQUEST_CONNECTION_INFO
		newEvent.event = "REQUEST_CONNECTION_INFO";
		connectionInfoReceived = false;
		EvListener.processEvent(CONNOTGO, newPid, newEvent);
		assertTrue("REQUEST_CONNECTION_INFO - Connected and not GO - checking final result", connectionInfoReceived);
		
		//WIFI_P2P_CONNECTION_CHANGED_ACTION
		newEvent.event = "WIFI_P2P_CONNECTION_CHANGED_ACTION";
		broadcastEvent = "";
		EvListener.processEvent(CONNOTGO, newPid, newEvent);
		assertEquals("WIFI_P2P_CONNECTION_CHANGED_ACTION - Connected and not GO - checking final result", "WIFI_P2P_CONNECTION_CHANGED_ACTION", broadcastEvent);

		//Commented deliberately. If you want to test this part first make serviceHolder on wifiP2pService class to public
		//onBonjourServiceAvailable 
//		txtRecordReceived = false;
//		dnsServiceReceived = false;
//		newEvent.event = "onBonjourServiceAvailable";
//		nodeInfo.setServicediscoveryStarted(true);
//		HashMap<String, String> record = new HashMap<String, String>();
//		record.put("Intention", "10");
//		wifiP2pService p2pService = new wifiP2pService("New_Service", "New_Type", record);
//		p2pService.serviceHolder = CONNOTGO;
//		newEvent.object =p2pService;
//		EvListener.processEvent(CONNOTGO, newPid, newEvent); 
//		assertTrue("Txt Record Received?", txtRecordReceived);
//		assertTrue("DNS Serice Received?", dnsServiceReceived);
	}

	/* (non-Javadoc)
	 * @see peersim.wifidirect.p2pcore.BroadcastReceiver#onReceive(peersim.wifidirect.p2pcore.wifiP2pEvent)
	 */
	@Override
	public void onReceive(wifiP2pEvent wifip2pevent) {
		// TODO Auto-generated method stub
		System.out.println(wifip2pevent.getEvent());
		broadcastEvent = wifip2pevent.getEvent();
		
		
	}

	/* (non-Javadoc)
	 * @see peersim.wifidirect.p2pcore.WifiP2pManager.DnsSdTxtRecordListener#onDnsSdTxtRecordAvailable(java.lang.String, java.util.Map, peersim.wifidirect.p2pcore.WifiP2pDevice)
	 */
	@Override
	public void onDnsSdTxtRecordAvailable(String fullDomainName, Map<String, String> record, WifiP2pDevice srcDevice) {
		//txtRecordReceived = true;
		
	}

	/* (non-Javadoc)
	 * @see peersim.wifidirect.p2pcore.WifiP2pManager.DnsSdServiceResponseListener#onDnsSdServiceAvailable(java.lang.String, java.lang.String, peersim.wifidirect.p2pcore.WifiP2pDevice)
	 */
	@Override
	public void onDnsSdServiceAvailable(String instanceName,
			String registrationType, WifiP2pDevice srcDevice) {
		// TODO Auto-generated method stub
		//dnsServiceReceived = true;
		
	}

	/* (non-Javadoc)
	 * @see peersim.wifidirect.p2pcore.WifiP2pManager.GroupInfoListener#onGroupInfoAvailable(peersim.wifidirect.p2pcore.WifiP2pGroup)
	 */
	@Override
	public void onGroupInfoAvailable(WifiP2pGroup groupInfo) {
		groupInfoReceived = true;
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see peersim.wifidirect.p2pcore.WifiP2pManager.ConnectionInfoListener#onConnectionInfoAvailable(peersim.wifidirect.p2pcore.WifiP2pInfo)
	 */
	@Override
	public void onConnectionInfoAvailable(WifiP2pInfo wifiInfo) {
		connectionInfoReceived = true;
		
	}

	/* (non-Javadoc)
	 * @see peersim.wifidirect.p2pcore.WifiP2pManager.PeerListListener#onPeersAvailable(peersim.wifidirect.p2pcore.WifiP2pDeviceList)
	 */
	@Override
	public void onPeersAvailable(WifiP2pDeviceList peers) {
		peerListRecieved = true;
		
	}

}
