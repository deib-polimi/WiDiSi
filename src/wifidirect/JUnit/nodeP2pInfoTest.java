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
import java.util.HashMap;
import org.junit.Before;
import org.junit.Test;
import peersim.core.Network;
import peersim.core.Node;
import wifidirect.p2pcore.nodeP2pInfo;
import wifidirect.p2pcore.wifiP2pService;

// TODO: Auto-generated Javadoc
/**
 * The Class nodeP2pInfoTest.
 */
public class nodeP2pInfoTest {

	/** The node info. */
	private nodeP2pInfo nodeInfo;
	
	/** The new service. */
	private wifiP2pService newService = null;
	
	/** The this node. */
	private Node thisNode;
	
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

	/**
	 * Sets the up.
	 *
	 * @throws Exception the exception
	 */
	// this setUp will be performed before each individual test
	@Before
	public void setUp() throws Exception {
		//System.out.println("SetUp at nodeP2PInfoTest");
		for(int i=0; i<Network.size(); i++){
			if(Network.get(i).isUp()){
				thisNode = Network.get(i);
				break;
			}
		}
		
		nodeInfo = (nodeP2pInfo) thisNode.getProtocol(nodeP2pInfo.thisPid);

		//Create a new dump wifi p2p service for test
		String serviceName = "Test";
		String serviceType = "TCP";
		HashMap<String, String> serviceRecord = new HashMap<String, String>();
		serviceRecord.put(serviceName, serviceType);
		serviceRecord.put("Intention", "14");
		newService = new wifiP2pService(serviceName, serviceType, serviceRecord);
	}

	/**
	 * Test add wifi p2p service.
	 */
	@Test
	public void testAddWifiP2pService() {
		// adding new service should return true
		assertTrue("adding new service should return true", nodeInfo.addWifiP2pService(newService));

		// the newService has already added. adding it again should return false
		assertFalse("adding the same service should return false", nodeInfo.addWifiP2pService(newService));
	}

	/**
	 * Test remove wifi p2p service.
	 */
	@Test
	public void testRemoveWifiP2pService() {

		assertFalse("Removing a service which is not available at the list should return false", nodeInfo.removeWifiP2pService(newService));

		nodeInfo.addWifiP2pService(newService);
		assertTrue("Removing a service which is laready available should return true", nodeInfo.removeWifiP2pService(newService));
	}

	/**
	 * Test clear local services.
	 */
	@Test
	public void testClearLocalServices() {
		// Adding a service
		nodeInfo.addWifiP2pService(newService);
		nodeInfo.setWifiP2pEnabled(true);
		assertTrue("Clear local services while there are some registedred service(s) should return true", nodeInfo.clearLocalServices());
	}

	/**
	 * Test set wifi p2p enabled.
	 */
	@Test
	public void testSetWifiP2pEnabled() {
		nodeInfo.setWifiP2pEnabled(false);
		assertTrue("setting wifip2p enable to false and then true", nodeInfo.setWifiP2pEnabled(true));
	}

	/**
	 * Test set status.
	 */
	@Test
	public void testSetStatus() {
		nodeInfo.setWifiP2pEnabled(true);

		nodeInfo.setStatus(AVAILABLE);
		assertEquals("Status Set to AVAILABLE", AVAILABLE, nodeInfo.getStatus());
		assertTrue("Seting node status to Connected while previous status was Available", nodeInfo.setStatus(CONNECTED));
		assertEquals("Status Set to CONNECTED", CONNECTED, nodeInfo.getStatus());
		assertTrue("Seting node status to UNAVAILABLE while previous status was CONNECTED", nodeInfo.setStatus(UNAVAILABLE));
	}

	/**
	 * Test set peer discovery started.
	 */
	@Test
	public void testSetPeerDiscoveryStarted() {
		nodeInfo.setPeerDiscoveryStarted(false);
		assertFalse("Peerdiscovery was set to false", nodeInfo.isPeerDiscoveryStarted());
		assertTrue("peerdiscovery started while the previous status was stopped",nodeInfo.setPeerDiscoveryStarted(true));
		assertTrue("Peerdiscovery was set to true", nodeInfo.isPeerDiscoveryStarted());
	}

	/**
	 * Test set servicediscovery started.
	 */
	@Test
	public void testSetServicediscoveryStarted() {
		nodeInfo.setPeerDiscoveryStarted(true);
		nodeInfo.setServicediscoveryStarted(false);
		assertFalse("Service discovery was set to false", nodeInfo.isServiceDiscoveryStarted());
		assertTrue("Service discovery started while the previous status was stopped",nodeInfo.setServicediscoveryStarted(true));
		assertTrue("Service discovery was set to true", nodeInfo.isServiceDiscoveryStarted());

	}

	/**
	 * Test set device name.
	 */
	@Test
	public void testSetDeviceName() {
		nodeInfo.setDeviceName("FirsName");
		assertTrue("Change device name",nodeInfo.setDeviceName("NaserPhone"));
		assertEquals("assert equal device name", "NaserPhone", nodeInfo.getDeviceName());
		nodeInfo.setDeviceName("Device_" + nodeInfo.thisNode.getID());
	}
}
