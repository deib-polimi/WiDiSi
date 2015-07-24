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

import org.junit.Before;
import org.junit.Test;

import peersim.core.Network;
import peersim.core.Node;
import wifidirect.p2pcore.WifiP2pGroup;

// TODO: Auto-generated Javadoc
/**
 * The Class WifiP2pGroupTest.
 */
public class WifiP2pGroupTest {

	/** The newgroup. */
	private WifiP2pGroup newgroup;
	
	/** The group owner. */
	private Node groupOwner = null;


	/**
	 * Sets the up.
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void setUp() throws Exception {
		newgroup = new WifiP2pGroup();
		groupOwner = Network.get(1);
		newgroup.setGroupValid(true);
		newgroup.setGroupOwner(groupOwner);
		newgroup.setmInterface("NaserInterface");
		newgroup.setmPassphrase("ABCDE");

	}

	/**
	 * Test add remove node.
	 */
	@Test
	public void testAddRemoveNode() {


		// check if the addNode method can add a node to the node list correctly
		newgroup.addNode(groupOwner);
		assertTrue("adding one Node", newgroup.getNodeList().contains(groupOwner));


		// check if it does not add the same node (e.g. groupowner) to the nodeList twice
		newgroup.addNode(groupOwner);
		assertEquals("adding the same node",1, newgroup.getNodeList().size());

		// check if the maximum number of nodes added is below the group capacity (10)
		for(int i=0; i<Network.size(); i++){
			newgroup.addNode(Network.get(i));
		}
		assertTrue("maximum number of nodes in a group", newgroup.getGroupSize()<=WifiP2pGroup.groupCapacity);
		
		//check removing nodes
		newgroup.removeNode(groupOwner);
		assertFalse("Group Owner removed", newgroup.getNodeList().contains(groupOwner));
		
		//check resetting the group
		newgroup.resetGroup();
		assertNull("Group Reset: GroupOwner should be null", newgroup.getGroupOwner());
		assertFalse("Group Reset: GroupValid should be false", newgroup.isGroupValid());
		assertNull("Group Reset: PassPhrase should be null", newgroup.getmPassphrase());
		assertNull("Group Reset: Interface should be null", newgroup.getmInterface());
		assertEquals("Group Reset: NetID should be 0", 0, newgroup.getmNetId());
	}
}
