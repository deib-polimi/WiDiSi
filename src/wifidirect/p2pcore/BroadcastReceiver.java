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

import java.util.EventListener;

// TODO: Auto-generated Javadoc
/**
 * The Interface BroadcastReceiver.
 * Any class which is eager to receive Wi-Fi P2P actions should implement this Interface.
 */
public interface BroadcastReceiver extends EventListener{

	/**
	 * Read the wifiP2pEvent.action to see the real action
	 * the actions could be one of the followings:
	 * 
	 * WIFI_P2P_PEERS_CHANGED_ACTION
	 * WIFI_P2P_STATE_CHANGED_ACTION
	 * WIFI_P2P_CONNECTION_CHANGED_ACTION
	 * WIFI_P2P_THIS_DEVICE_CHANGED_ACTION
	 *
	 * @param wifip2pevent the wifip2pevent
	 */
	public void onReceive(wifiP2pEvent wifip2pevent);
}
