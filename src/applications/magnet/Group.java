package applications.magnet;

import java.util.ArrayList;
import java.util.HashMap;

import wifidirect.p2pcore.WifiP2pDevice;


public class Group {

	private int groupID=0;
	private HashMap<String, String> record = new HashMap<String, String>();
	private String groupName = null;
	private String groupSSID = null;
	private String groupPassPhrase = null;
	private ArrayList<WifiP2pDevice> groupedPeerList = new ArrayList<WifiP2pDevice>();
	private String groupOwnerAddress = null;

	private boolean groupValid = false;
	/**
	 * @return the groupID
	 */
	public int getGroupID() {
		return groupID;
	}
	/**
	 * @param groupID the groupID to set
	 */
	public void setGroupID(int groupID) {
		this.groupID = groupID;
	}
	/**
	 * @return the recordGroup
	 */
	public HashMap<String, String> getRecord() {
		return record;
	}
	/**
	 * @param recordGroup the recordGroup to set
	 */
	public void setRecord(HashMap<String, String> record) {
		this.record = record;
	}
	/**
	 * @return the groupName
	 */
	public String getGroupName() {
		return groupName;
	}
	/**
	 * @param groupName the groupName to set
	 */
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	/**
	 * @return the groupedPeerList
	 */
	public ArrayList<WifiP2pDevice> getGroupedPeerList() {
		return groupedPeerList;
	}
	/**
	 * @param groupedPeerList the groupedPeerList to set
	 */
	public void setGroupedPeerList(ArrayList<WifiP2pDevice> groupedPeerList) {
		this.groupedPeerList = groupedPeerList;
	}
	public void resetGroup (){
		groupID = 0;
		groupName = null;
		record.clear();
		groupedPeerList.clear();
	}
	/**
	 * @return the groupSSID
	 */
	public String getGroupSSID() {
		return groupSSID;
	}
	/**
	 * @param groupSSID the groupSSID to set
	 */
	public void setGroupSSID(String groupSSID) {
		this.groupSSID = groupSSID;
	}
	/**
	 * @return the groupPassPhrase
	 */
	public String getGroupPassPhrase() {
		return groupPassPhrase;
	}
	/**
	 * @param groupPassPhrase the groupPassPhrase to set
	 */
	public void setGroupPassPhrase(String groupPassPhrase) {
		this.groupPassPhrase = groupPassPhrase;
	}
	public boolean isGroupValid() {
		return groupValid;
	}
	public void setGroupValid(boolean groupValid) {
		this.groupValid = groupValid;
	}
	public String getGroupOwnerAddress() {
		return groupOwnerAddress;
	}
	public void setGroupOwnerAddress(String groupOwnerAddress) {
		this.groupOwnerAddress = groupOwnerAddress;
	}
}
