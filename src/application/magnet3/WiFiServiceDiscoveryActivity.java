package application.magnet3;
//Magnet 3
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import applications.magnet.Routing.Constants;
import applications.magnet.Routing.MacAddress;
import applications.magnet.Routing.MessageBytes;
import applications.magnet.Routing.NetAddress;
import applications.magnet.Routing.NetMessage;
import applications.magnet.Routing.RouteAodv;
import applications.magnet.Routing.eventMessage;
import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDProtocol;
import peersim.transport.Transport;
import visualization.Visualizer;
import wifi.ScanResult;
import wifi.WifiConfiguration;
import wifi.WifiManager;
import wifidirect.nodemovement.NodeMovement;
import wifidirect.p2pcore.BroadcastReceiver;
import wifidirect.p2pcore.Callback;
import wifidirect.p2pcore.WifiP2pConfig;
import wifidirect.p2pcore.WifiP2pDevice;
import wifidirect.p2pcore.WifiP2pDeviceList;
import wifidirect.p2pcore.WifiP2pGroup;
import wifidirect.p2pcore.WifiP2pInfo;
import wifidirect.p2pcore.WifiP2pManager;
import wifidirect.p2pcore.WifiP2pManager.ConnectionInfoListener;
import wifidirect.p2pcore.WifiP2pManager.DnsSdServiceResponseListener;
import wifidirect.p2pcore.WifiP2pManager.DnsSdTxtRecordListener;
import wifidirect.p2pcore.WifiP2pManager.GroupInfoListener;
import wifidirect.p2pcore.WifiP2pManager.PeerListListener;
import wifidirect.p2pcore.callbackMessage;
import wifidirect.p2pcore.nodeP2pInfo;
import wifidirect.p2pcore.wifiP2pEvent;
import wifidirect.p2pcore.wifiP2pService;

/**
 * The main activity for the sample. This activity registers a local service and
 * perform discovery over Wi-Fi p2p network. It also hosts a couple of fragments
 * to manage chat operations. When the app is launched, the device publishes a
 * chat service and also tries to discover services published by other peers. On
 * selecting a peer published service, the app initiates a Wi-Fi P2P (Direct)
 * connection with the peer. On successful connection with a peer advertising
 * the same service, the app opens up sockets to initiate a chat.
 * {@code WiFiChatFragment} is then added to the the main activity which manages
 * the interface and messaging needs for a chat session.
 */
