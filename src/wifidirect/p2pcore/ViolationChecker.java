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
import java.util.List;

import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Linkable;
import peersim.core.Network;
import peersim.core.Node;
import visualization.Visualizer;
import wifidirect.p2pcore.WifiP2pManager.*;


// TODO: Auto-generated Javadoc
/**
 * The Class ViolationChecker.
 */
public class ViolationChecker implements Control, PeerListListener, GroupInfoListener, ConnectionInfoListener{

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


	/** The Constant PAR_P2PINFO. */
	private static final String PAR_P2PINFO = "p2pinfo";
	
	/** The Constant PAR_LINKABLE. */
	private static final String PAR_LINKABLE = "linkable";
	
	/** The Constant PAR_LISTENER. */
	private static final String PAR_LISTENER = "listeners";
	
	/** The Constant PAR_MANAGE. */
	private static final String PAR_MANAGE = "p2pmanager";

	/** The rule2 node list. */
	private List<Node> rule2NodeList = new ArrayList<Node>();
	
	/** The rule7 node list. */
	private List<Node> rule7NodeList = new ArrayList<Node>();

	/** The listener pid. */
	private int listenerPid;
	
	/** The p2p info pid. */
	private int p2pInfoPid;
	
	/** The linkable id. */
	private int linkableId;
	
	/** The p2pmanager id. */
	private int p2pmanagerId;

	/** The cycle. */
	private long cycle=0;
	
	/**
	 * Instantiates a new violation checker.
	 *
	 * @param prefix the prefix
	 */
	public ViolationChecker (String prefix){
		p2pInfoPid = Configuration.getPid(prefix + "." + PAR_P2PINFO);
		linkableId = Configuration.getPid(prefix + "." + PAR_LINKABLE);
		listenerPid = Configuration.getPid(prefix + "." + PAR_LISTENER);
		p2pmanagerId 	= Configuration.getPid(prefix + "." + PAR_MANAGE);
	}

	/* (non-Javadoc)
	 * @see peersim.core.Control#execute()
	 */
	@Override
	public boolean execute() {
		if(cycle==1){
			// registering this listener in all nodes
			for (int i=0; i<Network.size(); i++){
				eventListeners listener = (eventListeners) Network.get(i).getProtocol(listenerPid);
				listener.addPeerListListener(this);
				listener.addGroupInfoListener(this);
				listener.addConInfoListener(this);
			}
		}else if(cycle%10==0 && cycle>1){
			for(int i=0; i<Network.size(); i++){
				Linkable neighbor = (Linkable) Network.get(i).getProtocol(linkableId);
				List<Node> neighborList = new ArrayList<Node>();
				for(int j=0; j<neighbor.degree(); j++){
					neighborList.add(neighbor.getNeighbor(j));
				}
				nodeP2pInfo nodeInfo = (nodeP2pInfo) Network.get(i).getProtocol(p2pInfoPid);

				// rule one: 	1.	A peer cannot connect to another peer outside its proximity range
				// rule two: 	2.	A group owner cannot connect to another group 
				// rule seven: 	7.	A peer cannot be group owner and client at the same time 
				// rule eight:	8.	A group cannot consist of more than M peers
				// rule ten:	10.	A Client cannot communicate with other client in the same group directly (bypassing GO)
				if (nodeInfo.getStatus()==CONNECTED){
					if(nodeInfo.isGroupOwner()){
						for (Node cNode: nodeInfo.currentGroup.getNodeList()){
//							// rule 1 (we do not check rule one on group owners since it takes time for cancel connect to take effect and we are checking this fact every cycle)
//							if (!neighborList.contains(cNode)){
//								Visualizer.rulesText1.setText(String.valueOf(Integer.parseInt(Visualizer.rulesText1.getText())+1));
//							}

							//rule 2
							nodeP2pInfo cInfo = (nodeP2pInfo) cNode.getProtocol(p2pInfoPid);
							if(cInfo.isGroupOwner() && !rule2NodeList.contains(cNode)){
								rule2NodeList.add(cNode);
								//Visualizer.print("rules 2 violation. Node: " + cNode.getID() + " is still available at client List of Node: " + Network.get(i).getID());
								Visualizer.mDwindow.ruleTwoCheck.setText(String.valueOf(Integer.parseInt(Visualizer.mDwindow.ruleTwoCheck.getText())+1));
							}
							
							//rule 10
//							WifiP2pManager manager = (WifiP2pManager) cNode.getProtocol(p2pmanagerId);
//							for(Node cinNode: nodeInfo.currentGroup.getNodeList()){
//								if(cNode.getID()!=cinNode.getID() && cinNode.getID()!=Network.get(i).getID() && cNode.getID()!=Network.get(i).getID()){
//									if(manager.send(null, String.valueOf(cinNode.getID())).equals("Message Sent!")){
//										Visualizer.rulesText10.setText(String.valueOf(Integer.parseInt(Visualizer.rulesText10.getText())+1));
//										Visualizer.print("Violation rule 10: A message sent from Node: " +  cNode.getID() + " to the Node: " + 
//										cinNode.getID() + " and the group owner is: " + Network.get(i).getID());
//									}
//								}
//							}
						}
						//rule 7
						for(int k=0; k<Network.size(); k++){
							if (k==i) continue;
							nodeP2pInfo kInfo = (nodeP2pInfo) Network.get(k).getProtocol(p2pInfoPid);
							if(kInfo.isGroupOwner() && kInfo.currentGroup.getNodeList().contains(Network.get(i)) && !rule7NodeList.contains(Network.get(k))){
								Visualizer.mDwindow.ruleSevenCheck.setText(String.valueOf(Integer.parseInt(Visualizer.mDwindow.ruleSevenCheck.getText())+1));
								rule7NodeList.add(Network.get(k));
							}
						}
						
						//rule 8
						if(nodeInfo.currentGroup.getGroupSize()>WifiP2pGroup.groupCapacity){
							Visualizer.mDwindow.ruleEightCheck.setText(String.valueOf(Integer.parseInt(Visualizer.mDwindow.ruleEightCheck.getText())+1));
						}
						
					}else{

						// rule 1
						if(!neighborList.contains(nodeInfo.getGroupOwner())){
							Visualizer.mDwindow.ruleOneCheck.setText(String.valueOf(Integer.parseInt(Visualizer.mDwindow.ruleOneCheck.getText())+1));
						}	

						//rule 3 This Node is a Client of another device which is not Group Owner
						nodeP2pInfo gInfo = (nodeP2pInfo) nodeInfo.getGroupOwner().getProtocol(p2pInfoPid);
						if(!gInfo.isGroupOwner()){
							Visualizer.mDwindow.ruleThreeCheck.setText(String.valueOf(Integer.parseInt(Visualizer.mDwindow.ruleThreeCheck.getText())+1));
						}
					}
				}
			}
		}
		cycle++;
		return false;
	}

//	/* (non-Javadoc)
//	 * @see java.lang.Object#clone()
//	 */
//	public ViolationChecker clone(){
//		ViolationChecker vc = null;
//		try { vc = (ViolationChecker) super.clone(); }
//		catch( CloneNotSupportedException e ) {} // never happen
//		vc.p2pInfoPid = p2pInfoPid;
//		vc.linkableId = linkableId;
//		vc.listenerPid = listenerPid;
//		return vc;	
//	}

