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

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import peersim.core.Network;
import peersim.core.Node;
import wifidirect.nodemovement.CoordinateKeeper;
import wifidirect.nodemovement.NodeMovement;

// TODO: Auto-generated Javadoc
/**
 * The Class NodeMovementTest.
 */
public class NodeMovementTest {

	/** The node movement. */
	private NodeMovement nodeMovement;
	
	/** The node list. */
	private List<Node> nodeList = new ArrayList<Node>();;
	
	/** The coord pid. */
	private int coordPid;

	/**
	 * Sets the up.
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void setUp() throws Exception {
		nodeMovement = new NodeMovement("control.move");
		coordPid = nodeMovement.coordinatesPid;
		//nodeList = new ArrayList<Node>();
		for (int i=0; i<Network.size(); i++){
			nodeList.add(Network.get(i));
		}	
		((CoordinateKeeper) Network.get(1).getProtocol(coordPid)).setX(0.5);
		((CoordinateKeeper) Network.get(1).getProtocol(coordPid)).setY(0.25);
		((CoordinateKeeper) Network.get(2).getProtocol(coordPid)).setX(0.1);
		((CoordinateKeeper) Network.get(2).getProtocol(coordPid)).setY(-0.05);
	}

	/**
	 * Test distance.
	 */
	@Test
	public void testDistance() {
		assertEquals("the distance between two nodes", 0.5, NodeMovement.distance(Network.get(1), Network.get(2), coordPid), 0.00001);
	}

	/**
	 * Test execute.
	 */
	@Test
	public void testExecute() {
		nodeMovement.execute();		
		for(int i=0; i<Network.size(); i++){
			if(((CoordinateKeeper) Network.get(i).getProtocol(coordPid)).isMobile()){
				assertTrue(NodeMovement.distance(Network.get(i), nodeList.get(i), coordPid)<0.0001);
				assertTrue(NodeMovement.distance(Network.get(i), nodeList.get(i), coordPid)==0);
			}else if(!((CoordinateKeeper) Network.get(i).getProtocol(coordPid)).isMobile()){				
				assertTrue(NodeMovement.distance(Network.get(i), nodeList.get(i), coordPid)==0);
			}
		}
	}



}
