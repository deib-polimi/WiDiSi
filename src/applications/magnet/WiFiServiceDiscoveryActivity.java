package applications.magnet;

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

import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.edsim.EDProtocol;

import wifi.ScanResult;
import wifi.WifiManager;
import wifidirect.nodemovement.Visualizer;
import wifidirect.p2pcore.BroadcastReceiver;
import wifidirect.p2pcore.WifiP2pConfig;
import wifidirect.p2pcore.WifiP2pDevice;
import wifidirect.p2pcore.WifiP2pDeviceList;
import wifidirect.p2pcore.WifiP2pGroup;
import wifidirect.p2pcore.WifiP2pInfo;
import wifidirect.p2pcore.WifiP2pManager;
import wifidirect.p2pcore.WifiP2pManager.Callback;
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
	public static final String 	TAG 					= "wifidirectdemo"; // A common tag for LogCat

	public static final int 	MESSAGE_READ 			= 0x400 + 1;		// Reserved Code for Message Handler 
	public static final int 	MY_HANDLE 				= 0x400 + 2;		// Reserved Code for Message Handler
	public static final int 	OBJECT_READ 			= 0x400 + 3;		// Reserved Code for Message Handler 
	public static final int     ALCON_APLIST_RESPONSE 	= 0x400 + 4;		// Reserved Code for Message Handler 
	public static final int     ALCON_APLIST_REQUEST	= 0x400 + 5;		// Reserved Code for Message Handler 
	public static final int		MESSAGE_TEST			= 0x400 + 6;		// Reserved Code for Message Handler 

	public static final int 	waitcoeff 				= 300;				// Waiting time will be multiply by this coefficient (milliseconds)
	public static final int 	SERVER_PORT 			= 4545;				// Server port is the same in GroupOwnerSocketHandler
	private peersim.core.Node   thisNode;

	// None constant variables
	public 	int 		count 				= 0;
	public 	int 		groupCapacity 		= 0;		// will be increamented by one for each new peer in the group
	public 	boolean 	discoveryFlag 		= false;	
	public 	boolean 	isWifiP2pEnabled 	= false;	// would be set to true by Broadcast receiver when the wifi P2P enabled
	public 	boolean 	isGroupOwner 		= false;	// would be set to true by ConnectionInfoListener when connection info avilable
	public 	boolean 	isConnected 		= false;    // would be set to true by Broadcast receiver when the device state changed
	private boolean 	SMARTSelected 		= false;
	public 	double   	intention 			= 0;		// The intention of this device. we may set it once to save memory instead of calling getIntention() method several times
	private boolean     appTerminate        = false;
	private boolean     isGroupFormed		= false;
	private boolean     apListRequested		= false;
	private	int			groupID				= 0;
	private boolean		groupRecordUpdated	= false;
	private boolean		alreadyConnected	= false;
	private String		p2pMacAddress		= null;

	//Device Status
	public static final int CONNECTED   = 0;
	public static final int INVITED     = 1;
	public static final int FAILED      = 2;
	public static final int AVAILABLE   = 3;
	public static final int UNAVAILABLE = 4;

	// Objetc definitions
	private 		WifiP2pManager 				manager;
	private			WifiManager 				wifiManager;
	//private 		ChatManager 				chatManager;
	private 	 	WifiP2pConfig 				config; 			  		// configuration of wifi p2p. here for WPS configuration
	private			Group						newGroup;					// The current group if this device is GO. Otherwise it will return null
	private 		wifiP2pService			 	serviceGroup;
	private 		nodeP2pInfo					nodeInfo;
	private 		long						delayHandler1 = 0, delayHandler2 = 0, delayHandler3 = 0, delayHandler4 = 0;
	private 		boolean						delayHandler1Started = false, delayHandler2Started = false, delayHandler3Started = false, delayHandler4Started = false;
	private 		long						cycleLength = 0;

	// Collections
	//private ArrayList<wifiP2pService>			servicesList;										
	private HashMap<Double, String> 			intentionList;  			// Intention	<=>	Device Mac Address
	private HashMap<String, String> 			serviceList;  				// Device Mac Address <=> Service Name
	private HashMap<String, String> 			groupList;  				// Group ID   <=> Mac Address of GO
	private ArrayList<WifiP2pDevice> 			peerList; 					// WifiP2pDevice of peers found in the proximity
	private ArrayList<WifiP2pDevice> 			groupedPeerList; 			// WifiP2pDevice of peers in the group  
	//private ArrayList<GroupOwnerSocketHandler> 	serverThreads; 				// each client need a seprate thread and socket
	//private ArrayList<ChatManager> 				chatClientList;   			// List of ChatManagers. A chatManager Object is needed for each client to send message
	private ArrayList<peersim.core.Node> 		chatClientList;
	private ArrayList<String>					macAddressList;
	private List<WTAClass> 						WTAList;

	private long cycle = 0;
	private int p2pInfoPid, wifip2pmanagerPid, wifimanagerPid;

	public WiFiServiceDiscoveryActivity clone(){
		WiFiServiceDiscoveryActivity wsda = null;
		try { wsda = (WiFiServiceDiscoveryActivity) super.clone(); }
		catch( CloneNotSupportedException e ) {} // never happens
		wsda.p2pInfoPid = p2pInfoPid;
		wsda.wifip2pmanagerPid = wifip2pmanagerPid;
		wsda.wifiManager = wifiManager;
		wsda.cycleLength = cycleLength;
		return wsda;
	}

	public WiFiServiceDiscoveryActivity(String prefix) {
		p2pInfoPid 	 		= Configuration.getPid(prefix + "." + "p2pinfo");
		wifip2pmanagerPid  	= Configuration.getPid(prefix + "." + "p2pmanager");
		wifimanagerPid  	= Configuration.getPid(prefix + "." + "wifimanager");
		cycleLength			= Configuration.getLong(prefix + "." + "cyclelength");
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
			appendStatus("Connected!");
			alreadyConnected = true;
		}else{          // if false
			//Toast.makeText(WiFiServiceDiscoveryActivity.this, "Not Connected", Toast.LENGTH_SHORT).show();
			appendStatus("Disconnected");
			isGroupOwner = false;
			isGroupFormed = false;
			groupRecordUpdated = false;
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
	public void nextCycle(peersim.core.Node node, int pid) {
		thisNode = node;
		if (cycle == 1){

			// Initiallizing wifiP2P manager, Channel and Broadcast receiver and registering the receiver
			manager = (WifiP2pManager) node.getProtocol(wifip2pmanagerPid);
			wifiManager = (WifiManager) node.getProtocol(wifimanagerPid);
			nodeInfo = (nodeP2pInfo) node.getProtocol(p2pInfoPid);

			//handler 			= new Handler(this);
			config 				= new WifiP2pConfig();  			// configuration of wifi p2p. here for WPS configuration
			newGroup			= new Group();						// The current group if this device is GO. Otherwise it will return null
			intentionList 		= new HashMap<Double, String>();  	// Intention	<=>	Device Mac Address
			serviceList 		= new HashMap<String, String>();  	// Device Mac Address <=> Service Name
			groupList 			= new HashMap<String, String>();  	// Group ID   <=> Mac Address of GO
			peerList 			= new ArrayList<WifiP2pDevice>(); 	// WifiP2pDevice of peers found in the proximity
			groupedPeerList 	= new ArrayList<WifiP2pDevice>(); 	// WifiP2pDevice of peers in the group  
			//serverThreads		= new ArrayList<GroupOwnerSocketHandler>(); // each client need a seprate thread and socket
			//chatClientList		= new ArrayList<ChatManager>();  	 // List of ChatManagers. A chatManager Object is needed for each client to send message
			chatClientList		= new ArrayList<peersim.core.Node>(); 
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
			wifiManager.startScan();

			// Configure the Intention and WPS in wifi P2P
			//Random r = new Random();

			// in this simulator the Intention value has been already fixed at the NodeP2pInfo class. We have not using the intention parameter in Configuration file in this simulator -- later I will fix this
			config.groupOwnerIntent = nodeInfo.getGoIntentionValue();
			// WPS in this simualtro is always PBC -- it has been just modelded by a fix delay
			config.wps = "PBC";

			//p2pMacAddress = Utils.getMACAddress("p2p0");
			p2pMacAddress = nodeInfo.getMacAddress();
			// calculating the intention and Putting the Intention of this device inside the intention List
			intention = config.groupOwnerIntent;
			intentionList.put(intention, p2pMacAddress);

			// Calling the first method in this activity
			//Visualizer.print("Cycle 1 on Node: " + node.getID() + " is finished");
			startRegistrationAndDiscovery();
		}

		if(delayHandler1Started && cycle == delayHandler1){
			// go to the position where we set the delayHandler1 parameters for more details
			// we are actually mimicing the action of Threads in a real world.
			if(!isConnected){
				appendStatus("All Requests failed; Creating a group");
				createGroup();
			}
			delayHandler1Started = false;
		}

		if(delayHandler2Started && cycle == delayHandler2){
			if (!groupList.isEmpty() && !isConnected){      // if there are some groups around send request to join          		                  			
				for (Entry<String, String> groupentry : groupList.entrySet()) {
					appendStatus("Invitation sent to Group: " + groupentry.getKey());
					connecPeer(groupentry.getValue());
				}
				delayHandler3 = cycle + (calculateWait()/cycleLength);
				delayHandler3Started = true;				
			}else if(groupList.isEmpty() && !isConnected){  // if there is not any group, create a group
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
		// Put this new Record in the intention List only it has not been already there
		if(!(intentionList.containsKey(Double.parseDouble(record.get("Intention"))) && intentionList.containsValue(srcDevice.deviceAddress))){
			intentionList.put(Double.parseDouble(record.get("Intention")), srcDevice.deviceAddress);
		}

		if (record.get("GroupID") != null){   // If this is a advertisement of Group, add to the group List
			// put this Group ID only if it has not been already there
			if(!(groupList.containsKey(record.get("GroupID")) && groupList.containsValue(srcDevice.deviceAddress))){
				groupList.put(record.get("GroupID"), srcDevice.deviceAddress);
				appendStatus("GroupList: " + String.valueOf(groupList));
			}			
		}
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
			double maxValueInMap=(Collections.max(intentionList.keySet())); // This will return max value in the Hashmap
			for (Entry<Double, String> entry : intentionList.entrySet()) {  // Itrate through hashmap
				if (entry.getKey()==maxValueInMap) {
					deviceMaxIntention = entry.getValue();     				// find the device name with the max intention
				}
			}

			//Check to see whether this device has the highest Intention
			//If this device has the highest intention it will create a group
			//else it will wait for the other device to create a group. if no groups found it will create one

			if (p2pMacAddress.equals(deviceMaxIntention)){
				//This device has the highest Intention
				appendStatus("This device has the highest Intention");
				if (!isConnected && groupList.isEmpty()){   // IF this device is not connected and there is not any group around
					appendStatus("No Other groups; Creating a new group");
					createGroup();
				} else if(!isConnected && !groupList.isEmpty()){ 
					//If the device is not connected and there are some groups around
					//Send request to joing to all available group owners
					for (Entry<String, String> groupentry : groupList.entrySet()) {						
						connecPeer(groupentry.getValue());
						appendStatus("Invitation sent to Group: " + groupentry.getKey());
					} 
					//wait for couple of seconds and then check to see if it is connected. otherwise creat a group
					delayHandler1 = cycle+(calculateWait()/cycleLength);
					delayHandler1Started = true;
				}  	 
			}
			else { 
				//Not the highest Intention. It will wait and then check the group list for any 
				// available groups and try to connect to the group owner of each group.
				appendStatus("Not the highest Intention");
				appendStatus("will wait for " + calculateWait() + " miliseconds"); 
				delayHandler2 = cycle + (calculateWait()/cycleLength);
				delayHandler2Started = true;    
			}
		}  
		SMARTSelected = false;
		appendStatus("SMART selected= false");
	}

	// Group Creation
	private void createGroup(){
		appendStatus("Group List: " + groupList);
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
		for (WifiP2pDevice device: peerList){
			macAddressList.add(device.deviceAddress);
		}
		// Updating Intention List and remove all intentions from devices that are not available anymore
		for (Iterator<Entry<Double, String>> itr = intentionList.entrySet().iterator(); itr.hasNext();)
		{
			Map.Entry<Double, String> entrySet = (Entry<Double, String>) itr.next();
			String value = entrySet.getValue();
			if (!macAddressList.contains((value)))
			{
				itr.remove();               
			}
		}
		intentionList.put(intention, p2pMacAddress);  // adding the Intention of this device again because it was remove at the above procedure

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
			delayHandler4 = cycle + (calculateWait()/cycleLength);
			delayHandler4Started = true;
		}
	}

	// Update Peer List
	public void  updatePeerList(){
		// updating peer list
		manager.requestPeers();
		manager.requestConnectionInfo();
		//manager.requestGroupInfo(channel, this);
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
			appendStatus("Connected as group owner");
			appendStatus("Group formed= " + isGroupFormed);
			manager.requestGroupInfo();   // will be infomed at the onGroupInfoAvailable callback whenever it was ready    	
			// if it is not a GO, run a client socket handler
		} else if (isGroupFormed){
			appendStatus("Group formed= " + p2pInfo.groupFormed);        	
			isGroupOwner = false;
		}
	}

	// Status of the program which will be shown at the bottom
	public void appendStatus(String status) {
		Visualizer.print(status);
	}

