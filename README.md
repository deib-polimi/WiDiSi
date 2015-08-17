# WiDiSi
A WiFi Direct Simulator

WiDiSi is an open-source system-level simulator for Wi-Fi Direct networks. WiDiSi is based on [PeerSim](http://peersim.sourceforge.net/), a widely-used, open-source simulation framework for large-scale peer to peer networks. WiDiSi is presenting an environment that Wi-Fi direct applications in Android can be easily tested in large-scale dynamic scenarios. A user can create and modify various network parameters via a configuration text file before the simulation starts. 

The WiFi Direct API in WiDiSi is very similar to the [WiFi Peer-to-Peer API in Android](http://developer.android.com/guide/topics/connectivity/wifip2p.html). However, some changes should be applied before you test an Android application in the simulation environment. These changes have been described in this paper.

WiDiSi supports important functionalities of WiFi direct. These include Standard and autonomous group formation, device and service advertisement/discovery, group termination and socket delivery. all these functions follow the [WiFi P2P specification]( https://www.wi-fi.org/discover-wi-fi/specifications).
The other functionalities in WiFi Direct specification like Authentication, Encryption, and Power Management are modeled using configurable delays. 

To run the example following steps should be taken:
1-	Download the WiDiSi source code from this website and import it to the Eclipse
2-	Add all provided libraries inside the lib folder to the project by right clicking on the Project => properties => Java Build Path => Libraries => Add External JARs
3-	Modify the configuration text (wifip2pconfig.txt) file inside the configuration folder to change all settings of the network if needed
4-	Pass the path of this configuration file as an argument to the main class by right clicking on the project => Run As=> Run Configurations => (x)Arguments --- and put the following arguments in the Program Arguments: “configuration\\wifip2pconfig.txt” 
5-	Run the simulator
6-	If asking which simulator to run, choose the class inside the WiFi direct package.
