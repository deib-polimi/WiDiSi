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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

// TODO: Auto-generated Javadoc
/**
 * The Class WifiP2pDeviceList.
 */
public class WifiP2pDeviceList {


    /** The m devices. */
    private final HashMap<String, WifiP2pDevice> mDevices = new HashMap<String, WifiP2pDevice>();

    /**
     * Instantiates a new wifi p2p device list.
     */
    public WifiP2pDeviceList() {
    }

    /**
     *  copy constructor.
     *
     * @param source the source
     */
    public WifiP2pDeviceList(WifiP2pDeviceList source) {
        if (source != null) {
            for (WifiP2pDevice d : source.getDeviceList()) {
                mDevices.put(d.deviceAddress, new WifiP2pDevice(d));
            }
        }
    }

    /**
     * Instantiates a new wifi p2p device list.
     *
     * @param devices the devices
     * @hide 
     */
    public WifiP2pDeviceList(ArrayList<WifiP2pDevice> devices) {
        for (WifiP2pDevice device : devices) {
            if (device.deviceAddress != null) {
                mDevices.put(device.deviceAddress, new WifiP2pDevice(device));
            }
        }
    }

    /**
     *  Clear the list @hide.
     *
     * @return true, if successful
     */
    public boolean clear() {
        if (mDevices.isEmpty()) return false;
        mDevices.clear();
        return true;
    }

    /**
     * Add/update a device to the list. If the device is not found, a new device entry
     * is created. If the device is already found, the device details are updated
     * @param device to be updated
     * @hide
     */
    public void update(WifiP2pDevice device) {
        updateSupplicantDetails(device);
        mDevices.get(device.deviceAddress).status = device.status;
    }

    /**
     *  Only updates details fetched from the supplicant @hide.
     *
     * @param device the device
     */
    void updateSupplicantDetails(WifiP2pDevice device) {
        WifiP2pDevice d = mDevices.get(device.deviceAddress);
        if (d != null) {
            d.deviceName = device.deviceName;
            d.primaryDeviceType = device.primaryDeviceType;
            d.secondaryDeviceType = device.secondaryDeviceType;
            d.wpsConfigMethodsSupported = device.wpsConfigMethodsSupported;
            d.deviceCapability = device.deviceCapability;
            d.groupCapability = device.groupCapability;
            return;
        }
        //Not found, add a new one
        mDevices.put(device.deviceAddress, device);
    }

    /**
     * Fetch a device from the list.
     *
     * @param deviceAddress is the address of the device
     * @return WifiP2pDevice device found, or null if none found
     */
    public WifiP2pDevice get(String deviceAddress) {
        return mDevices.get(deviceAddress);
    }

    /**
     * Removes the.
     *
     * @param device the device
     * @return true, if successful
     * @hide 
     */
    public boolean remove(WifiP2pDevice device) {
        return mDevices.remove(device.deviceAddress) != null;
    }

    /**
     * Remove a device from the list.
     *
     * @param deviceAddress is the address of the device
     * @return WifiP2pDevice device removed, or null if none removed
     * @hide 
     */
    public WifiP2pDevice remove(String deviceAddress) {
        return mDevices.remove(deviceAddress);
    }

    /**
     *  Returns true if any device the list was removed @hide.
     *
     * @param list the list
     * @return true, if successful
     */
    public boolean remove(WifiP2pDeviceList list) {
        boolean ret = false;
        for (WifiP2pDevice d : list.mDevices.values()) {
            if (remove(d)) ret = true;
        }
        return ret;
    }

    /**
     *  Get the list of devices.
     *
     * @return the device list
     */
    public Collection<WifiP2pDevice> getDeviceList() {
        return Collections.unmodifiableCollection(mDevices.values());
    }

    /**
     * Checks if is group owner.
     *
     * @param deviceAddress the device address
     * @return true, if is group owner
     * @hide 
     */
    public boolean isGroupOwner(String deviceAddress) {
        WifiP2pDevice device = mDevices.get(deviceAddress);
        if (device == null) {
            throw new IllegalArgumentException("Device not found " + deviceAddress);
        }
        return device.isGroupOwner();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer sbuf = new StringBuffer();
        for (WifiP2pDevice device : mDevices.values()) {
            sbuf.append("\n").append(device);
        }
        return sbuf.toString();
    }
}
