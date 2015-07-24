/*
 * 
 */
package wifidirect.nodemovement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Linkable;
import peersim.core.Network;
import peersim.core.Node;
import wifidirect.p2pcore.WifiP2pManager;
import wifidirect.p2pcore.nodeP2pInfo;

// TODO: Auto-generated Javadoc
/**
 * The Class NodeMovement.
 */
public class NodeMovement implements Control{

	/** Linkable protocol *. */
	public static final String PAR_LINKABLE = "linkable";
	
	/** The linkable id. */
	public int linkableId;

	/**  Coordinates protocol which keeps coordinates *. */
	public static final String PAR_COORD = "coord";
	
	/** The coordinates pid. */
	public int coordinatesPid;

	/**  eventDetection protocol for event-based simulation *. */
	public static final String PAR_P2PINFO = "p2pinfo";
	
	/** The p2p info pid. */
	public int p2pInfoPid;

	/** The Constant PAR_MANAGE. */
	public static final String PAR_MANAGE = "p2pmanager";
	
	/** The p2pmanager id. */
	public int p2pmanagerId;

	/** The Constant RADIO_RANGE. */
	private static final String RADIO_RANGE = "radio_range";
	
	/** The range. */
	protected final double range;

	/** The Constant NODE_SPEED_MAX. */
	private static final String NODE_SPEED_MAX = "node_speed_max";
	
	/** The maxspeed. */
	protected final double maxspeed;

	/** The Constant NODE_SPEED_MIN. */
	private static final String NODE_SPEED_MIN = "node_speed_min";
	
	/** The minspeed. */
	protected final double minspeed;


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

	/** The move freq. */
	private HashMap<Long, Container> moveFreq = new HashMap<Long, Container>();


