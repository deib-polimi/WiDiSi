package applications.magnet.Routing;

import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;
import visualization.Visualizer;

public class statsCollector implements Control{

	private int aodvroutePid;
	private long aodvPacketsSend = 0;
	private long helloPacketsSend = 0;
	private long ravaPacketsSend = 0;
	private long rerrPacketsSend = 0;
	private long rreqPacketsSend = 0;
	private long rrepPacketsSend = 0;
	private long aodvPacketsReceive = 0;
	private long helloPacketsReceive = 0;
	private long ravaPacketsReceive = 0;
	private long rerrPacketsReceive = 0;
	private long rreqPacketsReceive = 0;
	private long rrepPacketsReceive = 0;
	private long netMsgsPackets = 0;
	private long rrepOrigPackets = 0;
	private long rreqOrigPackets = 0;
	private long rsucPackets = 0;


	public statsCollector(String prefix){
		aodvroutePid 		= Configuration.getPid(prefix + "." + "aodvroute");
	}

	@Override
	public boolean execute() {
		
		aodvPacketsSend = 0;
		helloPacketsSend = 0;
		ravaPacketsSend = 0;
		rerrPacketsSend = 0;
		rreqPacketsSend = 0;
		rrepPacketsSend = 0;
		aodvPacketsReceive = 0;
		helloPacketsReceive = 0;
		ravaPacketsReceive = 0;
		rerrPacketsReceive = 0;
		rreqPacketsReceive = 0;
		rrepPacketsReceive = 0;
		netMsgsPackets = 0;
		rrepOrigPackets = 0;
		rreqOrigPackets = 0;
		rsucPackets = 0;
		
		for(int i=0; i<Network.size(); i++){
			RouteAodv route = (RouteAodv) Network.get(i).getProtocol(aodvroutePid);
			if(route.stats!=null){
				netMsgsPackets = route.stats.netMsgs + netMsgsPackets;
				rrepOrigPackets = route.stats.rrepOrig + rrepOrigPackets;
				rreqOrigPackets = route.stats.rreqOrig + rreqOrigPackets;
				rsucPackets = route.stats.rreqSucc + rsucPackets;

				aodvPacketsSend = route.stats.send.aodvPackets + aodvPacketsSend;
				helloPacketsSend = route.stats.send.helloPackets + helloPacketsSend;
				ravaPacketsSend = route.stats.send.ravaPackets + ravaPacketsSend;
				rerrPacketsSend = route.stats.send.rerrPackets + rerrPacketsSend;
				rreqPacketsSend = route.stats.send.rreqPackets + rreqPacketsSend;
				rrepPacketsSend = route.stats.send.rrepPackets + rrepPacketsSend;

				aodvPacketsReceive = route.stats.recv.aodvPackets + aodvPacketsReceive;
				helloPacketsReceive = route.stats.recv.helloPackets + helloPacketsReceive;
				ravaPacketsReceive = route.stats.recv.ravaPackets + ravaPacketsReceive;
				rerrPacketsReceive = route.stats.recv.rerrPackets + rerrPacketsReceive;
				rreqPacketsReceive = route.stats.recv.rreqPackets + rreqPacketsReceive;
				rrepPacketsReceive = route.stats.recv.rrepPackets + rrepPacketsReceive;
			}

		}

		Visualizer.mDwindow.aodvPacketsSend.setText(String.valueOf(aodvPacketsSend));
		Visualizer.mDwindow.helloPacketsSend.setText(String.valueOf(helloPacketsSend));
		Visualizer.mDwindow.ravaPacketsSend.setText(String.valueOf(ravaPacketsSend));
		Visualizer.mDwindow.rerrPacketsSend.setText(String.valueOf(rerrPacketsSend));
		Visualizer.mDwindow.rreqPacketsSend.setText(String.valueOf(rreqPacketsSend));
		Visualizer.mDwindow.rrepPacketsSend.setText(String.valueOf(rrepPacketsSend));

		//receive stats
		Visualizer.mDwindow.aodvPacketsReceive.setText(String.valueOf(aodvPacketsReceive));
		Visualizer.mDwindow.helloPacketsReceive.setText(String.valueOf(helloPacketsReceive));
		Visualizer.mDwindow.ravaPacketsReceive.setText(String.valueOf(ravaPacketsReceive));
		Visualizer.mDwindow.rerrPacketsReceive.setText(String.valueOf(rerrPacketsReceive));
		Visualizer.mDwindow.rreqPacketsReceive.setText(String.valueOf(rreqPacketsReceive));
		Visualizer.mDwindow.rrepPacketsReceive.setText(String.valueOf(rrepPacketsReceive));

		//general stats
		Visualizer.mDwindow.netMsgsPackets.setText(String.valueOf(netMsgsPackets));
		Visualizer.mDwindow.rrepOrigPackets.setText(String.valueOf(rrepOrigPackets));
		Visualizer.mDwindow.rreqOrigPackets.setText(String.valueOf(rreqOrigPackets));
		Visualizer.mDwindow.rsucPackets.setText(String.valueOf(rsucPackets));
		return false;
	}

}
