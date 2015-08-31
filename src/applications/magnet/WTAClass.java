package applications.magnet;

import java.util.HashMap;
import java.util.List;

import wifi.ScanResult;



public class WTAClass {

	private List<ScanResult> groupSeen;
	private String interfaceName;
	public HashMap <String, Integer> RSSIMap;     // <Group name, RSSI Value>  0 =< RSSI >= 100
	public HashMap <String, Integer> groupValue;  // <Group name, group Value (the value of target i in WTA)> ----  1 =< value >= 10
	
	public WTAClass(String name){
		setInterfaceName(name);
		RSSIMap = new HashMap<String, Integer>();
		groupValue = new HashMap<String, Integer>();
	}

	public List<ScanResult> getGroupSeen() {
		return groupSeen;
	}

	public void setGroupSeen(List<ScanResult> groupSeen) {
		this.groupSeen = groupSeen;
		
		// right now we do not have group value so the group values are all 1
		// later we will add a real value for each group based on real metrics like the number of other groups that this group is connected to
		for (ScanResult tempString: groupSeen){
			RSSIMap.put(tempString.SSID, tempString.level);
			groupValue.put(tempString.SSID, tempString.level);
		}
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}
}
	