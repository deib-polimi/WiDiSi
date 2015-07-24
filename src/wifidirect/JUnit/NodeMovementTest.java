/*
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
