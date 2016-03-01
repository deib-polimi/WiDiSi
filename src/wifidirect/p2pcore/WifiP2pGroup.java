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

/*
 * A class that contain Group Information
 * In a group, only Group Owner create an object of this class and keep it updated
 * other clients may request for this object by calling requestGroupInfo()
 */
package wifidirect.p2pcore;

import java.util.ArrayList;

import peersim.core.Node;


// TODO: Auto-generated Javadoc
/**
 * The Class WifiP2pGroup.
 */
public class WifiP2pGroup {
	
	/** SSID is equal to the mInterface */
	public String SSID;
	
	/** BSSID is the MAC address */
	public String BSSID;

	// Fields related to group Info
	/** The node list. */
	private ArrayList<Node> nodeList;
	
	/** The group owner. */
	private Node groupOwner;
	
	/** The group valid. */
	private boolean groupValid;
	
	/** The Constant groupCapacity. */
	public static final int groupCapacity = 8; 
	
    /**  The passphrase used for WPA2-PSK. */
    private String mPassphrase;

    /** The m interface. */
    private String mInterface;

    /**  The network id in the wpa_supplicant. */
    private int mNetId;
    
	
	/**
	 * Instantiates a new wifi p2p group.
	 */
	public WifiP2pGroup(){
		nodeList = new ArrayList<Node>();
		setGroupValid(false);
		setGroupOwner(null);
	}

	/**
	 * Checks if is group valid.
	 *
	 * @return true, if is group valid
	 */
	public boolean isGroupValid() {
		return groupValid;
	}

	/**
	 * Sets the group valid.
	 *
	 * @param groupValid the new group valid
	 */
	public void setGroupValid(boolean groupValid) {
		this.groupValid = groupValid;
	}

	/**
	 * Gets the group owner.
	 *
	 * @return the group owner
	 */
	public Node getGroupOwner() {
		return groupOwner;
	}

	/**
	 * Sets the group owner.
	 *
	 * @param groupOwner the new group owner
	 */
	public void setGroupOwner(Node groupOwner) {
		this.groupOwner = groupOwner;
	}
	
	/**
	 * Gets the node list.
	 *
	 * @return the nodeList
	 */
	public ArrayList<Node> getNodeList() {
		return nodeList;
	}

	/**
	 * Adds the node.
	 *
	 * @param node the node
	 */
	public void addNode(Node node) {
		if(nodeList.size()<groupCapacity){
			boolean tempB = true;
			for(Node tempNode:nodeList){
				if(tempNode.getID()==node.getID()){
					tempB=false;
				}
			}
			if(tempB){
				nodeList.add(node);
			}
		}
	}
	
	/**
	 * Removes the node.
	 *
	 * @param node the node
	 */
	public void removeNode(Node node) {
		ArrayList<Node> tempList = new ArrayList<Node>();
		tempList.addAll(nodeList);
		for(Node tempNode:nodeList){
			if(tempNode.getID()==node.getID()){
				tempList.remove(tempNode);
			}
		}
		nodeList.clear();
		nodeList.addAll(tempList);
	}
	
	/**
	 * Reset group.
	 */
	public void resetGroup(){
		nodeList.clear();
		groupOwner = null;
		groupValid = false;
		setmPassphrase(null);
		setmInterface(null);
		setmNetId(0);
	}

	/**
	 * Gets the group size.
	 *
	 * @return the group size
	 */
	public int getGroupSize() {
		return nodeList.size();
	}

	/**
	 * Gets the m passphrase.
	 *
	 * @return the m passphrase
	 */
	public String getmPassphrase() {
		return mPassphrase;
	}

	/**
	 * Sets the m passphrase.
	 *
	 * @param mPassphrase the new m passphrase
	 */
	public void setmPassphrase(String mPassphrase) {
		this.mPassphrase = mPassphrase;
	}

	/**
	 * Gets the m interface.
	 *
	 * @return the m interface
	 */
	public String getmInterface() {
		return mInterface;
	}

	/**
	 * Sets the m interface.
	 *
	 * @param mInterface the new m interface
	 */
	public void setmInterface(String mInterface) {
		this.mInterface = mInterface;
		SSID = mInterface;
	}

	/**
	 * Gets the m net id.
	 *
	 * @return the m net id
	 */
	public int getmNetId() {
		return mNetId;
	}

	/**
	 * Sets the m net id.
	 *
	 * @param mNetId the new m net id
	 */
	public void setmNetId(int mNetId) {
		this.mNetId = mNetId;
	}
	
/**
 * SSID is the name of the network	
 * BSSID is the MAC address of the Access Point which in our case is the node ID
 * @return
 */
	public String getSSID(){
		return mInterface;
	}


}
