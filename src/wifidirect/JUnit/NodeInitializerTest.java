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

// If this Control and Initializer perform its task correctly all nodes should have the
// necessary value. we are checking that in this test case
package wifidirect.JUnit;

import static org.junit.Assert.*;
import org.junit.Test;
import peersim.core.Network;
import wifidirect.p2pcore.WifiP2pManager;
import wifidirect.p2pcore.nodeInitializer;
import wifidirect.p2pcore.nodeP2pInfo;

// TODO: Auto-generated Javadoc
/**
 * The Class NodeInitializerTest.
 */
public class NodeInitializerTest {

	/**
	 * Test.
	 */
	@Test
	public void test() {
		for(int i=0; i<Network.size(); i++){
			nodeP2pInfo nodeInfo = (nodeP2pInfo) Network.get(i).getProtocol(nodeInitializer.nodeInfoPid);
			WifiP2pManager p2pmanager = (WifiP2pManager)  Network.get(i).getProtocol(nodeInitializer.p2pManagerPid);
			
			assertTrue("Battery should be between [0 100)", (nodeInfo.getBatteryLevel()>=0 && nodeInfo.getBatteryLevel()<100));
			assertTrue("GO Intention should be between [0 15)", (nodeInfo.getGoIntentionValue()>=0 && nodeInfo.getGoIntentionValue()<15));
			assertTrue("Memory Remained should be between [10 100)", (nodeInfo.getMemoryRemained()>=10 && nodeInfo.getMemoryRemained()<100));
			assertTrue("Processing cap should be between [10 100)", (nodeInfo.getProcessingCap()>=10 && nodeInfo.getProcessingCap()<100));
			assertEquals("thisNode in p2pInfo should set correctly", Network.get(i), nodeInfo.thisNode);
			assertEquals("thisPid in p2pInfo should set correctly", nodeInitializer.nodeInfoPid, nodeP2pInfo.thisPid);
			assertEquals("thisNode in p2pmanager should set correctly", Network.get(i), p2pmanager.thisNode);
			assertEquals("thisPid in p2pmanager should set correctly", nodeInitializer.p2pManagerPid, p2pmanager.thisPid);
			assertEquals("device name should set correctly", "Device_" + Network.get(i).getID(), p2pmanager.getDeviceName());
			
		}
	}

}
