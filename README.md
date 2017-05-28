# WiDiSi
A WiFi Direct Simulator

WiDiSi is an open-source system-level simulator for Wi-Fi Direct networks. WiDiSi is based on [PeerSim](http://peersim.sourceforge.net/), a widely-used, open-source simulation framework for large-scale peer to peer networks. WiDiSi is presenting an environment that Wi-Fi direct applications in Android can be easily tested in large-scale dynamic scenarios. A user can create and modify various network parameters via a configuration text file before the simulation starts. 

The WiFi Direct API in WiDiSi is very similar to the [WiFi Peer-to-Peer API in Android](http://developer.android.com/guide/topics/connectivity/wifip2p.html). However, some changes should be applied before you test an Android application in the simulation environment. A first limitation is that WiDiSi is a single-threaded simulator; this means that all its internal components synchronize around Peersim cycles. Android applications, on the other hand, can be multi-threaded. To solve this discrepancy one can decide to use PeerSim in a hybrid mode, i.e., one in which both its cycle-driven and event-driven engines are used. A more detailed discussion on how to achieve this is provided at https://github.com/nasser1941/WiDiSi/blob/master/Multi-Threaded. A second limitation is that we only support Bonjour service discovery; we do not support UPnP or other service discovery mechanisms. A third limitation is that the channel that connects the application to the Wi-Fi P2P framework and channelListener are not available since they are not needed in our simulations. In Android an instance of a Channel is obtained by calling a specific initialize method. As a result, the initialize method is also not needed in our Simulator. A fourth limitation regards Android's use of listeners for asynchronous method calls to the API. In Android's implementation responses from an application are dealt with through listener callbacks provided by the application itself. There are two kinds of listeners that are used. Some listeners are used to inform the application whether a call to the framework has been successful or not. We assume that all method calls are always successful and therefore, did not implement these listeners. Others are used to inform the application that the required information is ready to be picked up. These are important to us, and we support the following ones:

WifiP2pManager.ConnectionInfoListener: This is the interface for when connection info is available.
WifiP2pManager.DnsSdServiceResponseListener: This is the interface for when a Bonjour service discovery response is received.
WifiP2pManager.DnsSdTxtRecordListener: This is the interface for when a Bonjour TXT record is available for a service.
WifiP2pManager.GroupInfoListener: This is the interface for when group info is available.
WifiP2pManager.PeerListListener: This is the interface for when peer lists are updated.

WiDiSi supports important functionalities of WiFi direct. These include Standard and autonomous group formation, device and service advertisement/discovery, group termination and simple socket delivery. all these functions follow the [WiFi P2P specification]( https://www.wi-fi.org/discover-wi-fi/specifications).
The other functionalities in WiFi Direct specification like Authentication, Encryption, and Power Management are modeled using configurable delays. These user-configurable delays are summerized as follow:

1- SwitchingDelay is caused by switching the channels during the search phase. It represents the average time that two peers take to find a common channel;

2- ChannelDelay mimics the time needed for the physical propagation of signals. It is the amount of time needed to successfully exchange one frame between two peers at the MAC layer;

3- AuthenticationDelay is the user-dependent delay for authentication. It is the amount of time needed for the user to accept an invitation and perform the provisioning phase;

4- EncryptionDelay is additional delay caused by the AES-CCMP encryption process;

5- PowerManagementDelay is the amount of time that a device stays in sleeping mode;

6- InternalProcessingDelay is the time taken to process a basic action.  

To run the example following steps should be taken:
1-	Download the WiDiSi source code from this website and import it to the Eclipse

2-	Add all provided libraries inside the lib folder to the project by right clicking on the Project => properties => Java Build Path => Libraries => Add External JARs

3-	Modify the configuration text (wifip2pconfig.txt) file inside the configuration folder to change all settings of the network if needed

4-	Pass the path of this configuration file as an argument to the main class by right clicking on the project => Run As=> Run Configurations => (x)Arguments --- and put the following arguments in the Program Arguments: configuration\\wifip2pconfig.txt

5-	Run the simulator

6-	If asking which simulator to run, choose the class inside the peerSimEngine package.

7- You can change the control parameters like the speed of node movement or the number of nodes in the control panel. First, modify the value and then push SET once for the values to take effect.

[The published paper](http://ieeexplore.ieee.org/document/7565169/)

[A Live Demo](https://github.com/nasser1941/WiDiSi/blob/master/demoVideo.asf)

[Screen Shot 1](https://github.com/nasser1941/WiDiSi/blob/master/shoppingMall.png)

[Screen Shot 2: Roles](https://github.com/nasser1941/WiDiSi/blob/master/simulator.png)
