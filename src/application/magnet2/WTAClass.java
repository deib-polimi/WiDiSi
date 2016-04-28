package application.magnet2;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final int maxLen = 10;
		return "WTAClass [groupSeen="
				+ (groupSeen != null ? toString(groupSeen, maxLen) : null)
				+ ", interfaceName="
				+ interfaceName
				+ ", RSSIMap="
				+ (RSSIMap != null ? toString(RSSIMap.entrySet(), maxLen)
						: null)
				+ ", groupValue="
				+ (groupValue != null ? toString(groupValue.entrySet(), maxLen)
						: null) + "]";
	}

	private String toString(Collection<?> collection, int maxLen) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		int i = 0;
		for (Iterator<?> iterator = collection.iterator(); iterator.hasNext()
				&& i < maxLen; i++) {
			if (i > 0)
				builder.append(", ");
			builder.append(iterator.next());
		}
		builder.append("]");
		return builder.toString();
	}

	public List<ScanResult> getGroupSeen() {
		return groupSeen;
	}

	public void setGroupSeen(List<ScanResult> groupSeen) {
		this.groupSeen = groupSeen;
		
		// right now we do not have group value so the group values are all 1
		// later we will add a real value for each group based on real metrics like the number of other groups that this group is connected to
		for (ScanResult tempString: groupSeen){
			RSSIMap.put(tempString.BSSID, tempString.level);
			groupValue.put(tempString.BSSID, tempString.level);
		}
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}
}
	