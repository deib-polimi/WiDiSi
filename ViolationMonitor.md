We check the behavior of WiDiSi using rules and monitors. 
The aim was to find out whether the simulator behaves like a real Wi-Fi Direct device in a Wi-Fi direct network. 
To this end, we defined over twenty rules that a Wi-Fi Direct device must comply with, as well as rules that state what the simulator must not do. 
These rules are further divided into three categories: 
general rules that are forced by radio propagation in space, 
Wi-Fi Direct rules that are defined by Wi-Fi P2P specification, 
and OS limitations that are forced by Android. 
In the following, the most important ones have been stated:

General rules:
• A peer cannot connect to another peer outside its proximity range
• A group cannot consist of more than pre-defined number of peers
• A peer cannot discover more than pre-defined number of devices and services
• A peer cannot discover devices and services outside its radio range

Wi-Fi Direct rules:
• A client cannot connect to another client directly
• A peer cannot discover other peers or services if they have not started peer discovery
• A group formation cannot take more than 15 seconds
• A peer cannot have access to group data if it left the group or if the group is not available anymore
• A group owner is always discoverable
• When two P2P devices negotiate to decide the GO role, that device with higher intention should become the group owner.
• If the group owner fails, the group should be terminated
• GroupOwner roles cannot be transferred inside a group before terminating the group
• Events generation rules as discussed in the paper

Android rules: 
• A Client cannot communicate with another client in the same group directly (the message should be passed through the group owner - channel delay will be applied)
• The discovery remains active until a connection is initiated, or a P2P group is formed (for clients)- (this rule is not fixed in different android APIs)

These rules cover the most important aspects of the Wi-Fi Direct behavior.