//	public void setChatManager(ChatManager obj) {
//		chatManager = obj;
//		chatClientList.add(chatManager);
//		if(isGroupOwner){
//			appendStatus(chatClientList.size() + "th peer address: " +  chatManager.getRemoteAddress());  
//			requestClientApList(chatManager);
//		}
//		else{
//			appendStatus("Connected as peer to: " + chatManager.getRemoteAddress());
//		}
//	}

//	public void requestClientApList(ChatManager chatManager){
//		//		magnetMessage message = new magnetMessage();
//		//		message.what = ALCON_APLIST_REQUEST;
//		//		Bundle bundle = new Bundle();
//		//		bundle.putString(chatManager.getLocalAddress(), chatManager.getRemoteAddress());
//		//		message.setData(bundle);
//		//		ArrayList<String> Testlist = new ArrayList<String>();
//		//		Testlist.add("SALAM");
//		//		chatManager.writeObject(Testlist);
//		//		appendStatus("AP request sent!");
//	}
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

		List<ScanResult> wifiScanResults = new ArrayList<ScanResult>();
		wifiScanResults = wifiManager.getScanResults();

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

	// Here Group Owner decieds intelligently and tell each client to connect to what other APs(GOs)
	private void decideGroupConnection(List<WTAClass> interfaceList){
		appendStatus("decideGroupConnection");
		Node root = new Node("root");

		// Now instantiate a Tree with the above test information
		Tree tree = new Tree("Tree0", root);
		tree.generateTree(interfaceList);

		// Extracting the List of all external proximity groups from interfaceList
		List<String> groupsInInterfaceList = new ArrayList<String>(); 
		for(WTAClass tempWTA: interfaceList){
			for(ScanResult tempString: tempWTA.getGroupSeen()){
				if (!groupsInInterfaceList.contains(tempString.BSSID)){
					groupsInInterfaceList.add(tempString.BSSID);
				}
			}
		}
		// Find possible solutions by passing the tree to the findNodeDFS method
		List<HashMap<String, String>> finalResult = new ArrayList<HashMap<String, String>>();
		finalResult = findNodeDFS(root);

		// rearrang the finalResult HashMap in a better human readable format
		List<HashMap<String, String>> finalResultB = new ArrayList<HashMap<String, String>>();
		for(HashMap<String, String> tempHash: finalResult){
			HashMap<String, String> newHash = new HashMap<String, String>();
			for (String tempString: groupsInInterfaceList){
				int stage = 0;
				if(tempHash.containsKey(tempString)){
					Pattern pattern = Pattern.compile("_(.*?)_");
					Matcher matcher = pattern.matcher(tempHash.get(tempString));
					if (matcher.find())
					{
						stage = Integer.parseInt((matcher.group(1).substring(0,1)));
					}

					String newString = interfaceList.get(stage-1).getInterfaceName();
					newHash.put(tempString, newString); 
				}
			}
			finalResultB.add(newHash);				
		}

		// Print the reruned solutions (finalResult => normal; finalResultB => human redeable format)
		System.out.println(finalResult.size() + " different combination(s) are possible to connect to maximum " + 
				finalResult.get(0).size() + " groups (out of " + groupsInInterfaceList.size() + ")" + 
				" by means of " + interfaceList.size() + " interfaces");
		for (HashMap<String, String> hash: finalResultB){
			System.out.println(hash);
		}

		// Pass all possible solutions to the solutionOptimizer method to find the optimized one based on user defined metrics
		HashMap<String, String> optimizedSolution = new HashMap<String, String>();
		optimizedSolution = solutionOptimizer(finalResultB, interfaceList);
		//Log.d("optimizedSolution", "\nThe optimized solution is:\n " + optimizedSolution);	
	}

	public static List<HashMap<String, String>> findNodeDFS(final Node root) {
		int i=0;
		int stage = 0;
		int maxSize=0;
		String[] pathList = new String[500];

		List<HashMap<String, String>> finalSolution = new ArrayList<HashMap<String, String>>();


		@SuppressWarnings("serial")
		Stack<Node> stack = new Stack<Node>(){{
			add(root);  
		}};
		while (!stack.isEmpty()) {
			Node current = stack.pop();
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
//			while (it.hasNext()) {
//				@SuppressWarnings("rawtypes")
//				HashMap.Entry pairs = (HashMap.Entry)it.next();
//				double RSSI= 0;
//				double groupValue = 1;
//
//				// getting the RSSI and group Value for current pairs (connection)
//				for (WTAClass tempWTA: interfaceList){
//					if(tempWTA.getInterfaceName().equals(pairs.getValue())){
//						RSSI = (double)tempWTA.RSSIMap.get(pairs.getKey());
//						groupValue = (double)tempWTA.groupValue.get(pairs.getKey());
//
//					}
//				}
//				// calculating one of the line of the algorithm (Weapon Target Assignment)
//				newdouble = newdouble + groupValue*(1-(RSSI/100));
//
//				//pairs.getKey()  pairs.getValue();
//				//it.remove(); // avoids a ConcurrentModificationException
//			}



			if(wtaResult == 0 || newdouble<wtaResult){
				wtaResult = newdouble;
				optimizedSolution = tempHash;
				//System.out.println(wtaResult + " " + optimizedSolution);

			}
		}
		return optimizedSolution;
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
		if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
			updateWifiAPs(); 

		}

	}

	@Override
	public void onGroupInfoAvailable(WifiP2pGroup group) {

		groupedPeerList.clear();   // Clear All elements because the peer changed may be caused by peer disappearance
		 // Add all peers found to the peerList 
		for(peersim.core.Node cNode: group.getNodeList()){
			WifiP2pDevice newDevice = new WifiP2pDevice(cNode, p2pInfoPid);
			groupedPeerList.add(newDevice);
		}       			
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
	}
		@Override
		public void handleMessage(callbackMessage msg) {
			switch (msg.what)
			{
			case MESSAGE_READ:
				break;
			case MY_HANDLE:
				break;
			}

		}

		@Override
		public void processEvent(peersim.core.Node arg0, int arg1, Object arg2) {
			// TODO Auto-generated method stub
			
		}


	}
