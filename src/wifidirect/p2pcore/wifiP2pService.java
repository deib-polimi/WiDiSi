/*
 * 
 */
package wifidirect.p2pcore;

import java.util.HashMap;

import peersim.core.Node;

// TODO: Auto-generated Javadoc
/**
 * The Class wifiP2pService.
 */
public class wifiP2pService {
	
	/** The service name. */
	public String serviceName = "";					// Service instanceName
	
	/** The service type. */
	public String serviceType = "";					// Service registerationType
	
	/** The service record. */
	public HashMap<String, String> serviceRecord;	// Record
	
	/** The service holder address. */
	protected Node serviceHolder= null;
	
	/**
	 * Instantiates a new wifi p2p service.
	 *
	 * @param name the name
	 * @param type the type
	 * @param record the record
	 */
	public wifiP2pService (String name, String type, HashMap<String, String> record){
		serviceName = name;
		serviceType = type;
		serviceRecord = new HashMap<String, String>(record);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "wifiP2pService [serviceName=" + serviceName + ", serviceType="
				+ serviceType + ", serviceRecord=" + serviceRecord + "]";
	}
}
