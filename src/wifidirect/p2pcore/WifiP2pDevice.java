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

import peersim.core.Node;
// TODO: Auto-generated Javadoc

/**
 * The Class WifiP2pDevice.
 */
/*
 * A class representing a Wi-Fi p2p device
 *
 */
public class WifiP2pDevice {

	/** The simulator node. */
	protected Node simulatorNode;
    
    /** The device name is a user friendly string to identify a Wi-Fi p2p device. */
    public String deviceName = "";

    /** The device MAC address uniquely identifies a Wi-Fi p2p device. */
    public String deviceAddress = "";

    /**
     * Primary device type identifies the type of device. For example, an application
     * could filter the devices discovered to only display printers if the purpose is to
     * enable a printing action from the user. See the Wi-Fi Direct technical specification
     * for the full list of standard device types supported.
     */
    public String primaryDeviceType;

    /**
     * Secondary device type is an optional attribute that can be provided by a device in
     * addition to the primary device type.
     */
    public String secondaryDeviceType;


    /**
     * WPS config methods supported.
     *
     * @hide 
     */
    public int wpsConfigMethodsSupported;

    /**
     * Device capability.
     *
     * @hide 
     */
    public int deviceCapability;

    /**
     * Group capability.
     *
     * @hide 
     */
    public int groupCapability;

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

    /**  Device connection status. */
    public int status = UNAVAILABLE;
    
    /** The node info. */
    private nodeP2pInfo nodeInfo;

    private int thisPid;
    /**
     * Instantiates a new wifi p2p device.
     *
     * @param node the node
     * @param p2pInfoPid the p2p info pid
     */
    public WifiP2pDevice(Node node, int p2pInfoPid) {
    	simulatorNode = node;
    	thisPid = p2pInfoPid;
    	nodeInfo = (nodeP2pInfo) node.getProtocol(p2pInfoPid);
    	deviceAddress = nodeInfo.getMacAddress();
    	primaryDeviceType = "Mobile";
    	deviceName = nodeInfo.getDeviceName();
    	groupCapability = WifiP2pGroup.groupCapacity;
    	status = nodeInfo.getStatus();
    	
    }
    
    /**
     *  Returns true if WPS push button configuration is supported.
     *
     * @return true, if successful
     */
    public boolean wpsPbcSupported() {
        return true;
    }

    /**
     *  Returns true if WPS keypad configuration is supported.
     *
     * @return true, if successful
     */
    public boolean wpsKeypadSupported() {
        return false;
    }

    /**
     *  Returns true if WPS display configuration is supported.
     *
     * @return true, if successful
     */
    public boolean wpsDisplaySupported() {
        return false;
    }

    /**
     *  Returns true if the device is capable of service discovery.
     *
     * @return true, if is service discovery capable
     */
    public boolean isServiceDiscoveryCapable() {
        return true;
    }

    /**
     *  Returns true if the device is capable of invitation {@hide}.
     *
     * @return true, if is invitation capable
     */
    public boolean isInvitationCapable() {
        return true;
    }

    /**
     *  Returns true if the device reaches the limit. {@hide}
     *
     * @return true, if is device limit
     */
    public boolean isDeviceLimit() {
        return false;
    }

    /**
     *  Returns true if the device is a group owner.
     *
     * @return true, if is group owner
     */
    public boolean isGroupOwner() {
    	if(nodeInfo!=null){
    		return nodeInfo.isGroupOwner();
    	}else{
    		return false;
    	}
    }

    /**
     *  Returns true if the group reaches the limit. {@hide}
     *
     * @return true, if is group limit
     */
    public boolean isGroupLimit() {
    	if(nodeInfo!=null){
    		if(nodeInfo.currentGroup!=null){
    			return nodeInfo.currentGroup.getGroupSize() < WifiP2pGroup.groupCapacity;
    		}else {
    			return false;
    		}
    	}else{
    		return false;
    	}
    }

    /**
     * Update device details. This will be throw an exception if the device address
     * does not match.
     * @param device to be updated
     * @throws IllegalArgumentException if the device is null or device address does not match
     * @hide
     */
    public void update(WifiP2pDevice device) {
        updateSupplicantDetails(device);
        status = device.status;
    }

    /**
     *  Updates details obtained from supplicant @hide.
     *
     * @param device the device
     */
    void updateSupplicantDetails(WifiP2pDevice device) {
        if (device == null) {
            throw new IllegalArgumentException("device is null");
        }
        if (device.deviceAddress == null) {
            throw new IllegalArgumentException("deviceAddress is null");
        }
        if (!deviceAddress.equals(device.deviceAddress)) {
            throw new IllegalArgumentException("deviceAddress does not match");
        }
        deviceName = device.deviceName;
        primaryDeviceType = device.primaryDeviceType;
        secondaryDeviceType = device.secondaryDeviceType;
        wpsConfigMethodsSupported = device.wpsConfigMethodsSupported;
        deviceCapability = device.deviceCapability;
        groupCapability = device.groupCapability;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof WifiP2pDevice)) return false;

        WifiP2pDevice other = (WifiP2pDevice) obj;
        if (other == null || other.deviceAddress == null) {
            return (deviceAddress == null);
        }
        return other.deviceAddress.equals(deviceAddress);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer sbuf = new StringBuffer();
        sbuf.append("Device: ").append(deviceName);
        sbuf.append("\n deviceAddress: ").append(deviceAddress);
        sbuf.append("\n primary type: ").append(primaryDeviceType);
        sbuf.append("\n secondary type: ").append(secondaryDeviceType);
        sbuf.append("\n wps: ").append(wpsConfigMethodsSupported);
        sbuf.append("\n grpcapab: ").append(groupCapability);
        sbuf.append("\n devcapab: ").append(deviceCapability);
        sbuf.append("\n status: ").append(status);
        return sbuf.toString();
    }
    
    /**
     *  copy constructor.
     *
     * @param source the source
     */
    public WifiP2pDevice(WifiP2pDevice source) {
        if (source != null) {
        	nodeInfo = (nodeP2pInfo) source.simulatorNode.getProtocol(source.thisPid);
            deviceName = source.deviceName;
            deviceAddress = source.deviceAddress;
            primaryDeviceType = source.primaryDeviceType;
            secondaryDeviceType = source.secondaryDeviceType;
            wpsConfigMethodsSupported = source.wpsConfigMethodsSupported;
            deviceCapability = source.deviceCapability;
            groupCapability = source.groupCapability;
            status = source.status;
        }
    }
}