	/* (non-Javadoc)
	 * @see peersim.wifidirect.p2pcore.WifiP2pManager.PeerListListener#onPeersAvailable(peersim.wifidirect.p2pcore.WifiP2pDeviceList)
	 */
	@Override
	public void onPeersAvailable(WifiP2pDeviceList peers) {
		
		// rule 4: 4.	A peer cannot see more than X number of devices and services
		if(peers.getDeviceList().size()>nodeP2pInfo.maxNumDevices){
			Visualizer.mDwindow.ruleFourCheck.setText(String.valueOf(Integer.parseInt(Visualizer.mDwindow.ruleFourCheck.getText())+1));
		}
		
		//rule 6: 6.	A peer cannot discover other peers or services if they have not started peer discovery
		List<Node> peerNodeList = new ArrayList<Node>();
		List<Long> nodeIdList = new ArrayList<Long>();
		for (WifiP2pDevice p2pDevice: peers.getDeviceList()){
			nodeIdList.add(Long.parseLong(p2pDevice.deviceAddress));
		}
		
		for(int i=0; i<Network.size(); i++){
			if (nodeIdList.contains(Network.get(i).getID())){
				peerNodeList.add(Network.get(i));
			}
		}
		
		for(Node peerNode: peerNodeList){
			nodeP2pInfo peerInfo = (nodeP2pInfo) peerNode.getProtocol(p2pInfoPid);
			if(!peerInfo.isPeerDiscoveryStarted()){
				Visualizer.mDwindow.ruleSixCheck.setText(String.valueOf(Integer.parseInt(Visualizer.mDwindow.ruleSixCheck.getText())+1));
			}
		}
		
	}

	/* (non-Javadoc)
	 * @see peersim.wifidirect.p2pcore.WifiP2pManager.GroupInfoListener#onGroupInfoAvailable(peersim.wifidirect.p2pcore.WifiP2pGroup)
	 */
	@Override
	public void onGroupInfoAvailable(WifiP2pGroup groupInfo) {
		
		
	}

	/* (non-Javadoc)
	 * @see peersim.wifidirect.p2pcore.WifiP2pManager.ConnectionInfoListener#onConnectionInfoAvailable(peersim.wifidirect.p2pcore.WifiP2pInfo)
	 */
	@Override
	public void onConnectionInfoAvailable(WifiP2pInfo wifiInfo) {
		
	}

}
