# WiDiSi
A WiFi Direct Simulator

To run the example following steps should be taken:
1-	Download the WiDiSi source code from this website and import it to the Eclipse
2-	Add all provided libraries inside the lib folder to the project by right clicking on the Project => properties => Java Build Path => Libraries => Add External JARs
3-	Modify the configuration text (wifip2pconfig.txt) file inside the configuration folder to change all settings of the network if needed
4-	Pass the path of this configuration file as an argument to the main class by right clicking on the project => Run As=> Run Configurations => (x)Arguments --- and put the following arguments in the Program Arguments: “configuration\\wifip2pconfig.txt” 
5-	Run the simulator
6-	If asking which simulator to run, choose the class inside the wifidirect package.
