/*
 * 
 */
package wifidirect.p2pcore;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;
import peersim.dynamics.NodeInitializer;

// TODO: Auto-generated Javadoc
/**
 * The Class nodeInitializer.
 */
public class nodeInitializer implements NodeInitializer, Control{

	/** The Constant PAR_PROT_1. */
	private static final String PAR_PROT_1 = "protocol_1";    // NodeP2P Info set in the configuration
	
	/** The pid1. */
	public static int nodeInfoPid;
	
	
	/** The Constant PAR_PROT_3. */
	private static final String PAR_PROT_3 = "protocol_3";  // wifip2pmanager set in the configuration
	
	/** The pid3. */
	public static int p2pManagerPid;
	
	
	/**
	 * Instantiates a new node initializer.
	 *
	 * @param prefix the prefix
	 */
	public nodeInitializer (String prefix){
		nodeInfoPid = Configuration.getPid(prefix + "." + PAR_PROT_1);
		p2pManagerPid = Configuration.getPid(prefix + "." + PAR_PROT_3);
	}
	/* (non-Javadoc)
	 * @see peersim.dynamics.NodeInitializer#initialize(peersim.core.Node)
	 */
	public void initialize(Node n) {		
		nodeP2pInfo p2pInfo;

		p2pInfo = (nodeP2pInfo) n.getProtocol(nodeInfoPid);
		p2pInfo.setBatteryLevel(CommonState.r.nextInt(100));
		p2pInfo.setGoIntentionValue(CommonState.r.nextInt(13)+2);
		p2pInfo.setMemoryRemained(CommonState.r.nextInt(90)+10);
		p2pInfo.setProcessingCap(CommonState.r.nextInt(90)+10);
		p2pInfo.thisNode = n;
		nodeP2pInfo.thisPid = nodeInfoPid;		
		
		WifiP2pManager p2pmanager = (WifiP2pManager) n.getProtocol(p2pManagerPid);
		p2pmanager.thisNode = n;
		p2pmanager.thisPid = p2pManagerPid;
		p2pmanager.setDeviceName("Device_" + n.getID());
	}
	// Control Interface implementation. This method will be executed Network.size() times at the beggining of network formation
	// to initialize the nodes. It would execute after that. instead for each new node which will be added later by Dynamics class
	// the above mentioned method will be excuted once to initialize the node
	/* (non-Javadoc)
	 * @see peersim.core.Control#execute()
	 */
	public boolean execute() {
		Node node;
		nodeP2pInfo p2pInfo;
		for (int i =0; i<Network.size(); i++){
			node=Network.get(i);
			
			p2pInfo = (nodeP2pInfo) node.getProtocol(nodeInfoPid);
			p2pInfo.setBatteryLevel(CommonState.r.nextInt(100));
			p2pInfo.setGoIntentionValue(CommonState.r.nextInt(13)+2);
			p2pInfo.setMemoryRemained(CommonState.r.nextInt(90)+10);
			p2pInfo.setProcessingCap(CommonState.r.nextInt(90)+10);
			p2pInfo.thisNode = node;
			nodeP2pInfo.thisPid = nodeInfoPid;
			
			WifiP2pManager p2pmanager = (WifiP2pManager) node.getProtocol(p2pManagerPid);
			p2pmanager.thisNode = node;
			p2pmanager.thisPid = p2pManagerPid;
			p2pmanager.setDeviceName("Device_" + node.getID());	
		}
		return false;
	}	
}