	/**
	 * Instantiates a new node movement.
	 *
	 * @param prefix the prefix
	 */
	public NodeMovement (String prefix){
		linkableId 		= Configuration.getPid(prefix + "." + PAR_LINKABLE);
		coordinatesPid 	= Configuration.getPid(prefix + "." + PAR_COORD);
		p2pInfoPid = Configuration.getPid(prefix + "." + PAR_P2PINFO);
		p2pmanagerId 	= Configuration.getPid(prefix + "." + PAR_MANAGE);
		range			= Configuration.getDouble(prefix + "." + RADIO_RANGE);
		maxspeed		= Configuration.getDouble(prefix + "." + NODE_SPEED_MAX); 
		minspeed		= Configuration.getDouble(prefix + "." + NODE_SPEED_MIN);		
	}
	// move nodes in an arbistrary pattern
	/* (non-Javadoc)
	 * @see peersim.core.Control#execute()
	 */
	// A node moves in the same direction for 5 cycles and then change it's direction
	@Override
	public boolean execute() {
		int minusY = 0; 
		int minusX = 0;
		double tempX = 0;
		double tempY = 0;
		double randX = 0;
		double randY = 0;

		for (int i=0; i<Network.size(); i++){
			Node node = Network.get(i);
			CoordinateKeeper coordinates = (CoordinateKeeper) node.getProtocol(coordinatesPid);
			if(coordinates.isMobile()){
				if(!moveFreq.containsKey(node.getID())){

					minusY = CommonState.r.nextBoolean() ? 1 : -1;
					minusX = CommonState.r.nextBoolean() ? 1 : -1;
					randX = (minusX * (minspeed + (maxspeed - minspeed) * CommonState.r.nextDouble())/10000);
					randY = (minusY * (minspeed + (maxspeed - minspeed) * CommonState.r.nextDouble())/10000);
					//randX = (minusX * ((double)(CommonState.r.nextInt((maxspeed - minspeed) + 1) + minspeed))/10000);
					//randY = (minusY * ((double)(CommonState.r.nextInt((maxspeed - minspeed) + 1) + minspeed))/10000);
					tempY = randY + coordinates.getY();
					tempX = randX + coordinates.getX();
					moveFreq.put(node.getID(), new Container(randX, randY, 1));
				}else{
					if(moveFreq.get(node.getID()).movecount < 200){
						Container newCon = moveFreq.get(node.getID());
						newCon.movecount = moveFreq.get(node.getID()).movecount+1;
						moveFreq.put(node.getID(), newCon);
						tempY = newCon.randomY + coordinates.getY();
						tempX = newCon.randomX + coordinates.getX();
					}else{

						minusY = CommonState.r.nextBoolean() ? 1 : -1;
						minusX = CommonState.r.nextBoolean() ? 1 : -1;
						randX = (minusX * (minspeed + (maxspeed - minspeed) * CommonState.r.nextDouble())/10000);
						randY = (minusY * (minspeed + (maxspeed - minspeed) * CommonState.r.nextDouble())/10000);
						tempY = randY + coordinates.getY();
						tempX = randX + coordinates.getX();
						moveFreq.put(node.getID(), new Container(randX, randY, 1));

					}
				}

				if(tempY>1){
					coordinates.setY(1);
				}else{
					coordinates.setY(tempY);
				}
				if(tempX>1){
					coordinates.setX(1);
				}else{
					coordinates.setX(tempX);
				}			
			}
		}

		for(int i=0; i<Network.size(); i++){
			// create a list of all new members for each node in the network
			Node node = Network.get(i);
			nodeP2pInfo nodeInfo = (nodeP2pInfo) node.getProtocol(p2pInfoPid);
			Linkable linkable = (Linkable) node.getProtocol(linkableId);
			List<Node> nodeList = new ArrayList<Node>();
			for(int j=0; j<Network.size(); j++){

				if (i==j) continue; 
				if(distance(Network.get(i), Network.get(j), coordinatesPid)< range){
					if(!nodeList.contains(Network.get(j))){
						nodeList.add(Network.get(j));
					}
				}
			}
			// Remove thoes previous neighbors which are not in the proximity of this node anymore			
			for(int k=0; k<linkable.degree(); k++){
				if(!nodeList.contains(linkable.getNeighbor(k))){

					// Now we have to check if the removed node has already been connectedto this Node or not if yes: the connection should be cancelled
					// We only search for clients in order to prevent conjestion in sending cancel request so only the clients will check whether they are still inside the group owner proximity or not
					// If not we should cencel the connection which will automatically put this device in the UNAVAILABLe status and send cancel request to the Group Owner
					if(nodeInfo.getStatus()==CONNECTED && !nodeInfo.isGroupOwner() && nodeInfo.getGroupOwner()==linkable.getNeighbor(k)){
						WifiP2pManager wifiManager = (WifiP2pManager) node.getProtocol(p2pmanagerId);
						wifiManager.cancelConnect();
					}
				}
			}

			// plus we check if a single node is left connected (not group owner) and at the same time it is not inside the proximity of GO
			// These phenamena is where we some some Blue (connected) node in the middle of no where
			
			if((nodeInfo.getStatus()==CONNECTED && !nodeInfo.isGroupOwner() && nodeInfo.getGroupOwner()!=null)){
				boolean groupOwnerInside = false;
				for(int k=0; k<nodeList.size(); k++){
					if(nodeList.get(k)==nodeInfo.getGroupOwner()){
						groupOwnerInside = true;
					}
				}
				if(!groupOwnerInside){
					WifiP2pManager wifiManager = (WifiP2pManager) node.getProtocol(p2pmanagerId);
					wifiManager.cancelConnect();
				}else{
					// if one should seems connected but the groupowner current group does not have this group
					nodeP2pInfo groupOwnerInfo = (nodeP2pInfo) nodeInfo.getGroupOwner().getProtocol(p2pInfoPid);
					if(!groupOwnerInfo.currentGroup.getNodeList().contains(node)){
						WifiP2pManager wifiManager = (WifiP2pManager) node.getProtocol(p2pmanagerId);
						wifiManager.cancelConnect();
					}
				}

			}

			//Now we have the new nodeList lets kill the neighbor list and put these list inside instead
			linkable.onKill();
			for(Node tempNode:nodeList){
				linkable.addNeighbor(tempNode);
			}
		}
		return false;
	}

	/**
	 * Distance.
	 *
	 * @param new_node the new_node
	 * @param old_node the old_node
	 * @param coordPid the coord pid
	 * @return the double
	 */
	// Naser: Calculating distance
	public static double distance(Node new_node, Node old_node, int coordPid) {
		double x1 = ((CoordinateKeeper) new_node.getProtocol(coordPid))
				.getX();
		double x2 = ((CoordinateKeeper) old_node.getProtocol(coordPid))
				.getX();
		double y1 = ((CoordinateKeeper) new_node.getProtocol(coordPid))
				.getY();
		double y2 = ((CoordinateKeeper) old_node.getProtocol(coordPid))
				.getY();
//		if (x1 == -1 || x2 == -1 || y1 == -1 || y2 == -1)
//			throw new RuntimeException(
//					"Found un-initialized coordinate. Use e.g., InetInitializer class in the config file." + "Node1: " + new_node.getID() + x1 + " " + y1 + " " + "Node2: " + old_node.getID() + x2 + " " + y2);
		return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
	}

	/**
	 * The Class Container.
	 */
	private class Container {
		
		/**
		 * Instantiates a new container.
		 *
		 * @param randomX the random x
		 * @param randomY the random y
		 * @param movecount the movecount
		 */
		private Container (double randomX, double randomY, int movecount){
			this.movecount = movecount;
			this.randomX = randomX;
			this.randomY = randomY;
		}

		/** The random x. */
		private double randomX = 0;
		
		/** The random y. */
		private double randomY = 0;
		
		/** The movecount. */
		private int movecount = 0;


	}

}