public class WiFiServiceDiscoveryActivity implements EDProtocol, CDProtocol,
ConnectionInfoListener, DnsSdServiceResponseListener, DnsSdTxtRecordListener, PeerListListener, Callback,GroupInfoListener, 
BroadcastReceiver{

	public static final String 	SERVICE_REG_TYPE 		= "_presence._tcp"; // this is registeration type for services

	public static final int 	MESSAGE_READ 			= 0x400 + 1;		// Reserved Code for Message Handler 
	public static final int 	MY_HANDLE 				= 0x400 + 2;		// Reserved Code for Message Handler
	public static final int 	OBJECT_READ 			= 0x400 + 3;		// Reserved Code for Message Handler 
	public static final int     ALCON_APLIST_RESPONSE 	= 0x400 + 4;		// Reserved Code for Message Handler 
	public static final int     ALCON_APLIST_REQUEST	= 0x400 + 5;		// Reserved Code for Message Handler 
	public static final int		MESSAGE_TEST			= 0x400 + 6;		// Reserved Code for Message Handler 
	public static final int		REQUEST_AP_SEEN			= 0x400 + 7;        // Reserved Code for Message Handler 
	public static final int		CLIENT_AP_LIST			= 0x400 + 8;		// Reserved Code for Message Handler 
	public static final int		REQUEST_CONNECT_AP		= 0x400 + 9;		// Reserved Code for Message Handler
	public static final int		ROUTING_MESSAGE			= 0x400 + 10;		// Reserved Code for Message Handler
	public static final int		GO_SSID_RESPONSE		= 0x400 + 11;		// Reserved Code for Message Handler
	public static final int		REQUEST_SSID			= 0x400 + 12;		// Reserved Code for Message Handler

	private static final int	wTimeForWTA				= 10;				// period (seconds) in which the GO decide a new combination based on new clients added or removed
	private boolean				startWTAClaculation;
	public static final int 	waitcoeff 				= 300;				// Waiting time will be multiply by this coefficient (milliseconds)
	public static final int 	SERVER_PORT 			= 4545;				// Server port is the same in GroupOwnerSocketHandler
	private Node   thisNode;
	public String AbstractState = "";

	// None constant variables
	public 	int 		count;
	public 	int 		groupCapacity;		// will be increamented by one for each new peer in the group
	public 	boolean 	discoveryFlag;	
	public 	boolean 	isWifiP2pEnabled;	// would be set to true by Broadcast receiver when the wifi P2P enabled
	public 	boolean 	isGroupOwner;		// would be set to true by ConnectionInfoListener when connection info avilable
	public 	boolean 	isConnected;    	// would be set to true by Broadcast receiver when the device state changed
	private boolean 	SMARTSelected;
	public 	double   	intention;			// The intention of this device. we may set it once to save memory instead of calling getIntention() method several times
	private boolean     appTerminate;
	private boolean     isGroupFormed;
	private boolean     apListRequested;
	private	int			groupID;
	private boolean		groupRecordUpdated;
	private boolean		alreadyConnected;
	private String		p2pMacAddress;
	public  String		goMacAddress;		

	//Device Status
	public static final int CONNECTED   = 0;
	public static final int INVITED     = 1;
	public static final int FAILED      = 2;
	public static final int AVAILABLE   = 3;
	public static final int UNAVAILABLE = 4;

	// Objetc definitions
	private 		WifiP2pManager 				manager;
	private			WifiManager 				wifiManager;
	private 	 	WifiP2pConfig 				config; 			  		// configuration of wifi p2p. here for WPS configuration
	private			Group						newGroup;					// The current group if this device is GO. Otherwise it will return null
	private 		wifiP2pService			 	serviceGroup;
	private 		nodeP2pInfo					nodeInfo;
	private 		long						delayHandler1, delayHandler2, delayHandler3, delayHandler4;
	private 		boolean						delayHandler1Started, delayHandler2Started, delayHandler3Started, delayHandler4Started;

	// Collections									
	private HashMap<String, Double> 			intentionList;  			// Intention	<=>	Device Mac Address
	private HashMap<String, String> 			serviceList;  				// Device Mac Address <=> Service Name
	//private HashMap<String, String> 			groupList;  				// Group ID   <=> Mac Address of GO
	private ArrayList<WifiP2pDevice> 			peerList; 					// WifiP2pDevice of peers found in the proximity
	private ArrayList<WifiP2pDevice> 			groupedPeerList; 			// WifiP2pDevice of peers in the group  
	private ArrayList<Node> 					chatClientList;
	private ArrayList<String>					macAddressList;
	private List<WTAClass> 						WTAList;
	private List<WifiP2pDevice>					proximityGroupList;

	private long 								cycle, emptyGroupTimerReached;
	private int 								p2pInfoPid, wifip2pmanagerPid, wifimanagerPid, transport0Pid, thisPid, routaodvPid;							
	public WiFiServiceDiscoveryActivity clone(){
		WiFiServiceDiscoveryActivity wsda = null;
		try { wsda = (WiFiServiceDiscoveryActivity) super.clone(); }
		catch( CloneNotSupportedException e ) {} // never happens
		wsda.p2pInfoPid 			= p2pInfoPid;
		wsda.wifip2pmanagerPid 		= wifip2pmanagerPid;
		wsda.wifiManager 			= wifiManager;
		wsda.transport0Pid 			= transport0Pid;
		wsda.routaodvPid			= routaodvPid;
		wsda.thisPid				= 0;
		wsda.cycle 					= 0;
		wsda.emptyGroupTimerReached = 0;
		wsda.delayHandler1Started 	= false;
		wsda.delayHandler2Started 	= false;
		wsda.delayHandler3Started 	= false;
		wsda.delayHandler4Started 	= false;
		wsda.delayHandler1		  	= 0;
		wsda.delayHandler2		  	= 0;
		wsda.delayHandler3		  	= 0;
		wsda.delayHandler4		  	= 0;
		wsda.count				  	= 0;
		wsda.groupCapacity		  	= 0;
		wsda.discoveryFlag		  	= false;
		wsda.isWifiP2pEnabled 		= false;	// would be set to true by Broadcast receiver when the wifi P2P enabled
		wsda.isGroupOwner 			= false;	// would be set to true by ConnectionInfoListener when connection info avilable
		wsda.isConnected 			= false;    // would be set to true by Broadcast receiver when the device state changed
		wsda.SMARTSelected 			= false;
		wsda.intention 				= 0;		// The intention of this device. we may set it once to save memory instead of calling getIntention() method several times
		wsda.appTerminate       	= false;
		wsda.isGroupFormed			= false;
		wsda.apListRequested		= false;
		wsda.groupID				= 0;
		wsda.groupRecordUpdated		= false;
		wsda.alreadyConnected		= false;
		wsda.p2pMacAddress			= "";
		wsda.goMacAddress			= "";
		wsda.startWTAClaculation	= false;
		wsda.thisNode				= Network.get(0);
		wsda.proximityGroupList		= new ArrayList<WifiP2pDevice>();
		return wsda;
	}

	public WiFiServiceDiscoveryActivity(String prefix) {
		p2pInfoPid 	 			= Configuration.getPid(prefix + "." + "p2pinfo");
		wifip2pmanagerPid  		= Configuration.getPid(prefix + "." + "p2pmanager");
		wifimanagerPid  		= Configuration.getPid(prefix + "." + "wifimanager");
		transport0Pid			= Configuration.getPid(prefix + "." + "transport0");
		routaodvPid				= Configuration.getPid(prefix + "." + "routaodv");
		cycle 					= 0;
		emptyGroupTimerReached 	= 0;		
		delayHandler1Started	= false;
		delayHandler2Started	= false;
		delayHandler3Started	= false;
		delayHandler4Started	= false;
		delayHandler1			= 0;
		delayHandler2			= 0;
		delayHandler3			= 0;
		delayHandler4			= 0;
		count 					= 0;
		groupCapacity 			= 0;		// will be increamented by one for each new peer in the group
		discoveryFlag 			= false;	
		isWifiP2pEnabled 		= false;	// would be set to true by Broadcast receiver when the wifi P2P enabled
		isGroupOwner 			= false;	// would be set to true by ConnectionInfoListener when connection info avilable
		isConnected 			= false;    // would be set to true by Broadcast receiver when the device state changed
		SMARTSelected 			= false;
		intention 				= 0;		// The intention of this device. we may set it once to save memory instead of calling getIntention() method several times
		appTerminate       	 	= false;
		isGroupFormed			= false;
		apListRequested			= false;
		groupID					= 0;
		groupRecordUpdated		= false;
		alreadyConnected		= false;
		p2pMacAddress			= "";
		goMacAddress			= "";
		startWTAClaculation		= false;
		thisNode				= Network.get(0);
		proximityGroupList		= new ArrayList<WifiP2pDevice>();
	}

	// This method will be called by Broadcast receiver when wifip2p state changed
	public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
		//            Toast.makeText(WiFiServiceDiscoveryActivity.this, "WiFiP2PEnabled = " + isWifiP2pEnabled, Toast.LENGTH_SHORT).show();
		this.isWifiP2pEnabled = isWifiP2pEnabled;
		if (isWifiP2pEnabled){
			appendStatus("WiFi P2P is enabled");
		}else{
			appendStatus("WiFi P2P is disabled");
		}
	}

	// This method will be called by Broadcast receiver when peer change occur
	public void setPeersChanged() {
		//Toast.makeText(WiFiServiceDiscoveryActivity.this, "Peer Changed", Toast.LENGTH_SHORT).show();
		updatePeerList();
	}

	//This method will be called by Broadcast receiver when connection status changed
	public void setConnectChanged(boolean connectionState){
		isConnected = connectionState;    	
		if(isConnected){ // if true
			//Toast.makeText(WiFiServiceDiscoveryActivity.this, "Connected", Toast.LENGTH_SHORT).show();
			//manager.requestConnectionInfo();
			appendStatus("Connected!");
			alreadyConnected = true;
		}else{          // if false
			//Toast.makeText(WiFiServiceDiscoveryActivity.this, "Not Connected", Toast.LENGTH_SHORT).show();
			appendStatus("Disconnected");
			isGroupOwner = false;
			isGroupFormed = false;
			groupRecordUpdated = false;
			startWTAClaculation = false;
			WTAList.clear();
			chatClientList.clear();
			//Restart Discovery only if the disconnection was not requested by the user and we had already connected
			if (alreadyConnected && !appTerminate){
				alreadyConnected = false;    			    			    			
				appendStatus("Restarting service Discovery");
				restartServiceDiscovery();     			
			}
		}
	}

	public void setDevicestatuschanged(int srcdevicestatus){
		switch(srcdevicestatus){
		case CONNECTED:
			appendStatus("Device Status: CONNECTED");
			break;
		case INVITED:
			appendStatus("Device Status: INVITED");
			break;
		case FAILED:
			appendStatus("Device Status: FAILED");
			break;
		case AVAILABLE:
			appendStatus("Device Status: AVAILABLE");
			break;
		case UNAVAILABLE:
			appendStatus("Device Status: UNAVAILABLE");
			break;
		default:
			appendStatus("Device Status: Unknown");
			break;
		}
	}


	@Override
	public void nextCycle(Node node, int pid) {
		thisNode = node;
		thisPid = pid;
		if (cycle == 1){


			// Initiallizing wifiP2P manager, Channel and Broadcast receiver and registering the receiver
			manager = (WifiP2pManager) node.getProtocol(wifip2pmanagerPid);
			wifiManager = (WifiManager) node.getProtocol(wifimanagerPid);
			nodeInfo = (nodeP2pInfo) node.getProtocol(p2pInfoPid);

			//handler 			= new Handler(this);
			config 				= new WifiP2pConfig();  			// configuration of wifi p2p. here for WPS configuration
			newGroup			= new Group();						// The current group if this device is GO. Otherwise it will return null
			intentionList 		= new HashMap<String, Double>();  	// Intention	<=>	Device Mac Address
			serviceList 		= new HashMap<String, String>();  	// Device Mac Address <=> Service Name
			//groupList 			= new HashMap<String, String>();  	// Group ID   <=> Mac Address of GO
			peerList 			= new ArrayList<WifiP2pDevice>(); 	// WifiP2pDevice of peers found in the proximity
			groupedPeerList 	= new ArrayList<WifiP2pDevice>(); 	// WifiP2pDevice of peers in the group  
			//serverThreads		= new ArrayList<GroupOwnerSocketHandler>(); // each client need a seprate thread and socket
			//chatClientList		= new ArrayList<ChatManager>();  	 // List of ChatManagers. A chatManager Object is needed for each client to send message
			chatClientList		= new ArrayList<Node>(); 
			macAddressList  	= new ArrayList<String>();
			WTAList 			= new ArrayList<WTAClass>();


			//			statusTxtView = (TextView) findViewById(R.id.status_text);         	//Status text at the bottom of main activity        
			//			statusTxtView.setMovementMethod(new ScrollingMovementMethod());	   	// making Status text view scrollable
			//			statusTxtView.setScrollbarFadingEnabled(false);					  	// Set the Scroll bar visible all the time        

			// Adding necessar change action to the intent filter
			//			intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
			//			intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
			//			intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
			//			intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
			//			intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);


			/*
			 * Register listeners. These are callbacks invoked
			 * by the system when a service is actually discovered.
			 */

			manager.registerBroadcastReceiver(this);
			manager.registerPeerListener(this);
			manager.registerConInfoListener(this);
			manager.registerGroupInfoListener(this);
			manager.registerHandler(this);
			wifiManager.registerHandler(this);
			wifiManager.startScan();

			// Configure the Intention and WPS in wifi P2P
			//Random r = new Random();

			// In this simulator the Intention value has been already fixed at the NodeP2pInfo class. We have not using the intention parameter in Configuration file in this simulator -- later I will fix this
			config.groupOwnerIntent = nodeInfo.getGoIntentionValue();
			// WPS in this simulator is always PBC -- it has been just modeled by a fix delay
			config.wps = "PBC";

			//p2pMacAddress = Utils.getMACAddress("p2p0");
			p2pMacAddress = nodeInfo.getMacAddress();
			// Calculating the intention and Putting the Intention of this device inside the intention List
			intention = config.groupOwnerIntent;
			intentionList.put(p2pMacAddress, intention);

			// Calling the first method in this activity
			//Visualizer.print("Cycle 1 on Node: " + node.getID() + " is finished");
			startRegistrationAndDiscovery();
		}

		if(delayHandler1Started && cycle == delayHandler1){
			// go to the position where we set the delayHandler1 parameters for more details
			// we are actually mimicking the action of Threads in a real world.
			if(!isConnected){
				appendStatus("All Requests failed; Creating a group");
				createGroup();
			}
			delayHandler1Started = false;
		}

		if(delayHandler2Started && cycle == delayHandler2){
			if (!proximityGroupList.isEmpty() && !isConnected){      // if there are some groups around send request to join          		                  			
				//				for (Entry<String, String> groupentry : groupList.entrySet()) {
				//					appendStatus("Invitation sent to Group: " + groupentry.getKey());
				//					connecPeer(groupentry.getValue());
				//				}
				for(WifiP2pDevice device: proximityGroupList){
					appendStatus("Invitation sent to Group: " + device.deviceAddress);
					connecPeer(device.deviceAddress);
				}
				delayHandler3 = (long) (cycle + (calculateWait()/NodeMovement.CycleLenght));
				delayHandler3Started = true;				
			}else if(proximityGroupList.isEmpty() && !isConnected){  // if there is not any group, create a group
				appendStatus("No Other groups; Creating a new group");
				createGroup();
			}
		}

		if(delayHandler3Started && cycle == delayHandler3){
			if(!isConnected){
				appendStatus("All Requests failed; Creating a group");
				createGroup();
			}
			delayHandler3Started = false;
		}

		if(delayHandler4Started && cycle == delayHandler4){
			WiFiP2pService newService = new WiFiP2pService();
			newService.instanceName="MAGNET";    						 
			appendStatus("SMART selected= true");
			connectP2p(newService);
			delayHandler4Started = false;
		}

		// if WTA calculation requested: decide the group connection every 10 seconds
//		if(isGroupOwner && startWTAClaculation && cycle%((wTimeForWTA*1000)/NodeMovement.CycleLenght)==0){
//			startWTAClaculation = false;
//			decideGroupConnection();
//		}

		// empty group time out check -- If a GO remains without any clients for 10 seconds then it should check around to see if it is possible to connec to another group as a client or not
		// If it is possible the GO will remove its group and will connect to the other group as client
		if(isGroupOwner && groupedPeerList.isEmpty() && emptyGroupTimerReached<=(5000/NodeMovement.CycleLenght)){
			emptyGroupTimerReached++;
		}else if(isGroupOwner && groupedPeerList.isEmpty() && emptyGroupTimerReached>(5000/NodeMovement.CycleLenght)){
			emptyGroupTimerReached = 0;

			if(!proximityGroupList.isEmpty()){
				//Visualizer.print("empty group with groups around. Node: " + node.getID() + " connection to Node: " +proximityGroupList.get(0).deviceAddress);
				manager.removeGroup();
				// getting the first group in the list
				//config.deviceAddress = groupList.get((String) groupList.keySet().toArray()[0]);
				config.deviceAddress = proximityGroupList.get(0).deviceAddress;
				manager.connect(config);
			}
		}else{
			emptyGroupTimerReached = 0;
		}
		cycle++;
	}


	//Registers a local service and then initiates a service discovery    
	private void startRegistrationAndDiscovery() {
		HashMap<String, String> record1 = new HashMap<String, String>();
		record1.put("Intention", String.valueOf(intention));
		wifiP2pService service1 = new wifiP2pService("MAGNET", SERVICE_REG_TYPE, record1);

		manager.addLocalService(service1);
		// Start Service discovery

		discoverService();
	}

	@Override
	public void onDnsSdServiceAvailable(String instanceName,
			String registrationType, WifiP2pDevice srcDevice) {
		// A service has been discovered.                                
		WiFiP2pService service = new WiFiP2pService();

		service.device = srcDevice;			   // srcDevice is a WifiP2Device object
		service.instanceName = instanceName;   //instancename is the name of Service e.g. MAGNET
		service.serviceRegistrationType = registrationType;

		// putting the new service inside the serviceList only if it has not already been there.
		if(!(serviceList.containsKey(srcDevice.deviceAddress) && serviceList.containsValue(instanceName))){
			serviceList.put(srcDevice.deviceAddress, instanceName);
		}
	}

	@Override
	public void onDnsSdTxtRecordAvailable(String fullDomainName, Map<String, String> record, WifiP2pDevice srcDevice) {
		appendStatus(String.valueOf(srcDevice.deviceName + " " + record.get("Intention")));
		// Put this new Record in the intention List
		intentionList.put(srcDevice.deviceAddress, Double.parseDouble(record.get("Intention")));
	}

	// Start Discovery
	private void discoverService() {

		manager.registerDnsSdResponseListeners(this);
		manager.registerDnsSdTxtRecordListener(this);

		// After attaching listeners, initiate discovery.
		manager.discoverServices();
		//Visualizer.print("Service Discovery requested on Node: " + thisNode.getID() + " is finished");
	}

	// This method actually will be called when the related view has been clicked on the fragment. 
	// But to make the click autonamous, I have also start this method after couple of seconds by passing "MAGNET" to it
	// which means I mimic the click scenario
	public void connectP2p(WiFiP2pService service) {

		if (service.instanceName.equals("Sample Service")){
			// this is a sample service name advertisemnet. Nothing has been implemented here	

		}else if (service.instanceName.equals("MAGNET")){
			//Finding the maximum intention inside the intentionList HashMap
			String deviceMaxIntention = null;
			double maxValueInMap=(Collections.max(intentionList.values())); // This will return max value in the Hashmap
			for (Map.Entry<String, Double> entry : intentionList.entrySet()) {  // Itrate through hashmap
				if (entry.getValue()==maxValueInMap) {
					deviceMaxIntention = entry.getKey();     				// find the device name with the max intention
				}
			}

			//Check to see whether this device has the highest Intention
			//If this device has the highest intention it will create a group
			//else it will wait for the other device to create a group. if no groups found it will create one

			if (p2pMacAddress.equals(deviceMaxIntention)){
				//This device has the highest Intention
				appendStatus("This device has the highest Intention");
				if (!isConnected && proximityGroupList.isEmpty()){   // IF this device is not connected and there is not any group around
					appendStatus("No Other groups; Creating a new group");
					createGroup();
				} else if(!isConnected && !proximityGroupList.isEmpty()){ 
					for(WifiP2pDevice device: proximityGroupList){						
						connecPeer(device.deviceAddress);
						appendStatus("Invitation sent to Group: " + device.deviceAddress);
					}
					//wait for couple of seconds and then check to see if it is connected. otherwise creat a group
					delayHandler1 = (long) (cycle+(calculateWait()/NodeMovement.CycleLenght));
					delayHandler1Started = true;
				}  	 
			}
			else { 
				//Not the highest Intention. It will wait and then check the group list for any 
				// available groups and try to connect to the group owner of each group.
				appendStatus("Not the highest Intention");
				appendStatus("will wait for " + calculateWait() + " miliseconds"); 
				delayHandler2 = (long)(cycle + (calculateWait()/NodeMovement.CycleLenght));
				delayHandler2Started = true;    
			}
		}  
		SMARTSelected = false;
		appendStatus("SMART selected= false");
	}

	// Group Creation
	private void createGroup(){
		//appendStatus("Group List: " + groupList);
		// before advertising the new group first remove the previous one if there is any
		if (newGroup.getGroupName()!=null){ 
			// this means that we have already set a group name which means we already created a group which removed now
			// So we have to stop the group adv at service level by calling the following method
			// serviceGroup is created as a global field and set at the previous group generation
			//final String ID = String.valueOf(newGroup.getGroupID());
			manager.removeLocalService(serviceGroup);
			newGroup.resetGroup();

		}
		manager.createGroup();
		// A random Group number will be created
		if (groupID==0){
			groupID = (int)(Math.random() * 1000);
		}
		newGroup.setGroupID(groupID);       // Set Group ID
		newGroup.setGroupName("Group ID: " + String.valueOf(groupID));  // set Group name;

		//Creating a record for group and advertise it
		HashMap<String, String> recordGroup = new HashMap<String, String>();
		recordGroup.put("GroupID", String.valueOf(groupID));
		recordGroup.put("Intention", String.valueOf(intention));
		newGroup.setRecord(recordGroup);

		serviceGroup = new wifiP2pService(newGroup.getGroupName(), SERVICE_REG_TYPE, newGroup.getRecord());	            
		manager.addLocalService(serviceGroup);

		appendStatus("Added Group " + String.valueOf(newGroup.getGroupID()));                    
		// If there are peers around, invite them to join the newly created group
		if (!peerList.isEmpty()){
			for (WifiP2pDevice device : peerList){
				if(device.status == AVAILABLE){ // Send invitations if it is available (not connected not invited not unavailable)
					//appendStatus("Invitation sent to: " + device.deviceAddress); 	               		  		
					connecPeer(device.deviceAddress);
				}
			}						                   					         
		}
	}

	// get the device MAC address and connect to the peer
	private void connecPeer(String deviceMacAddress){
		config.deviceAddress = deviceMacAddress;
		appendStatus("Trying to connect to: " + config.deviceAddress);
		manager.connect(config);		  				
	}

	@Override
	public void onPeersAvailable(WifiP2pDeviceList peers) {

		//ArrayList<WifiP2pDevice> tempPeer = new ArrayList<WifiP2pDevice>();
		peerList.clear();   // Clear All elements because the peer changed may be caused by peer disappearance
		peerList.addAll(peers.getDeviceList());	 // Add all peers found to the peerList

		//Updating the Mac address List of all available devices around
		// We need this list somethimes to make computations easier
		// This list is as fresh as the peerList So better than the other list
		macAddressList.clear();
		proximityGroupList.clear();
		for (WifiP2pDevice device: peerList){
			macAddressList.add(device.deviceAddress);
			if (device.isGroupOwner()){
				proximityGroupList.add(device);
			}
		}
		// Updating Intention List and remove all intentions from devices that are not available anymore
		for (Iterator<Entry<String, Double>> itr = intentionList.entrySet().iterator(); itr.hasNext();)
		{
			Map.Entry<String, Double> entrySet = (Entry<String, Double>) itr.next();
			String Key = entrySet.getKey();
			if (!macAddressList.contains((Key)))
			{
				itr.remove();
			}
		}
		intentionList.put(p2pMacAddress, intention);  // adding the Intention of this device again because it was remove at the above procedure

		//remove the previous groups from groupList if there are not available anymore		
		//		for (Iterator<Entry<String, String>> itr = groupList.entrySet().iterator(); itr.hasNext();)
		//		{
		//			Map.Entry<String, String> entrySet = (Entry<String, String>) itr.next();
		//			String value = entrySet.getValue();
		//			if (!macAddressList.contains((value)))
		//			{
		//				itr.remove();               
		//			}
		//		}
		// if we are the group owner we send the invitation to the newly found peer
		if (isConnected && isGroupOwner){					
			for (WifiP2pDevice device : peerList){
				if(device.status != CONNECTED && device.status != INVITED) {   // status = 3 means device available / status=0 means device connected / status = 1 means device invited					 
					connecPeer(device.deviceAddress);
				}
			}	
			// This will be executed at the beggining while the device is not connected
			// and peers changed. The device will wait first to see if it sees all peers around and then it tryes to 
			// start the connection procedure
		}else if (!isConnected && !SMARTSelected){
			SMARTSelected = true;
			delayHandler4 = (long)(cycle + (calculateWait()/NodeMovement.CycleLenght));
			delayHandler4Started = true;
		}
	}

	// Update Peer List
	public void  updatePeerList(){
		// updating peer list
		manager.requestPeers();
		manager.requestConnectionInfo();
	}

	// IF the device is connected, this method will be called
	@Override
	public void onConnectionInfoAvailable(WifiP2pInfo p2pInfo) {
		isGroupFormed = p2pInfo.groupFormed;
		/*
		 * The group owner accepts connections using a server socket and then spawns a
		 * client socket for every client. This is handled by {@code
		 * GroupOwnerSocketHandler}
		 */
		if (p2pInfo.isGroupOwner) {
			isGroupOwner = true;
			goMacAddress = p2pMacAddress;
			appendStatus("Connected as group owner");
			appendStatus("Group formed= " + isGroupFormed);
			manager.requestGroupInfo();   // will be infomed at the onGroupInfoAvailable callback whenever it was ready    	
			// if it is not a GO, run a client socket handler
		} else if (isGroupFormed){
			appendStatus("Group formed= " + p2pInfo.groupFormed);        	
			isGroupOwner = false;
			goMacAddress = p2pInfo.groupOwnerAddress;
//			callbackMessage newMessage = new callbackMessage();
//			newMessage.what = MY_HANDLE;
//			newMessage.obj = p2pMacAddress;
//			String Receiver = p2pInfo.groupOwnerAddress;
//			manager.send(newMessage, Receiver); 
			callbackMessage newMessage = new callbackMessage();
			newMessage.what = MESSAGE_READ;
			newMessage.arg1 = REQUEST_SSID;
			newMessage.obj = p2pMacAddress;
			manager.send(newMessage, p2pInfo.groupOwnerAddress);
		}
	}

	// Status of the program which will be shown at the bottom
	public void appendStatus(String status) {
		Visualizer.print(status, Color.BLUE);
	}

	// Clearing the service requests and start a fresh discovery
	// discovery stops after connection but we need to continue discovery
	public void restartServiceDiscovery(){
		wifiManager.startScan();
		manager.stopPeerDiscovery();
		appendStatus("Peer Discovery Stopped!");
		appendStatus("All Service Requests Cleared");
		appendStatus("Added service discovery request");
		manager.discoverServices();
		appendStatus("Service discovery initiated");                


	}

	//Updating list of wifi APs
	public void updateWifiAPs(){
		//Visualizer.print("Update WIFI AP");
		List<ScanResult> wifiScanResults = new ArrayList<ScanResult>();
		wifiScanResults = wifiManager.getScanResults();

		//		WifiManager wifiManager = (WifiManager) thisNode.getProtocol(wifimanagerPid);
		//		if(wifiManager.getWifiStatus()==AVAILABLE && wifiScanResults.size()>0){
		//			WifiConfiguration config = new WifiConfiguration();
		//			config.SSID = wifiScanResults.get(CommonState.r.nextInt(wifiScanResults.size())).SSID;
		//			Visualizer.print("Trying to connect to SSID: " + config.SSID);
		//			wifiManager.connect(config);
		//		}

		//		wifiAPList.clear();
		//		directAPList.clear();
		//		for(int i=0; i < wifiScanResults.size(); i++){
		//			wifiAPList.add(wifiScanResults.get(i).SSID);
		//			if (wifiScanResults.get(i).SSID.contains("DIRECT")){
		//				Log.d("Access Points", "Found Direct: " + wifiScanResults.get(i).SSID);
		//				appendStatus("Direct AP found: " + wifiScanResults.get(i).SSID);
		//				directAPList.add(wifiScanResults.get(i).SSID);
		//				Log.d("Access Points", "directAPList" + directAPList);
		//			}
		//		}
		if (apListRequested){
			apListRequested = false;
			//			directAPList.add("ALCON_APLIST_RESPONSE");
			//			directAPList.add("IP" + chatManager.getLocalAddress());
			//			directAPList.add("MAC" + p2pMacAddress);

			//			magnetMessage message = new magnetMessage();
			//			message.what = ALCON_APLIST_RESPONSE;
			//			//message.object = wifiScanResults;
			//			ArrayList<String> Testlist = new ArrayList<String>();
			//			Testlist.add("SALAM");
			//			chatManager.writeObject(Testlist);
			//			appendStatus("AP List Sent");
			for(ScanResult ap: wifiScanResults){
				appendStatus(ap.SSID);
			}
		}
	}

	public String desError(int errorCode){
		String returnState = null;
		switch(errorCode){
		case 0:
			returnState =   "internal error";
			break;
		case 1:
			returnState = " p2p unsupported";
			break;
		case 2:
			returnState = "framework busy";
			break;
		case 3:
			returnState = "no service requests";
			break;
		default:
			returnState = "Unknown error!";
		}
		return returnState;
	}

	//getting device name 
	public String getPhoneName(){  
		String deviceName = nodeInfo.getDeviceName();     
		return deviceName;
	}

	// Calculate Wating time for being second or ... Intention dynamically based on Intention
	private int calculateWait(){
		return (int) ((-5*intention) + 75)*waitcoeff; // it will return waiting time Intention=14 => wait 5s, Inten = 1 => wait 70s
	}

	public String extractIP(ArrayList<String> array){
		String IP = null;
		for(String tempArray: array){
			if (tempArray.contains("IP")){
				IP = tempArray.substring(3);
			}
		}
		return IP;
	}

	public String extractMAC(ArrayList<String> array){
		String MAC = null;
		for(String tempArray: array){
			if (tempArray.contains("MAC")){
				MAC = tempArray.substring(3);
			}
		}
		return MAC;
	}

	// Here Group Owner connects to another group owner via WiFi
	private void decideGroupConnection(){
		appendStatus("decideGroupConnection");		
		WifiConfiguration wifiConfig = new WifiConfiguration();
		if(wifiManager.getScanResults().size()>0 && wifiManager.getScanResults().get(0)!=null){
			wifiConfig.SSID = wifiManager.getScanResults().get(0).SSID;
			wifiManager.connect(wifiConfig);
		}

	}

	public static List<HashMap<String, String>> findNodeDFS(final wtaNode root) {
		int i=0;
		int stage = 0;
		int maxSize=0;
		String[] pathList = new String[500];

		List<HashMap<String, String>> finalSolution = new ArrayList<HashMap<String, String>>();

		@SuppressWarnings("serial")
		Stack<wtaNode> stack = new Stack<wtaNode>(){{
			add(root);  
		}};
		while (!stack.isEmpty()) {
			wtaNode current = stack.pop();
			String mydata = current.nodeName;

			// Extracting the Stage that the current node is in
			Pattern pattern = Pattern.compile("_(.*?)_");
			Matcher matcher = pattern.matcher(mydata);
			if (matcher.find())
			{
				stage = Integer.parseInt((matcher.group(1).substring(0,1)));
			}

			// Extracting the real external group name (nodes with the same group names should not be counted twice in the path)	        
			//externalGroup = mydata.substring(mydata.lastIndexOf('_') + 1).trim();

			//Add the current node name to the right stage in the pathList
			pathList[stage] = mydata;

			// count the number of groups in the path when we reach the leaf
			if (current.childNodeList.isEmpty()){
				//System.out.println("Reach the leaf" + mydata + " " + stage);
				HashMap <String, String> tempArray = new HashMap<String, String>();
				for(i=1; i<stage+1; i++){	        		
					// we put the node in a hash map. the key values are the real name of external groups
					// if there is any duplicates the previous value would be replaces
					// later when we count the size of the hashmap it would return the correct size for us
					tempArray.put(pathList[i].substring(pathList[i].lastIndexOf('_') + 1).trim(), pathList[i]);

				}
				if(tempArray.size()== maxSize){
					finalSolution.add(tempArray);
					//System.out.println(finalSolution);
				}
				if (tempArray.size()> maxSize){
					maxSize=tempArray.size();
					finalSolution.clear();
					finalSolution.add(tempArray);
				} 
			}
			stack.addAll(current.childNodeList);
		}
		return finalSolution;
	}

	public static HashMap<String, String> solutionOptimizer(List<HashMap<String, String>> finalResultB, List<WTAClass> interfaceList){
		HashMap <String, String> optimizedSolution = new HashMap<String, String>();
		double wtaResult = 0;
		for (HashMap<String, String> tempHash: finalResultB){
			Iterator<Entry<String, String>> it = tempHash.entrySet().iterator();
			double newdouble = 0;
			while (it.hasNext()) {
				Map.Entry<String, String> pairs = (Map.Entry<String, String>)it.next();
				double RSSI= 0;
				double groupValue = 1;

				// getting the RSSI and group Value for current pairs (connection)
				for (WTAClass tempWTA: interfaceList){

					if(tempWTA.getInterfaceName().equals(pairs.getValue())){
						//Visualizer.print(" PaisValue: " + pairs.getValue() + " key: " + pairs.getKey());
						if(tempWTA.RSSIMap.get(pairs.getKey())!=null)
							RSSI = (double)tempWTA.RSSIMap.get(pairs.getKey());
						if(tempWTA.groupValue.get(pairs.getKey())!=null)
							groupValue = (double)tempWTA.groupValue.get(pairs.getKey());
					}
				}
				// calculating one of the line of the algorithm (Weapon Target Assignment)
				newdouble = newdouble + groupValue*(1-(RSSI/100));

				//pairs.getKey()  pairs.getValue();
				//it.remove(); // avoids a ConcurrentModificationException
			}



			if(wtaResult == 0 || newdouble<wtaResult){
				wtaResult = newdouble;
				optimizedSolution = tempHash;
				//System.out.println(wtaResult + " " + optimizedSolution);

			}
		}
		return optimizedSolution;
	}

	// ask clients to connect to the external groups via WiFi Interface
	public void connectGroups(HashMap<String, String> bestSolution){
		if(isGroupOwner){
			Iterator<Entry<String, String>> it = bestSolution.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, String> pair = (Map.Entry<String, String>)it.next();
				callbackMessage newMessage = new callbackMessage();
				newMessage.what = MESSAGE_READ;
				newMessage.arg1 = REQUEST_CONNECT_AP;
				newMessage.obj = pair.getKey();
				manager.send(newMessage, pair.getValue());
			}
		}
	}

	@Override
	public void onReceive(wifiP2pEvent wifip2pevent) {
		String action = wifip2pevent.getEvent();

		if (action.equals("WIFI_P2P_STATE_CHANGED_ACTION")) {

			// UI update to indicate wifi p2p status.
			int state = manager.getExtraSystemInfo(WifiP2pManager.EXTRA_WIFI_STATE);
			if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
				// Wifi Direct mode is enabled
				setIsWifiP2pEnabled(true);
			} else {
				setIsWifiP2pEnabled(false);
			}
		}

		if (action.equals("WIFI_P2P_PEERS_CHANGED_ACTION")) {
			setPeersChanged();
		}

		if (action.equals("WIFI_P2P_CONNECTION_CHANGED_ACTION")) {
			if (manager == null) {
				return;
			}

			if (nodeInfo.getStatus()==CONNECTED) {

				// we are connected with the other device, request connection
				// info to find group owner IP
				manager.requestConnectionInfo();
				setConnectChanged(true); // connected
			} else {
				setConnectChanged(false); // disconnect
				// It's a disconnect
			}
		} else if (action.equals("WIFI_P2P_THIS_DEVICE_CHANGED_ACTION")) {

			WifiP2pDevice device = new WifiP2pDevice(thisNode, p2pInfoPid) ;
			setDevicestatuschanged(device.status);


		}
		if (action.equals("SCAN_RESULTS_AVAILABLE_ACTION")) {
			updateWifiAPs(); 

		}

	}

	@Override
	public void onGroupInfoAvailable(WifiP2pGroup group) {

		groupedPeerList.clear();   // Clear All elements because the peer changed may be caused by peer disappearance
		// Add all peers found to the peerList 
		for(Node cNode: group.getNodeList()){
			WifiP2pDevice newDevice = new WifiP2pDevice(cNode, p2pInfoPid);
			groupedPeerList.add(newDevice);
		}  

		// Update the WTA class as well - maybe a client left the group and is not available anymore
		List<String> addressList = new ArrayList<String>();

		// creating a list of all available clinet macaddress in this group
		for(Node cNode: group.getNodeList()){
			nodeP2pInfo cNodeInfo = (nodeP2pInfo) cNode.getProtocol(p2pInfoPid);
			addressList.add(cNodeInfo.getMacAddress());
		}
		//check if every WTAclass in the WTAList is still available inside the group.
		List<WTAClass> tempWTAClass = new ArrayList<WTAClass>();
		for(WTAClass wtaIns: WTAList){
			for(String macAddress:addressList){
				if(macAddress.equals(wtaIns.getInterfaceName())){
					tempWTAClass.add(wtaIns);
					break;
				}
			}	
		}
		WTAList.clear();
		WTAList.addAll(tempWTAClass);

		appendStatus("Number of devices in the group: " + (groupedPeerList.size()+1)); 
		appendStatus("Group SSID: " + group.getSSID());
		appendStatus("Group PASS: " + group.getmPassphrase());
		if (newGroup.getGroupName()!=null && !groupRecordUpdated){
			newGroup.setGroupSSID(group.getSSID());
			newGroup.setGroupPassPhrase(group.getmPassphrase());

			manager.removeLocalService(serviceGroup);

			HashMap<String, String> recordGroup = new HashMap<String, String>();
			recordGroup.put("GroupID", String.valueOf(groupID));
			recordGroup.put("Intention", String.valueOf(intention));
			recordGroup.put("groupSSID", newGroup.getGroupSSID());
			recordGroup.put("groupPass", newGroup.getGroupPassPhrase());
			newGroup.setRecord(recordGroup);

			serviceGroup = new wifiP2pService(
					newGroup.getGroupName(), SERVICE_REG_TYPE, newGroup.getRecord());	            
			manager.addLocalService(serviceGroup);
			appendStatus("Group Record Updated " + String.valueOf(newGroup.getGroupID()));
			groupRecordUpdated = true;
		}

		//		for(Node tempDevice: group.getNodeList()){
		//			nodeP2pInfo tempDeviceInfo = (nodeP2pInfo) tempDevice.getProtocol(p2pInfoPid);
		//			callbackMessage newMessage = new callbackMessage();
		//			newMessage.what = MY_HANDLE;
		//			newMessage.obj = nodeInfo.getMacAddress();
		//			String receiverAdd = tempDeviceInfo.getMacAddress();
		//			manager.send(newMessage, receiverAdd);
		//		}

	}

	// when two devices (one group owner and one is client) connecting to each other the firsl message that is exchanging between thme is MY_HANDLE message. Here we undesrtand that the client is connected or the group owner has a new client
	@Override
	public void handleMessage(callbackMessage msg) {
		if(msg==null)
			return;
		switch (msg.what){
		case MESSAGE_READ:

			switch (msg.arg1){
			case REQUEST_AP_SEEN:
				if(!isGroupOwner && isConnected && ((String) msg.obj).equals(goMacAddress)){ // reply to this only if you are a connected clinet to the GO which request this
					//get the final list of APs around
					List<ScanResult> currentApList = wifiManager.getScanResults();

					WTAClass newWTA = new WTAClass(p2pMacAddress);
					newWTA.setGroupSeen(currentApList);

					callbackMessage newMessage = new callbackMessage();
					newMessage.what = MESSAGE_READ;
					newMessage.arg1 = CLIENT_AP_LIST;
					newMessage.obj = newWTA;
					manager.send(newMessage, (String) msg.obj);
				}
				break;


			case REQUEST_SSID:
				if(isGroupOwner){
					callbackMessage newMessage = new callbackMessage();
					newMessage.what = MESSAGE_READ;
					newMessage.arg1 = GO_SSID_RESPONSE;
					newMessage.obj = newGroup.getGroupSSID();
					manager.send(newMessage, (String) msg.obj);
				}
				break;
				
			case GO_SSID_RESPONSE:
				if(wifiManager.getWifiStatus()!=CONNECTED){
					manager.cancelConnect();
					WifiConfiguration wifiConfig = new WifiConfiguration();
					wifiConfig.SSID = (String)msg.obj;
					wifiManager.connect(wifiConfig);
				}
				
				
			case CLIENT_AP_LIST:
				if(isGroupOwner && isConnected){

					// check if the received WTA object has not been already available in the WTAList
					// if it is available. first remove it and then add the updated version to the list
					for (Iterator<WTAClass> wtaIterator = WTAList.iterator(); wtaIterator.hasNext();) {
						WTAClass result = wtaIterator.next();
						if (result.getInterfaceName().equals(((WTAClass) msg.obj).getInterfaceName())) {
							// Remove the current element from the iterator and the list.
							wtaIterator.remove();
						}
					}

					WTAList.add((WTAClass) msg.obj);				
					startWTAClaculation = true;
				}
				break;

			case REQUEST_CONNECT_AP:
				//Visualizer.print("REQUEST_CONNECT_AP Received: Node: " + thisNode.getID() + " Request Connection to: " + (String) msg.obj);
				if(!isGroupOwner && isConnected){
					WifiConfiguration wifiConfig = new WifiConfiguration();
					wifiConfig.SSID = null;
					// find the relevant SSID for received BSSID (msg.obj)
					for(ScanResult apList: wifiManager.getScanResults()){
						if(apList.BSSID.equals((String) msg.obj)){
							wifiConfig.SSID = apList.SSID;
						}
					}

					if(wifiConfig.SSID!=null){
						wifiManager.connect(wifiConfig);
					}
				}
				break;

			case ROUTING_MESSAGE:
				Transport transport0 = (Transport) thisNode.getProtocol(transport0Pid);
				eventMessage newEvent = new eventMessage();
				newEvent.destNode = thisNode;
				newEvent.destPid  = routaodvPid;
				newEvent.srcNode  = thisNode;
				newEvent.srcPid   = thisPid;
				newEvent.event    = "ROUTING_MESSAGE";
				newEvent.object   = msg.obj;
				newEvent.lastHopMacAddr  = msg.lastHopMacAddr;
				transport0.send(newEvent.srcNode, newEvent.destNode, newEvent, newEvent.destPid);

				break;
			}
			break;
		case MY_HANDLE:
			if(isGroupOwner){
				callbackMessage newMessage = new callbackMessage();
				newMessage.what = MESSAGE_READ;
				newMessage.arg1 = REQUEST_AP_SEEN;
				newMessage.obj = p2pMacAddress;
				String Receiver = (String) msg.obj;
				manager.send(newMessage, Receiver); 
			}
			break;
		}

	}

	@Override
	public void processEvent(Node arg0, int arg1, Object arg2) {
		// TODO Auto-generated method stub

	}
}
