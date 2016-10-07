package visualization;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JSeparator;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import visualization.Visualizer;
import wifidirect.nodemovement.NodeMovement;
import javax.swing.JRadioButton;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainDebug {

	public JFrame frame;
	public JTextField realTime;
	public JTextField simTime;
	public JTextField netSize;
	public JTextField numGroups;
	public JTextField maxNodeSpeed;
	public JTextField minNodeSpeed;
	public JTextField numConnectedNodes;
	public JTextField numclients;
	public JTextField curCycle;
	public JTextField cycleLengthInfo;
	public JTextField timeOutInfo;
	public JTextField maxNeighbor;
	public JTextField avShortestPath;
	public JTextField shortestPathCount;
	public JTextField textField_14;
	public JTextField textField_15;
	public JTextField ruleOneCheck;
	public JTextField ruleTwoCheck;
	public JTextField ruleThreeCheck;
	public JTextField ruleFourCheck;
	public JTextField ruleFiveCheck;
	public JTextField ruleSixCheck;
	public JTextField ruleSevenCheck;
	public JTextField ruleEightCheck;
	public JTextField ruleNineCheck;
	public JTextField ruleTenCheck;
	public JTextField ruleElevenCheck;
	public JTextField ruleTwelveCheck;
	public JTextField cycleLengthControl;
	public JTextField fieldLengthControl;
	public JTextField maxSpeedControl;
	public JTextField minSpeedControl;
	public JTextField radioRangeControl;
	public JTextField maxClientControl;
	public JTextField maxNetSizeControl;
	public JTextField minNetSizeControl;
	public JTextField aodvPacketsSend;
	public JTextField rreqPacketsSend;
	public JTextField rerrPacketsSend;
	public JTextField ravaPacketsSend;
	public JTextField rrepPacketsSend;
	public JTextField helloPacketsSend;
	public JTextField rsucPackets;
	public JTextField netMsgsPackets;
	public JTextField rreqOrigPackets;
	public JTextField aodvPacketsReceive;
	public JTextField helloPacketsReceive;
	public JTextField rreqPacketsReceive;
	public JTextField rrepPacketsReceive;
	public JTextField rerrPacketsReceive;
	public JTextField ravaPacketsReceive;
	public JTextField rrepOrigPackets;
	public JTextField singleNodeMov;
	public JRadioButton vizNetBut, vizProxyBut, pauseSimBut, anyNodeMove, singleNodeSel; 
	public JRadioButton rdbtnExpand;

//	/**
//	 * Launch the application.
//	 */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					MainDebug window = new MainDebug();
//					window.frame.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}

	/**
	 * Create the application.
	 */
	public MainDebug() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 472, 769);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblCurrentRealTime = new JLabel("Current Real Time (S)");
		lblCurrentRealTime.setBounds(10, 26, 139, 14);
		frame.getContentPane().add(lblCurrentRealTime);
		
		realTime = new JTextField();
		realTime.setEditable(false);
		realTime.setBounds(178, 23, 86, 20);
		frame.getContentPane().add(realTime);
		realTime.setColumns(10);
		
		JLabel lblSimulatorTimes = new JLabel("Simulator Time (S)");
		lblSimulatorTimes.setBounds(10, 48, 106, 14);
		frame.getContentPane().add(lblSimulatorTimes);
		
		simTime = new JTextField();
		simTime.setEditable(false);
		simTime.setColumns(10);
		simTime.setBounds(178, 48, 86, 20);
		frame.getContentPane().add(simTime);
		
		JLabel lblNetworkSizenodes = new JLabel("Network Size (Nodes)");
		lblNetworkSizenodes.setBounds(10, 76, 139, 14);
		frame.getContentPane().add(lblNetworkSizenodes);
		
		JLabel lblNoOfGroups = new JLabel("No. of Groups");
		lblNoOfGroups.setBounds(10, 98, 106, 14);
		frame.getContentPane().add(lblNoOfGroups);
		
		netSize = new JTextField();
		netSize.setEditable(false);
		netSize.setColumns(10);
		netSize.setBounds(178, 73, 86, 20);
		frame.getContentPane().add(netSize);
		
		numGroups = new JTextField();
		numGroups.setEditable(false);
		numGroups.setColumns(10);
		numGroups.setBounds(178, 98, 86, 20);
		frame.getContentPane().add(numGroups);
		
		JLabel lblMaxNodeSpeed = new JLabel("Max Node Speed (m/s)");
		lblMaxNodeSpeed.setBounds(10, 128, 139, 14);
		frame.getContentPane().add(lblMaxNodeSpeed);
		
		JLabel lblMinNodeSpeed = new JLabel("Min Node Speed (m/s)");
		lblMinNodeSpeed.setBounds(10, 150, 139, 14);
		frame.getContentPane().add(lblMinNodeSpeed);
		
		maxNodeSpeed = new JTextField();
		maxNodeSpeed.setEditable(false);
		maxNodeSpeed.setColumns(10);
		maxNodeSpeed.setBounds(178, 125, 86, 20);
		frame.getContentPane().add(maxNodeSpeed);
		
		minNodeSpeed = new JTextField();
		minNodeSpeed.setEditable(false);
		minNodeSpeed.setColumns(10);
		minNodeSpeed.setBounds(178, 150, 86, 20);
		frame.getContentPane().add(minNodeSpeed);
		
		JLabel lblNoOfConnected = new JLabel("Average Connected(%)");
		lblNoOfConnected.setBounds(10, 178, 139, 14);
		frame.getContentPane().add(lblNoOfConnected);
		
		JLabel lblMaxNoOf = new JLabel("Max No. of Clients per Group");
		lblMaxNoOf.setBounds(10, 200, 166, 14);
		frame.getContentPane().add(lblMaxNoOf);
		
		numConnectedNodes = new JTextField();
		numConnectedNodes.setEditable(false);
		numConnectedNodes.setColumns(10);
		numConnectedNodes.setBounds(178, 175, 86, 20);
		frame.getContentPane().add(numConnectedNodes);
		
		numclients = new JTextField();
		numclients.setEditable(false);
		numclients.setColumns(10);
		numclients.setBounds(178, 200, 86, 20);
		frame.getContentPane().add(numclients);
		
		JLabel lblCurrentPeersimCycle = new JLabel("Current Cycle");
		lblCurrentPeersimCycle.setBounds(271, 26, 106, 14);
		frame.getContentPane().add(lblCurrentPeersimCycle);
		
		JLabel lblCycleLengths = new JLabel("Cycle Length (S)");
		lblCycleLengths.setBounds(271, 48, 106, 14);
		frame.getContentPane().add(lblCycleLengths);
		
		curCycle = new JTextField();
		curCycle.setEditable(false);
		curCycle.setColumns(10);
		curCycle.setBounds(383, 26, 63, 20);
		frame.getContentPane().add(curCycle);
		
		cycleLengthInfo = new JTextField();
		cycleLengthInfo.setEditable(false);
		cycleLengthInfo.setColumns(10);
		cycleLengthInfo.setBounds(383, 48, 63, 20);
		frame.getContentPane().add(cycleLengthInfo);
		
		JLabel lblNoOfTimeout = new JLabel("No. of TimeOut");
		lblNoOfTimeout.setBounds(271, 76, 106, 14);
		frame.getContentPane().add(lblNoOfTimeout);
		
		timeOutInfo = new JTextField();
		timeOutInfo.setEditable(false);
		timeOutInfo.setColumns(10);
		timeOutInfo.setBounds(383, 73, 63, 20);
		frame.getContentPane().add(timeOutInfo);
		
		JLabel lblMaxNoOf_1 = new JLabel("Max No. of Neigh");
		lblMaxNoOf_1.setBounds(271, 101, 111, 14);
		frame.getContentPane().add(lblMaxNoOf_1);
		
		maxNeighbor = new JTextField();
		maxNeighbor.setEditable(false);
		maxNeighbor.setColumns(10);
		maxNeighbor.setBounds(383, 98, 63, 20);
		frame.getContentPane().add(maxNeighbor);
		
		JLabel lblFree = new JLabel("Av Shortest Path");
		lblFree.setBounds(271, 125, 106, 14);
		frame.getContentPane().add(lblFree);
		
		avShortestPath = new JTextField();
		avShortestPath.setToolTipText("");
		avShortestPath.setEditable(false);
		avShortestPath.setColumns(10);
		avShortestPath.setBounds(383, 125, 63, 20);
		frame.getContentPane().add(avShortestPath);
		
		JLabel lblFree_1 = new JLabel("Sh Path Count");
		lblFree_1.setBounds(271, 147, 106, 14);
		frame.getContentPane().add(lblFree_1);
		
		shortestPathCount = new JTextField();
		shortestPathCount.setToolTipText("");
		shortestPathCount.setEditable(false);
		shortestPathCount.setColumns(10);
		shortestPathCount.setBounds(383, 150, 63, 20);
		frame.getContentPane().add(shortestPathCount);
		
		JLabel lblNa = new JLabel("Connectivity (%)");
		lblNa.setBounds(271, 175, 106, 14);
		frame.getContentPane().add(lblNa);
		
		textField_14 = new JTextField();
		textField_14.setEditable(false);
		textField_14.setColumns(10);
		textField_14.setBounds(383, 175, 63, 20);
		frame.getContentPane().add(textField_14);
		
		JLabel lblNa_1 = new JLabel("Con Average (%)");
		lblNa_1.setBounds(271, 197, 106, 14);
		frame.getContentPane().add(lblNa_1);
		
		textField_15 = new JTextField();
		textField_15.setEditable(false);
		textField_15.setColumns(10);
		textField_15.setBounds(383, 200, 63, 20);
		frame.getContentPane().add(textField_15);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(10, 225, 436, 7);
		frame.getContentPane().add(separator);
		
		JLabel lblNetworkInfo = new JLabel("Network Info");
		lblNetworkInfo.setHorizontalAlignment(SwingConstants.CENTER);
		lblNetworkInfo.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNetworkInfo.setForeground(Color.RED);
		lblNetworkInfo.setBounds(178, 0, 86, 14);
		frame.getContentPane().add(lblNetworkInfo);
		
		JLabel lblViolationCheck = new JLabel("WiFi Direct Violation Monitor");
		lblViolationCheck.setHorizontalAlignment(SwingConstants.CENTER);
		lblViolationCheck.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblViolationCheck.setForeground(Color.RED);
		lblViolationCheck.setBounds(137, 225, 184, 14);
		frame.getContentPane().add(lblViolationCheck);
		
		JLabel lblRule = new JLabel("Rule 1");
		lblRule.setToolTipText("A peer cannot connect to another peer outside its proximity range");
		lblRule.setBounds(10, 255, 35, 14);
		frame.getContentPane().add(lblRule);
		
		ruleOneCheck = new JTextField();
		ruleOneCheck.setToolTipText("");
		ruleOneCheck.setEditable(false);
		ruleOneCheck.setBounds(47, 252, 46, 20);
		frame.getContentPane().add(ruleOneCheck);
		ruleOneCheck.setColumns(10);
		
		JLabel lblRule_1 = new JLabel("Rule 2");
		lblRule_1.setToolTipText("A group owner cannot connect to another group");
		lblRule_1.setBounds(10, 277, 35, 14);
		frame.getContentPane().add(lblRule_1);
		
		ruleTwoCheck = new JTextField();
		ruleTwoCheck.setToolTipText("");
		ruleTwoCheck.setEditable(false);
		ruleTwoCheck.setColumns(10);
		ruleTwoCheck.setBounds(47, 274, 46, 20);
		frame.getContentPane().add(ruleTwoCheck);
		
		JLabel lblRule_2 = new JLabel("Rule 3");
		lblRule_2.setToolTipText("This Node is a Client of another device which is not Group Owner");
		lblRule_2.setBounds(10, 300, 35, 14);
		frame.getContentPane().add(lblRule_2);
		
		ruleThreeCheck = new JTextField();
		ruleThreeCheck.setToolTipText("");
		ruleThreeCheck.setEditable(false);
		ruleThreeCheck.setColumns(10);
		ruleThreeCheck.setBounds(47, 297, 46, 20);
		frame.getContentPane().add(ruleThreeCheck);
		
		JLabel lblRule_3 = new JLabel("Rule 4");
		lblRule_3.setToolTipText("A peer cannot see more than X number of devices and services");
		lblRule_3.setBounds(98, 255, 35, 14);
		frame.getContentPane().add(lblRule_3);
		
		ruleFourCheck = new JTextField();
		ruleFourCheck.setToolTipText("");
		ruleFourCheck.setEditable(false);
		ruleFourCheck.setColumns(10);
		ruleFourCheck.setBounds(137, 252, 46, 20);
		frame.getContentPane().add(ruleFourCheck);
		
		JLabel lblRule_4 = new JLabel("Rule 5");
		lblRule_4.setBounds(98, 277, 35, 14);
		frame.getContentPane().add(lblRule_4);
		
		ruleFiveCheck = new JTextField();
		ruleFiveCheck.setToolTipText("");
		ruleFiveCheck.setEditable(false);
		ruleFiveCheck.setColumns(10);
		ruleFiveCheck.setBounds(137, 274, 47, 20);
		frame.getContentPane().add(ruleFiveCheck);
		
		JLabel lblRule_5 = new JLabel("Rule 6");
		lblRule_5.setToolTipText("A peer cannot discover other peers or services if they have not started peer discovery");
		lblRule_5.setBounds(100, 297, 35, 14);
		frame.getContentPane().add(lblRule_5);
		
		ruleSixCheck = new JTextField();
		ruleSixCheck.setToolTipText("");
		ruleSixCheck.setEditable(false);
		ruleSixCheck.setColumns(10);
		ruleSixCheck.setBounds(137, 297, 46, 20);
		frame.getContentPane().add(ruleSixCheck);
		
		JLabel lblRule_6 = new JLabel("Rule 7");
		lblRule_6.setToolTipText("A peer cannot be group owner and client at the same time");
		lblRule_6.setBounds(193, 252, 37, 14);
		frame.getContentPane().add(lblRule_6);
		
		ruleSevenCheck = new JTextField();
		ruleSevenCheck.setToolTipText("");
		ruleSevenCheck.setEditable(false);
		ruleSevenCheck.setColumns(10);
		ruleSevenCheck.setBounds(232, 249, 46, 20);
		frame.getContentPane().add(ruleSevenCheck);
		
		JLabel lblRule_7 = new JLabel("Rule 8");
		lblRule_7.setToolTipText("A group cannot consist of more than M peers");
		lblRule_7.setBounds(194, 277, 36, 14);
		frame.getContentPane().add(lblRule_7);
		
		ruleEightCheck = new JTextField();
		ruleEightCheck.setToolTipText("");
		ruleEightCheck.setEditable(false);
		ruleEightCheck.setColumns(10);
		ruleEightCheck.setBounds(232, 274, 46, 20);
		frame.getContentPane().add(ruleEightCheck);
		
		JLabel lblRule_8 = new JLabel("Rule 9");
		lblRule_8.setBounds(193, 300, 35, 14);
		frame.getContentPane().add(lblRule_8);
		
		ruleNineCheck = new JTextField();
		ruleNineCheck.setToolTipText("");
		ruleNineCheck.setEditable(false);
		ruleNineCheck.setColumns(10);
		ruleNineCheck.setBounds(232, 297, 46, 20);
		frame.getContentPane().add(ruleNineCheck);
		
		JLabel lblRule_9 = new JLabel("Rule 10");
		lblRule_9.setToolTipText("A Client cannot communicate with other client in the same group directly (bypassing GO)");
		lblRule_9.setBounds(288, 252, 46, 14);
		frame.getContentPane().add(lblRule_9);
		
		ruleTenCheck = new JTextField();
		ruleTenCheck.setToolTipText("");
		ruleTenCheck.setEditable(false);
		ruleTenCheck.setColumns(10);
		ruleTenCheck.setBounds(336, 249, 46, 20);
		frame.getContentPane().add(ruleTenCheck);
		
		JLabel lblRule_10 = new JLabel("Rule 11");
		lblRule_10.setBounds(288, 274, 46, 14);
		frame.getContentPane().add(lblRule_10);
		
		ruleElevenCheck = new JTextField();
		ruleElevenCheck.setToolTipText("");
		ruleElevenCheck.setEditable(false);
		ruleElevenCheck.setColumns(10);
		ruleElevenCheck.setBounds(336, 271, 46, 20);
		frame.getContentPane().add(ruleElevenCheck);
		
		JLabel lblRule_11 = new JLabel("Rule 12");
		lblRule_11.setBounds(288, 297, 46, 14);
		frame.getContentPane().add(lblRule_11);
		
		ruleTwelveCheck = new JTextField();
		ruleTwelveCheck.setToolTipText("");
		ruleTwelveCheck.setEditable(false);
		ruleTwelveCheck.setColumns(10);
		ruleTwelveCheck.setBounds(336, 294, 46, 20);
		frame.getContentPane().add(ruleTwelveCheck);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(10, 325, 436, 7);
		frame.getContentPane().add(separator_1);
		
		JLabel lblNetworkControl = new JLabel("Network Control");
		lblNetworkControl.setHorizontalAlignment(SwingConstants.CENTER);
		lblNetworkControl.setForeground(Color.RED);
		lblNetworkControl.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNetworkControl.setBounds(162, 328, 123, 14);
		frame.getContentPane().add(lblNetworkControl);
		
		JLabel lblCycleLenghtms = new JLabel("Cycle Lenght (ms)");
		lblCycleLenghtms.setBounds(10, 357, 106, 14);
		frame.getContentPane().add(lblCycleLenghtms);
		
		cycleLengthControl = new JTextField();
		cycleLengthControl.setBounds(137, 354, 86, 20);
		frame.getContentPane().add(cycleLengthControl);
		cycleLengthControl.setColumns(10);
		
		JLabel lblFieldLenghtm = new JLabel("Field Lenght (m)");
		lblFieldLenghtm.setBounds(10, 385, 106, 14);
		frame.getContentPane().add(lblFieldLenghtm);
		
		fieldLengthControl = new JTextField();
		fieldLengthControl.setColumns(10);
		fieldLengthControl.setBounds(137, 382, 86, 20);
		frame.getContentPane().add(fieldLengthControl);
		
		JLabel lblMaxNodeSpeed_1 = new JLabel("Max Speed (m/s)");
		lblMaxNodeSpeed_1.setBounds(10, 413, 128, 14);
		frame.getContentPane().add(lblMaxNodeSpeed_1);
		
		maxSpeedControl = new JTextField();
		maxSpeedControl.setColumns(10);
		maxSpeedControl.setBounds(137, 410, 86, 20);
		frame.getContentPane().add(maxSpeedControl);
		
		JLabel lblMinNodeSpeed_1 = new JLabel("Min Speed (m/s)");
		lblMinNodeSpeed_1.setBounds(10, 439, 123, 14);
		frame.getContentPane().add(lblMinNodeSpeed_1);
		
		minSpeedControl = new JTextField();
		minSpeedControl.setColumns(10);
		minSpeedControl.setBounds(137, 436, 86, 20);
		frame.getContentPane().add(minSpeedControl);
		
		JLabel lblRadioRangem = new JLabel("Radio Range (m)");
		lblRadioRangem.setBounds(233, 356, 106, 14);
		frame.getContentPane().add(lblRadioRangem);
		
		radioRangeControl = new JTextField();
		radioRangeControl.setColumns(10);
		radioRangeControl.setBounds(360, 353, 86, 20);
		frame.getContentPane().add(radioRangeControl);
		
		JLabel lblMaxNoOf_2 = new JLabel("Max No. of Clients");
		lblMaxNoOf_2.setToolTipText("Maximum Number of Clients per WiFi Direct group");
		lblMaxNoOf_2.setBounds(233, 384, 106, 14);
		frame.getContentPane().add(lblMaxNoOf_2);
		
		maxClientControl = new JTextField();
		maxClientControl.setColumns(10);
		maxClientControl.setBounds(360, 381, 86, 20);
		frame.getContentPane().add(maxClientControl);
		
		JLabel lblMaxNetworkSize = new JLabel("Max Network Size");
		lblMaxNetworkSize.setBounds(233, 412, 123, 14);
		frame.getContentPane().add(lblMaxNetworkSize);
		
		maxNetSizeControl = new JTextField();
		maxNetSizeControl.setColumns(10);
		maxNetSizeControl.setBounds(360, 409, 86, 20);
		frame.getContentPane().add(maxNetSizeControl);
		
		JLabel lblMinNetworkSize = new JLabel("Min Network Size");
		lblMinNetworkSize.setBounds(233, 438, 123, 14);
		frame.getContentPane().add(lblMinNetworkSize);
		
		minNetSizeControl = new JTextField();
		minNetSizeControl.setColumns(10);
		minNetSizeControl.setBounds(360, 435, 86, 20);
		frame.getContentPane().add(minNetSizeControl);
		
		vizNetBut = new JRadioButton("Visualize Network");
		vizNetBut.setBounds(20, 460, 129, 23);
		frame.getContentPane().add(vizNetBut);
		
		vizProxyBut = new JRadioButton("Visualize Proximity");
		vizProxyBut.setBounds(155, 460, 149, 23);
		frame.getContentPane().add(vizProxyBut);
		
		pauseSimBut = new JRadioButton("Pause Simulation");
		pauseSimBut.setBounds(306, 459, 140, 23);
		frame.getContentPane().add(pauseSimBut);
		
		JButton btnLoad = new JButton("Load");
		btnLoad.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				cycleLengthControl.setText(String.valueOf(NodeMovement.CycleLenght));
				fieldLengthControl.setText(String.valueOf(NodeMovement.FieldLength));
				radioRangeControl.setText(String.valueOf(NodeMovement.radio));
				maxSpeedControl.setText(String.valueOf(NodeMovement.SpeedMx));
				minSpeedControl.setText(String.valueOf(NodeMovement.SpeedMn));
				vizNetBut.setSelected(Visualizer.showgroups);
				vizProxyBut.setSelected(Visualizer.isShowNetwokImage());
				singleNodeMov.setText(null);
				anyNodeMove.setSelected(false);
				singleNodeSel.setSelected(false);
			}
		});
		btnLoad.setBounds(10, 490, 89, 23);
		frame.getContentPane().add(btnLoad);
		
		JButton btnSet = new JButton("Set");
		btnSet.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Visualizer.connectivityAvg = 0;
				Visualizer.connValNum = 0;
				NodeMovement.CycleLenght = Double.parseDouble(cycleLengthControl.getText());
				NodeMovement.FieldLength = Double.parseDouble(fieldLengthControl.getText());
				NodeMovement.radio = Double.parseDouble(radioRangeControl.getText());
				NodeMovement.SpeedMx = Double.parseDouble(maxSpeedControl.getText());
				NodeMovement.SpeedMn = Double.parseDouble(minSpeedControl.getText());
				NodeMovement.anyNodeSel = anyNodeMove.isSelected();
				NodeMovement.singleNodeSel = singleNodeSel.isSelected();
				if(!anyNodeMove.isSelected() && singleNodeSel.isSelected()){
					NodeMovement.singleNodeId = Long.parseLong(singleNodeMov.getText()); 					
				}
				Visualizer.showgroups = vizNetBut.isSelected();
				Visualizer.setShowNetwokImage(vizProxyBut.isSelected());
				JOptionPane.showMessageDialog(frame, "New Seetings Set!");
			}
		});
		btnSet.setBounds(139, 490, 89, 23);
		frame.getContentPane().add(btnSet);
		
		singleNodeMov = new JTextField();
		singleNodeMov.setText(null);
		singleNodeMov.setToolTipText("Enter the ID of the node to move");
		singleNodeMov.setBounds(258, 490, 46, 20);
		frame.getContentPane().add(singleNodeMov);
		singleNodeMov.setColumns(10);
		
		anyNodeMove = new JRadioButton("Move All Nodes");
		anyNodeMove.setBounds(306, 480, 137, 20);
		frame.getContentPane().add(anyNodeMove);
		
		singleNodeSel = new JRadioButton("Move Single Node");
		singleNodeSel.setBounds(306, 500, 137, 20);
		frame.getContentPane().add(singleNodeSel);
		
		JSeparator separator_2 = new JSeparator();
		separator_2.setBounds(10, 524, 436, 7);
		frame.getContentPane().add(separator_2);
		
		JLabel lblRoutingStatistics = new JLabel("Routing Statistics");
		lblRoutingStatistics.setHorizontalAlignment(SwingConstants.CENTER);
		lblRoutingStatistics.setForeground(Color.RED);
		lblRoutingStatistics.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblRoutingStatistics.setBounds(155, 527, 149, 14);
		frame.getContentPane().add(lblRoutingStatistics);
		
		JLabel lblR = new JLabel("AODV Packets");
		lblR.setToolTipText("Any routing Packets");
		lblR.setBounds(10, 561, 74, 14);
		frame.getContentPane().add(lblR);
		
		aodvPacketsSend = new JTextField();
		aodvPacketsSend.setText("0");
		aodvPacketsSend.setEditable(false);
		aodvPacketsSend.setColumns(10);
		aodvPacketsSend.setBounds(90, 555, 46, 20);
		frame.getContentPane().add(aodvPacketsSend);
		
		JLabel labelRreq = new JLabel("RREQ Packets");
		labelRreq.setToolTipText("Route Request Packets");
		labelRreq.setBounds(141, 561, 89, 14);
		frame.getContentPane().add(labelRreq);
		
		rreqPacketsSend = new JTextField();
		rreqPacketsSend.setText("0");
		rreqPacketsSend.setToolTipText("");
		rreqPacketsSend.setEditable(false);
		rreqPacketsSend.setColumns(10);
		rreqPacketsSend.setBounds(232, 555, 46, 20);
		frame.getContentPane().add(rreqPacketsSend);
		
		JLabel lblRerrPackets = new JLabel("RERR Packets");
		lblRerrPackets.setToolTipText("Route Error Packets");
		lblRerrPackets.setBounds(288, 561, 102, 14);
		frame.getContentPane().add(lblRerrPackets);
		
		rerrPacketsSend = new JTextField();
		rerrPacketsSend.setText("0");
		rerrPacketsSend.setEditable(false);
		rerrPacketsSend.setColumns(10);
		rerrPacketsSend.setBounds(400, 552, 46, 20);
		frame.getContentPane().add(rerrPacketsSend);
		
		ravaPacketsSend = new JTextField();
		ravaPacketsSend.setText("0");
		ravaPacketsSend.setEditable(false);
		ravaPacketsSend.setColumns(10);
		ravaPacketsSend.setBounds(400, 577, 46, 20);
		frame.getContentPane().add(ravaPacketsSend);
		
		JLabel lblRavaPackets = new JLabel("RAVA Packets");
		lblRavaPackets.setToolTipText("Route Available Packets");
		lblRavaPackets.setBounds(289, 583, 101, 14);
		frame.getContentPane().add(lblRavaPackets);
		
		rrepPacketsSend = new JTextField();
		rrepPacketsSend.setText("0");
		rrepPacketsSend.setEditable(false);
		rrepPacketsSend.setColumns(10);
		rrepPacketsSend.setBounds(232, 577, 47, 20);
		frame.getContentPane().add(rrepPacketsSend);
		
		JLabel rrepPackets = new JLabel("RREP Packets");
		rrepPackets.setToolTipText("Route Reply Packets");
		rrepPackets.setBounds(141, 583, 86, 14);
		frame.getContentPane().add(rrepPackets);
		
		helloPacketsSend = new JTextField();
		helloPacketsSend.setText("0");
		helloPacketsSend.setToolTipText("");
		helloPacketsSend.setEditable(false);
		helloPacketsSend.setColumns(10);
		helloPacketsSend.setBounds(90, 577, 46, 20);
		frame.getContentPane().add(helloPacketsSend);
		
		JLabel lblHelloPacketsSend = new JLabel("Hello Packets");
		lblHelloPacketsSend.setToolTipText("Hello Packets");
		lblHelloPacketsSend.setBounds(10, 583, 74, 14);
		frame.getContentPane().add(lblHelloPacketsSend);
		
		JLabel lblRsucPackets = new JLabel("rreqSucc");
		lblRsucPackets.setToolTipText("Route Request Successfull Packets");
		lblRsucPackets.setBounds(4, 686, 54, 14);
		frame.getContentPane().add(lblRsucPackets);
		
		rsucPackets = new JTextField();
		rsucPackets.setToolTipText("");
		rsucPackets.setText("0");
		rsucPackets.setEditable(false);
		rsucPackets.setColumns(10);
		rsucPackets.setBounds(60, 683, 46, 20);
		frame.getContentPane().add(rsucPackets);
		
		JLabel lblOtherPackets1 = new JLabel("netMsgs");
		lblOtherPackets1.setToolTipText("");
		lblOtherPackets1.setBounds(111, 686, 54, 14);
		frame.getContentPane().add(lblOtherPackets1);
		
		netMsgsPackets = new JTextField();
		netMsgsPackets.setText("0");
		netMsgsPackets.setEditable(false);
		netMsgsPackets.setColumns(10);
		netMsgsPackets.setBounds(165, 683, 47, 20);
		frame.getContentPane().add(netMsgsPackets);
		
		JLabel textfiel222 = new JLabel("rreqOrig");
		textfiel222.setToolTipText("");
		textfiel222.setBounds(218, 686, 54, 14);
		frame.getContentPane().add(textfiel222);
		
		rreqOrigPackets = new JTextField();
		rreqOrigPackets.setText("0");
		rreqOrigPackets.setEditable(false);
		rreqOrigPackets.setColumns(10);
		rreqOrigPackets.setBounds(271, 683, 46, 20);
		frame.getContentPane().add(rreqOrigPackets);
		
		JLabel label = new JLabel("AODV Packets");
		label.setToolTipText("Any routing Packets");
		label.setBounds(10, 617, 74, 14);
		frame.getContentPane().add(label);
		
		JLabel label_1 = new JLabel("Hello Packets");
		label_1.setToolTipText("Hello Packets");
		label_1.setBounds(10, 639, 74, 14);
		frame.getContentPane().add(label_1);
		
		aodvPacketsReceive = new JTextField();
		aodvPacketsReceive.setText("0");
		aodvPacketsReceive.setEditable(false);
		aodvPacketsReceive.setColumns(10);
		aodvPacketsReceive.setBounds(90, 611, 46, 20);
		frame.getContentPane().add(aodvPacketsReceive);
		
		helloPacketsReceive = new JTextField();
		helloPacketsReceive.setToolTipText("");
		helloPacketsReceive.setText("0");
		helloPacketsReceive.setEditable(false);
		helloPacketsReceive.setColumns(10);
		helloPacketsReceive.setBounds(90, 633, 46, 20);
		frame.getContentPane().add(helloPacketsReceive);
		
		JLabel label_2 = new JLabel("RREQ Packets");
		label_2.setToolTipText("Route Request Packets");
		label_2.setBounds(141, 617, 89, 14);
		frame.getContentPane().add(label_2);
		
		JLabel label_3 = new JLabel("RREP Packets");
		label_3.setToolTipText("Route Reply Packets");
		label_3.setBounds(141, 639, 86, 14);
		frame.getContentPane().add(label_3);
		
		rreqPacketsReceive = new JTextField();
		rreqPacketsReceive.setToolTipText("");
		rreqPacketsReceive.setText("0");
		rreqPacketsReceive.setEditable(false);
		rreqPacketsReceive.setColumns(10);
		rreqPacketsReceive.setBounds(232, 611, 46, 20);
		frame.getContentPane().add(rreqPacketsReceive);
		
		rrepPacketsReceive = new JTextField();
		rrepPacketsReceive.setText("0");
		rrepPacketsReceive.setEditable(false);
		rrepPacketsReceive.setColumns(10);
		rrepPacketsReceive.setBounds(232, 633, 47, 20);
		frame.getContentPane().add(rrepPacketsReceive);
		
		JLabel label_4 = new JLabel("RERR Packets");
		label_4.setToolTipText("Route Error Packets");
		label_4.setBounds(288, 617, 102, 14);
		frame.getContentPane().add(label_4);
		
		JLabel label_5 = new JLabel("RAVA Packets");
		label_5.setToolTipText("Route Available Packets");
		label_5.setBounds(289, 639, 101, 14);
		frame.getContentPane().add(label_5);
		
		rerrPacketsReceive = new JTextField();
		rerrPacketsReceive.setText("0");
		rerrPacketsReceive.setEditable(false);
		rerrPacketsReceive.setColumns(10);
		rerrPacketsReceive.setBounds(400, 608, 46, 20);
		frame.getContentPane().add(rerrPacketsReceive);
		
		ravaPacketsReceive = new JTextField();
		ravaPacketsReceive.setText("0");
		ravaPacketsReceive.setEditable(false);
		ravaPacketsReceive.setColumns(10);
		ravaPacketsReceive.setBounds(400, 633, 46, 20);
		frame.getContentPane().add(ravaPacketsReceive);
		
		JLabel lblNewLabel = new JLabel("Send");
		lblNewLabel.setForeground(new Color(128, 0, 0));
		lblNewLabel.setBounds(10, 542, 46, 14);
		frame.getContentPane().add(lblNewLabel);
		
		JLabel lblReceive = new JLabel("Receive");
		lblReceive.setForeground(new Color(128, 0, 0));
		lblReceive.setBounds(10, 599, 46, 14);
		frame.getContentPane().add(lblReceive);
		
		JLabel lblAll = new JLabel("All");
		lblAll.setForeground(new Color(128, 0, 0));
		lblAll.setBounds(10, 664, 46, 14);
		frame.getContentPane().add(lblAll);
		
		JLabel label7 = new JLabel("rrepOrig");
		label7.setToolTipText("");
		label7.setBounds(331, 686, 59, 14);
		frame.getContentPane().add(label7);
		
		rrepOrigPackets = new JTextField();
		rrepOrigPackets.setText("0");
		rrepOrigPackets.setEditable(false);
		rrepOrigPackets.setColumns(10);
		rrepOrigPackets.setBounds(400, 683, 46, 20);
		frame.getContentPane().add(rrepOrigPackets);
		
		rdbtnExpand = new JRadioButton("Expand");
		rdbtnExpand.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				if(rdbtnExpand.isSelected()){
					frame.setBounds(100, 100, 472, 605);
				}else{
					frame.setBounds(100, 100, 472, 769);
				}
			}
		});
		rdbtnExpand.setBounds(337, 524, 109, 23);
		frame.getContentPane().add(rdbtnExpand);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnMenu = new JMenu("Menu");
		menuBar.add(mnMenu);
		
		JMenuItem mntmItem = new JMenuItem("Item1");
		mntmItem.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});
		mnMenu.add(mntmItem);
		
		JMenuItem mntmItem_1 = new JMenuItem("Item2");
		mntmItem_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});
		mnMenu.add(mntmItem_1);
		
		JMenuItem mntmItem_2 = new JMenuItem("Item3");
		mntmItem_2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});
		mnMenu.add(mntmItem_2);
		
		JMenuItem mntmIExit = new JMenuItem("Exit");
		mntmIExit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
			}
		});
		mnMenu.add(mntmIExit);
	}
}