/**
 * 
 */
package wifidirect.p2pcore;

import java.util.EventListener;

/**
 * @author Naser Derakhshan
 * 	
 * The Interface Callback. This Interface will be used by the sockets to deliver messages
 * It can deliver message regarding WiFi Direct and Wifi interface
 * any class that implements this interface should also register itself using WiFiP2pManager.registerHandler(this)
 * and wifiManager.registerHandler(this)
 *
 */
public interface Callback extends EventListener {

	/**
	 * Handle message.
	 *
	 * @param msg the msg
	 */
	public void handleMessage(callbackMessage msg);
}