/*
 * 
 */
package wifidirect.p2pcore;

import java.util.EventListener;

// TODO: Auto-generated Javadoc
/**
 * The Interface BroadcastReceiver.
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
