/*
 * 
 */
package wifidirect.p2pcore;

import peersim.core.Node;

// TODO: Auto-generated Javadoc
/**
 * The Class WifiP2pInfo.
 */
public class WifiP2pInfo {

	 /**  Indicates if a p2p group has been successfully formed. */
    public boolean groupFormed;

    /**  Indicates if the current device is the group owner. */
    public boolean isGroupOwner;

    /**  Group owner address. */
    public Node groupOwnerAddress;

    /**
     * Instantiates a new wifi p2p info.
     *
     * @param groupFormed the group formed
     * @param isGroupOwner the is group owner
     * @param groupOwnerAddress the group owner address
     */
    public WifiP2pInfo(boolean groupFormed, boolean isGroupOwner, Node groupOwnerAddress) {
    	this.groupFormed = groupFormed;
    	this.isGroupOwner = isGroupOwner;
    	this.groupOwnerAddress = groupOwnerAddress;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer sbuf = new StringBuffer();
        sbuf.append("groupFormed: ").append(groupFormed)
            .append(" isGroupOwner: ").append(isGroupOwner)
            .append(" groupOwnerAddress: ").append(groupOwnerAddress);
        return sbuf.toString();
    }    
    
}
