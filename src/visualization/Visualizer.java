/*
 * Copyright (c) 2014-2015 SCUBE Joint Open Lab
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 2 as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 * 
 * Author: Naser Derakhshan
 * Politecnico di Milano
 * Condition: Do not remove this head
 */
package visualization;

import it.uniroma1.dis.wsngroup.gexf4j.core.EdgeType;
import it.uniroma1.dis.wsngroup.gexf4j.core.Gexf;
import it.uniroma1.dis.wsngroup.gexf4j.core.Graph;
import it.uniroma1.dis.wsngroup.gexf4j.core.Mode;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.Attribute;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.AttributeClass;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.AttributeList;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.AttributeType;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.GexfImpl;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.StaxGraphWriter;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.data.AttributeListImpl;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.viz.ColorImpl;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.viz.PositionImpl;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import javax.swing.border.EmptyBorder;

import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.io.exporter.api.ExportController;
import org.gephi.io.exporter.preview.PNGExporter;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.EdgeDefault;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.plugins.layout.geo.GeoLayout;
import org.gephi.preview.api.PreviewController;
import org.gephi.preview.api.PreviewModel;
import org.gephi.preview.api.PreviewProperty;
import org.gephi.preview.types.EdgeColor;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.ranking.api.Ranking;
import org.gephi.ranking.api.RankingController;
import org.gephi.ranking.api.Transformer;
import org.gephi.ranking.plugin.transformer.AbstractSizeTransformer;
import org.openide.util.Lookup;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Linkable;
import peersim.core.Network;
import peersim.core.Node;
import wifi.WifiManager;
import wifidirect.nodemovement.CoordinateKeeper;
import wifidirect.nodemovement.NodeMovement;
import wifidirect.p2pcore.WifiP2pGroup;
import wifidirect.p2pcore.nodeP2pInfo;

// TODO: Auto-generated Javadoc
/**
 * The Class Visualizer.
 */
public class Visualizer implements Control{

	public static List<it.uniroma1.dis.wsngroup.gexf4j.core.Node> gephiNodeGroup;
	public static ColorImpl[] pNodeColor = new ColorImpl[1000];
	/** The Constant CONNECTED. */
	public static final int CONNECTED   = 0;

	/** The Constant INVITED. */
	public static final int INVITED     = 1;

	/** The Constant FAILED. */
	public static final int FAILED      = 2;

	/** The Constant AVAILABLE. */
	public static final int AVAILABLE   = 3;

	/** The Constant UNAVAILABLE. */
	public static final int UNAVAILABLE = 4;

	/** The coordinates pid. */
	private int coordinatesPid;

	/** The linkable id. */
	private int linkableId;

	private int wifimanagerPid;

	/** The p2p info pid. */
	private int p2pInfoPid;

	/** The Cycle length. */
	public static int CycleLength = 100;

	/** The Field length. */
	private int FieldLength;

	/** The Gephi size. */
	private int GephiSize;

	/** The maxspeed. */
	private double maxspeed;

	/** The minspeed. */
	private double minspeed;

	/** The showgroups. */
	public static boolean showgroups = true;

	/** The show netwok image. */
	private static  boolean 	showNetwokImage	= false;

	/** The start time real. */
	private long 		startTimeReal = 0;

	/** The cycle. */
	private long 		cycle 	= 0;

	/** The frame. */
	private JFrame 		frame 	= null;

	/** The frame2. */
	private static JFrame 		frame2 	= null;

	/** The node. */
	private Node 		node 	= null;

	/** The image. */
	private	Image 		image	= null;

	/** The image2. */
	private	Image 		image2	= null;

	/** The label. */
	private	JLabel 		label	= null;

	/** The label2. */
	private	JLabel 		label2	= null;

	/** The output. */
	private static	JTextArea 	output	= null;

	/** The content pane. */
	private JPanel 		contentPane	= null;

	private static boolean scrollOutput = true;

	public static MainDebug mDwindow;

	/**
	 * Instantiates a new visualizer.
	 *
	 * @param prefix the prefix
	 */
	public Visualizer(String prefix){
		p2pInfoPid 		= Configuration.getPid(prefix + "." + "p2pinfo");
		linkableId 		= Configuration.getPid(prefix + "." + "linkable");
		wifimanagerPid 	= Configuration.getPid(prefix + "." + "wifimanager");
		coordinatesPid  = Configuration.getPid(prefix + "." + "coord");
	}

	/* (non-Javadoc)
	 * @see peersim.core.Control#execute()
	 */
	@Override
	public boolean execute() {
		//System.out.println("Cycle: " + cycle + " time: " + CommonState.getTime() + " phase: " + CommonState.getPhase() + " int time: " + CommonState.getIntTime());

		CycleLength = (int)NodeMovement.CycleLenght;
		FieldLength = (int)NodeMovement.FieldLength;
		GephiSize	= (int)(NodeMovement.FieldLength/20);
		maxspeed 	= NodeMovement.SpeedMx; // meter/sec
		minspeed	= NodeMovement.SpeedMn; // meter/sec


		if(cycle%5==0 && showgroups){
			showGroups();
		}
		if(cycle%5==0 && isShowNetwokImage()){
			showNetwork();
		}

		if(cycle==0){

			mDwindow = new MainDebug();
			mDwindow.frame.setVisible(true);
			mDwindow.frame.setAlwaysOnTop(true);

			mDwindow.timeOutInfo.setText(String.valueOf(0));
			mDwindow.ruleOneCheck.setText(String.valueOf(0));
			mDwindow.ruleTwoCheck.setText(String.valueOf(0));
			mDwindow.ruleThreeCheck.setText(String.valueOf(0));
			mDwindow.ruleFourCheck.setText(String.valueOf(0));
			mDwindow.ruleFiveCheck.setText(String.valueOf(0));
			mDwindow.ruleSixCheck.setText(String.valueOf(0));
			mDwindow.ruleSevenCheck.setText(String.valueOf(0));
			mDwindow.ruleEightCheck.setText(String.valueOf(0));
			mDwindow.ruleNineCheck.setText(String.valueOf(0));
			mDwindow.ruleTenCheck.setText(String.valueOf(0));
			mDwindow.ruleElevenCheck.setText(String.valueOf(0));
			mDwindow.ruleTwelveCheck.setText(String.valueOf(0));			
			mDwindow.cycleLengthControl.setText(String.valueOf(NodeMovement.CycleLenght));
			mDwindow.fieldLengthControl.setText(String.valueOf(NodeMovement.FieldLength));
			mDwindow.radioRangeControl.setText(String.valueOf(NodeMovement.radio));
			mDwindow.maxSpeedControl.setText(String.valueOf(NodeMovement.SpeedMx));
			mDwindow.minSpeedControl.setText(String.valueOf(NodeMovement.SpeedMn));
			mDwindow.maxClientControl.setText(String.valueOf(WifiP2pGroup.groupCapacity));
			mDwindow.vizNetBut.setSelected(Visualizer.showgroups);
			mDwindow.vizProxyBut.setSelected(false);
			mDwindow.pauseSimBut.setSelected(false);

			pNodeColor = new ColorImpl[1000];
			startTimeReal = System.currentTimeMillis();	
		}

		if(cycle>1){
			mDwindow.maxNodeSpeed.setText(String.valueOf(maxspeed));
			mDwindow.minNodeSpeed.setText(String.valueOf(minspeed));

			long connectedDevices = 0;
			long noGroups = 0;
			int maxClient = 0;
			int maxNeighbor = 0;

			for(int i=0; i<Network.size(); i++){
				nodeP2pInfo nodeInfo2 = (nodeP2pInfo) Network.get(i).getProtocol(p2pInfoPid);
				Linkable neighbor = (Linkable) Network.get(i).getProtocol(linkableId);

				if(neighbor.degree()>maxNeighbor){
					maxNeighbor = neighbor.degree();
				}
				if(nodeInfo2.getStatus()==CONNECTED){
					connectedDevices++;
				}
				if(nodeInfo2.isGroupOwner()){
					noGroups++;
					if(nodeInfo2.currentGroup.getGroupSize()>maxClient){
						maxClient = nodeInfo2.currentGroup.getGroupSize();
					}
				}
			}	

			// calculate the real time
			long elapsedTime = System.currentTimeMillis() - startTimeReal;
			long elapsedSeconds = elapsedTime / 1000;
			int hour = (int) (elapsedSeconds/3600);
			long remain = elapsedSeconds%3600;
			int min = (int) remain/60;
			int sec = (int) remain%60;
			mDwindow.realTime.setText(String.valueOf(hour + ":" + min + ":" + sec));

			//calculate the simulator time
			elapsedTime = (CycleLength * CommonState.getTime());
			elapsedSeconds = elapsedTime / 1000;
			hour = (int) (elapsedSeconds/3600);
			remain = elapsedSeconds%3600;
			min = (int) remain/60;
			sec = (int) remain%60;
			mDwindow.simTime.setText(String.valueOf(hour + ":" + min + ":" + sec));

			mDwindow.netSize.setText(String.valueOf(Network.size()));
			mDwindow.numGroups.setText(String.valueOf(noGroups));
			mDwindow.numConnectedNodes.setText(String.valueOf(connectedDevices));
			mDwindow.numclients.setText(String.valueOf(maxClient));
			mDwindow.maxNeighbor.setText(String.valueOf(maxNeighbor));
			mDwindow.curCycle.setText(String.valueOf(CommonState.getTime()));	
			mDwindow.cycleLengthInfo.setText(String.valueOf(CycleLength));

		}

		cycle++;
		return false;
	}

	/**
	 * Show image.
	 *
	 * @param baos the baos
	 */
	public void showImage(ByteArrayOutputStream baos){
		byte[] png = baos.toByteArray();
		try {

			image = ImageIO.read(new ByteArrayInputStream(png));

			//label.setAutoscrolls(true);
			if(frame==null){

				label = new JLabel(new ImageIcon(image));
				frame = new JFrame();
				contentPane = new JPanel();

				contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
				contentPane.setLayout(new BorderLayout(10, 10));

				output = new JTextArea();
				output.setEditable(false);
				output.setLineWrap(true);
				output.setRows(20);
				output.setColumns(40);
				output.setText("DebugWin");
				JScrollPane scrollPane = new JScrollPane();
				scrollPane.setViewportView(output);
				scrollPane.setSize(50, 50);

				JTextArea output2 = new JTextArea();
				output2.setEditable(false);
				output2.setLineWrap(true);
				output2.setRows(20);
				output2.setColumns(40);
				output2.setText("Debn");

				contentPane.add(label, BorderLayout.CENTER);
				contentPane.add(scrollPane, BorderLayout.WEST);

				frame.setContentPane(contentPane);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				frame.pack();
				//frame.setLocationRelativeTo(null);
				frame.setAlwaysOnTop(true);
				frame.setName("Proximity Show");
				frame.setResizable(true);
				frame.setTitle("Proximity Show");
				//frame.repaint();
				frame.setFocusable(true);
				//f.setLocation(100,100);
				frame.setVisible(true);

				JMenuBar mb = new JMenuBar();
				JMenu file = new JMenu("File");
				mb.add(file);
				JMenu edit = new JMenu("Edit");
				mb.add(edit);
				JMenuItem exit = new JMenuItem("Exit");
				file.add(exit);
				JMenuItem saveAs = new JMenuItem("Save As");
				file.add(saveAs);
				JMenuItem showNet = new JMenuItem("Show Network");
				file.add(showNet);
				JMenuItem hideNet = new JMenuItem("Hide Network");
				file.add(hideNet);
				JMenuItem scrollOutput = new JMenuItem("Scroll Output List");
				file.add(scrollOutput);

				frame.setJMenuBar(mb);

				showNet.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent actionEvent) {
						setShowNetwokImage(true);
					}
				});

				hideNet.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent actionEvent) {
						setShowNetwokImage(false);					
					}
				});

				exit.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent actionEvent) {
						frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
					}
				});

				scrollOutput.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent actionEvent) {
						if(Visualizer.scrollOutput){
							Visualizer.scrollOutput = false;
						}else{
							Visualizer.scrollOutput = true;
						}
					}
				});


			}else{
				frame.remove(label);
				label = new JLabel(new ImageIcon(image));
				frame.getContentPane().add(label);	
				frame.revalidate();	

			}


		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean isShowNetwokImage() {
		return showNetwokImage;
	}

	public static void setShowNetwokImage(boolean showNetwokImage) {
		Visualizer.showNetwokImage = showNetwokImage;
		if(frame2!=null)
			frame2.setVisible(showNetwokImage);
	}

	/**
	 * Show network.
	 */
	@SuppressWarnings("rawtypes")
	public void showNetwork(){
		// generating .gexf file using gexf library
		Gexf gexf = new GexfImpl();

		// getting current date for gexf - optional
		Calendar date = Calendar.getInstance();

		//Set gexf necessary fields
		gexf.getMetadata()
		.setLastModified(date.getTime())
		.setCreator("NaserDerakhshan.org")
		.setDescription("Wifi Direct Simulator");
		gexf.setVisualization(true);

		// This Graph class is taken from gexf library
		Graph graph = gexf.getGraph();
		graph.setDefaultEdgeType(EdgeType.UNDIRECTED).setMode(Mode.STATIC);

		// attribute is needed so that the gephi importer find the necessary feilds for long-lat
		AttributeList attrList = new AttributeListImpl(AttributeClass.NODE);
		graph.getAttributeLists().add(attrList);
		Attribute longitude = attrList.createAttribute("longitude", AttributeType.FLOAT, "longitude");
		Attribute latitude = attrList.createAttribute("latitude", AttributeType.FLOAT, "latitude");

		// A list of Ghefi nodes - We used long identifier to distinguish gephi Node from peerSim Node
		List<it.uniroma1.dis.wsngroup.gexf4j.core.Node> gephiNode = new ArrayList<it.uniroma1.dis.wsngroup.gexf4j.core.Node>();	
		// A HashMap that map peersim nodeID to the gephi node
		HashMap<Long, it.uniroma1.dis.wsngroup.gexf4j.core.Node> gephiMap = new HashMap<Long, it.uniroma1.dis.wsngroup.gexf4j.core.Node>();

		// Set postion of the nodes in gephi layer
		for(int i=0; i<Network.size(); i++){
			node = (Node) Network.get(i);
			nodeP2pInfo nodeInfo = (nodeP2pInfo) node.getProtocol(p2pInfoPid);
			CoordinateKeeper coordinate = (CoordinateKeeper) node.getProtocol(coordinatesPid);
			PositionImpl nodePosition = new PositionImpl();
			ColorImpl nodeColor = new ColorImpl();
			if(nodeInfo.isGroupOwner() && nodeInfo.getStatus()==CONNECTED){
				nodeColor.setB(0);
				nodeColor.setG(255);
				nodeColor.setR(0);
			}else if(!nodeInfo.isGroupOwner() && nodeInfo.getStatus()==CONNECTED){
				nodeColor.setB(255);
				nodeColor.setG(0);
				nodeColor.setR(0);	
			}else if(nodeInfo.isPeerDiscoveryStarted()){
				nodeColor.setB(150);
				nodeColor.setG(150);
				nodeColor.setR(150);
			}else if(!nodeInfo.isPeerDiscoveryStarted()){
				nodeColor.setB(0);
				nodeColor.setG(0);
				nodeColor.setR(255);	
			}
			float signx, signy;
			if((float)coordinate.getX()<0){
				signx = -1;
			}else{
				signx = 1;
			}
			if((float)coordinate.getY()<0){
				signy = -1;
			}else{
				signy = 1;
			}
			// here I check the coordinates to be sure the coordinates is not larger than 100 or -100 (double check)
			nodePosition.setX(Math.abs((float)coordinate.getX()*FieldLength)> FieldLength ? signx*FieldLength:((float)coordinate.getX()*FieldLength));
			nodePosition.setY(Math.abs((float)coordinate.getY()*FieldLength)> FieldLength ? signy*FieldLength:((float)coordinate.getY()*FieldLength));
			nodePosition.setZ(0);
			gephiNode.add(i, graph.createNode(String.valueOf(node.getID())));
			gephiNode.get(i)
			.setLabel(String.valueOf(node.getID()))
			.setSize(GephiSize)
			.setPosition(nodePosition)
			.setColor(nodeColor)
			.getAttributeValues()
			.addValue(longitude, String.valueOf(nodePosition.getX()))
			.addValue(latitude, String.valueOf(nodePosition.getY()));
			gephiMap.put(node.getID(), gephiNode.get(i));
		}

		// This section creates 4 dump gephi nodes at the 4 end of cartesian system in order to see the movement
		for(int i=0; i<4; i++){			
			PositionImpl nodePosition = new PositionImpl();
			ColorImpl nodeColor = new ColorImpl();

			nodeColor.setB(0);
			nodeColor.setG(0);
			nodeColor.setR(255);

			if(i==0){
				nodePosition.setX(FieldLength);
				nodePosition.setY(FieldLength);
				nodePosition.setZ(0);
			}else if(i==1){
				nodePosition.setX(FieldLength);
				nodePosition.setY(-FieldLength);
				nodePosition.setZ(0);
			}else if(i==2){
				nodePosition.setX(-FieldLength);
				nodePosition.setY(-FieldLength);
				nodePosition.setZ(0);
			}else if(i==3){
				nodePosition.setX(-FieldLength);
				nodePosition.setY(FieldLength);
				nodePosition.setZ(0);
			}

			gephiNode.add(Network.size()+i, graph.createNode(String.valueOf((i+1)*1000)));
			gephiNode.get(Network.size()+i)
			.setLabel(String.valueOf((i+1)*1000))
			.setSize(GephiSize)
			.setPosition(nodePosition)
			.setColor(nodeColor)
			.getAttributeValues()
			.addValue(longitude, String.valueOf(nodePosition.getX()))
			.addValue(latitude, String.valueOf(nodePosition.getY()));
			gephiMap.put((long) ((i+1)*1000), gephiNode.get(i));
		}


		// create the gephi layer by setting the neighbors
		for(int i=0; i<Network.size(); i++){
			node = (Node) Network.get(i);
			Linkable idlelink = (Linkable) node.getProtocol(linkableId);
			for(int j=0; j<idlelink.degree(); j++){	
				gephiNode.get(i).connectTo(gephiMap.get(idlelink.getNeighbor(j).getID()));			
			}
		}

		// generate output gexf file from the above mentioned graph
		StaxGraphWriter graphWriter = new StaxGraphWriter();			
		Writer out;
		File tempFile = null;
		try {
			tempFile = File.createTempFile("tempStream", ".gexf");
			tempFile.deleteOnExit();
			out =  new FileWriter(tempFile, false);
			graphWriter.writeToStream(gexf, out, "UTF-8");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// Import the created graph.gexf file to the gephi library
		//Init a project - and therefore a workspace
		ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
		pc.newProject();
		Workspace workspace = pc.getCurrentWorkspace();

		// get import controller
		ImportController importController = Lookup.getDefault().lookup(ImportController.class);

		//Import file
		Container container = null;
		try{	
			container = importController.importFile(tempFile);
			container.getLoader().setEdgeDefault(EdgeDefault.UNDIRECTED); //Force DIRECTED
			container.setAllowAutoNode(false); //Don’t create missing nodes
			container.setAutoScale(false);
		}catch (Exception ex) {
			ex.printStackTrace();
			//return true;
		}

		//Append imported data to GraphAPI
		importController.process(container, new DefaultProcessor(), workspace);

		//Get graph model of current workspace
		GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getModel();

		// Layout based on GeoLayout Plugin for Gephi
		GeoLayout layout = new GeoLayout(null);
		layout.setGraphModel(graphModel);
		layout.resetPropertiesValues();
		layout.setCentered(true);
		layout.setScale(500.0);
		layout.setProjection("Equirectangular");			
		layout.initAlgo();
		layout.goAlgo();

		//Preview
		PreviewModel model = Lookup.getDefault().lookup(PreviewController.class).getModel();
		model.getProperties().putValue(PreviewProperty.SHOW_NODE_LABELS, Boolean.TRUE);
		model.getProperties().putValue(PreviewProperty.EDGE_COLOR, new EdgeColor(Color.BLACK));
		model.getProperties().putValue(PreviewProperty.EDGE_THICKNESS, new Float(0.1f));
		model.getProperties().putValue(PreviewProperty.EDGE_CURVED, Boolean.FALSE);
		model.getProperties().putValue(PreviewProperty.EDGE_OPACITY, new Float(100));
		model.getProperties().putValue(PreviewProperty.NODE_LABEL_OUTLINE_SIZE, new Float(18));
		model.getProperties().putValue(PreviewProperty.NODE_LABEL_OUTLINE_OPACITY, new Float(50));
		model.getProperties().putValue(PreviewProperty.NODE_LABEL_FONT, model.getProperties().getFontValue(PreviewProperty.NODE_LABEL_FONT).deriveFont(8));				  

		// Ranking size of nodes by Indegree
		RankingController rankingController = Lookup.getDefault().lookup(RankingController.class);
		Ranking degreeRanking = rankingController.getModel().getRanking(Ranking.NODE_ELEMENT, Ranking.DEGREE_RANKING);
		AbstractSizeTransformer sizeTransformer = (AbstractSizeTransformer) rankingController.getModel().getTransformer(Ranking.NODE_ELEMENT, Transformer.RENDERABLE_SIZE);
		sizeTransformer.setMinSize((float) (GephiSize+(0.05*FieldLength)));
		sizeTransformer.setMaxSize((float) (GephiSize+(0.15*FieldLength)));
		rankingController.transform(degreeRanking,sizeTransformer);

		//Export and show on Jframe
		ExportController ec = Lookup.getDefault().lookup(ExportController.class);
		//ec.exportFile(new File("log/autolayout.png"));
		PNGExporter exporter = (PNGExporter) ec.getExporter("png");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ec.exportStream(baos, exporter);
		//showImage(baos);
		byte[] png = baos.toByteArray();
		try {
			image2 = ImageIO.read(new ByteArrayInputStream(png));

			//label.setAutoscrolls(true);
			if(frame2==null){
				label2 = new JLabel(new ImageIcon(image2));
				frame2 = new JFrame();
				frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame2.getContentPane().add(label2);			
				frame2.pack();
				//frame2.setLocationRelativeTo(null);
				frame2.setAlwaysOnTop(true);
				frame2.setName("Proximity Show");
				frame2.setResizable(true);
				frame2.setTitle("Proximity Show");
				//frame.repaint();
				//f.setFocusable(true);
				//f.setLocation(100,100);
				frame2.setVisible(true);
			}else{
				frame2.remove(label2);
				label2 = new JLabel(new ImageIcon(image2));
				frame2.getContentPane().add(label2);	
				frame2.revalidate();			
			}


		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Show groups.
	 */
	@SuppressWarnings("rawtypes")
	private void showGroups(){
		// generating .gexf file using gexf library
		Gexf gexf = new GexfImpl();

		// getting current date for gexf - optional
		Calendar date = Calendar.getInstance();

		//Set gexf necessary fields
		gexf.getMetadata()
		.setLastModified(date.getTime())
		.setCreator("NaserDerakhshan.org")
		.setDescription("Wifi Direct Simulator");
		gexf.setVisualization(true);

		// This Graph class is taken from gexf library
		Graph graph = gexf.getGraph();
		graph.setDefaultEdgeType(EdgeType.UNDIRECTED).setMode(Mode.DYNAMIC);

		// attribute is needed so that the gephi importer find the necessary feilds for long-lat
		AttributeList attrList = new AttributeListImpl(AttributeClass.NODE);
		graph.getAttributeLists().add(attrList);
		Attribute longitude = attrList.createAttribute("longitude", AttributeType.FLOAT, "longitude");
		Attribute latitude = attrList.createAttribute("latitude", AttributeType.FLOAT, "latitude");

		// A list of Ghefi nodes - We used long identifier to distinguish gephi Node from peerSim Node
		gephiNodeGroup = new ArrayList<it.uniroma1.dis.wsngroup.gexf4j.core.Node>();	
		// A HashMap that map peersim nodeID to the gephi node
		HashMap<Long, it.uniroma1.dis.wsngroup.gexf4j.core.Node> gephiMap = new HashMap<Long, it.uniroma1.dis.wsngroup.gexf4j.core.Node>();

		// Set postion of the nodes in gephi layer
		for(int i=0; i<Network.size(); i++){
			node = (Node) Network.get(i);
			nodeP2pInfo nodeInfo = (nodeP2pInfo) node.getProtocol(p2pInfoPid);
			WifiManager wifiManager = (WifiManager) node.getProtocol(wifimanagerPid);
			CoordinateKeeper coordinate = (CoordinateKeeper) node.getProtocol(coordinatesPid);
			PositionImpl nodePosition = new PositionImpl();
			if(cycle == 5 || cycle%20==0){
				ColorImpl nodeColor = new ColorImpl();
				if(nodeInfo.isGroupOwner() && nodeInfo.getStatus()==CONNECTED){
					nodeColor.setB(0);
					nodeColor.setG(255);
					nodeColor.setR(0);
				}else if(!nodeInfo.isGroupOwner() && wifiManager.getWifiStatus()==CONNECTED &&  nodeInfo.getStatus()==CONNECTED){
					nodeColor.setB(255);
					nodeColor.setG(0);
					nodeColor.setR(255);	
				}else if(!nodeInfo.isGroupOwner() && wifiManager.getWifiStatus()!=CONNECTED && nodeInfo.getStatus()==CONNECTED){
					nodeColor.setB(254);
					nodeColor.setG(0);
					nodeColor.setR(0);	
				}else if(!nodeInfo.isGroupOwner() && wifiManager.getWifiStatus()==CONNECTED && nodeInfo.getStatus()!=CONNECTED){
					nodeColor.setB(180);
					nodeColor.setG(180);
					nodeColor.setR(60);	
				}else if(nodeInfo.getStatus()==AVAILABLE && nodeInfo.isPeerDiscoveryStarted()){
					nodeColor.setB(150);
					nodeColor.setG(150);
					nodeColor.setR(150);
				}else if(nodeInfo.getStatus()==INVITED && nodeInfo.isPeerDiscoveryStarted()){
					nodeColor.setB(255);
					nodeColor.setG(255);
					nodeColor.setR(0);
				}else if(!nodeInfo.isPeerDiscoveryStarted()){
					nodeColor.setB(100);
					nodeColor.setG(100);
					nodeColor.setR(100);	
				}else if((nodeInfo.getStatus()==UNAVAILABLE)){
					nodeColor.setB(255);
					nodeColor.setG(255);
					nodeColor.setR(255);
				}

				pNodeColor[i] = nodeColor;
			}
			float signx, signy;
			if((float)coordinate.getX()<0){
				signx = -1;
			}else{
				signx = 1;
			}
			if((float)coordinate.getY()<0){
				signy = -1;
			}else{
				signy = 1;
			}
			// here I check the coordinates to be sure the coordinates is not larger than 1000 or -1000 (double check)
			nodePosition.setX(Math.abs((float)coordinate.getX()*FieldLength)> FieldLength ? signx*FieldLength:((float)coordinate.getX()*FieldLength));
			nodePosition.setY(Math.abs((float)coordinate.getY()*FieldLength)> FieldLength ? signy*FieldLength:((float)coordinate.getY()*FieldLength));
			nodePosition.setZ(0);
			gephiNodeGroup.add(i, graph.createNode(String.valueOf(node.getID())));
			gephiNodeGroup.get(i)
			.setLabel(String.valueOf(node.getID()))
			.setSize(GephiSize)
			.setPosition(nodePosition)
			.setColor(pNodeColor[i])
			.getAttributeValues()
			.addValue(longitude, String.valueOf(nodePosition.getX()))
			.addValue(latitude, String.valueOf(nodePosition.getY()));
			gephiMap.put(node.getID(), gephiNodeGroup.get(i));
		}

		// This section creates 4 dump gephi nodes at the 4 end of cartesian system in order to see the movement
		for(int i=0; i<4; i++){			
			PositionImpl nodePosition = new PositionImpl();
			ColorImpl nodeColor = new ColorImpl();

			nodeColor.setB(255);
			nodeColor.setG(255);
			nodeColor.setR(255);

			if(i==0){
				nodePosition.setX(FieldLength);
				nodePosition.setY(FieldLength);
				nodePosition.setZ(0);
			}else if(i==1){
				nodePosition.setX(FieldLength);
				nodePosition.setY(-FieldLength);
				nodePosition.setZ(0);
			}else if(i==2){
				nodePosition.setX(-FieldLength);
				nodePosition.setY(-FieldLength);
				nodePosition.setZ(0);
			}else if(i==3){
				nodePosition.setX(-FieldLength);
				nodePosition.setY(FieldLength);
				nodePosition.setZ(0);
			}

			gephiNodeGroup.add(Network.size()+i, graph.createNode(String.valueOf((i+1)*1000)));
			gephiNodeGroup.get(Network.size()+i)
			.setLabel(String.valueOf((i+1)*1000))
			.setSize(GephiSize)
			.setPosition(nodePosition)
			.setColor(nodeColor)
			.getAttributeValues()
			.addValue(longitude, String.valueOf(nodePosition.getX()))
			.addValue(latitude, String.valueOf(nodePosition.getY()));
			gephiMap.put((long) ((i+1)*1000), gephiNodeGroup.get(i));
		}

		// create the gephi layer by setting the neighbors
		for(int i=0; i<Network.size(); i++){
			node = (Node) Network.get(i);
			Linkable idlelink 			= (Linkable) node.getProtocol(linkableId);
			nodeP2pInfo nodeInfo 		= (nodeP2pInfo) node.getProtocol(p2pInfoPid);
			WifiManager wifiManager		= (WifiManager) node.getProtocol(wifimanagerPid);

			for(int j=0; j<idlelink.degree(); j++){	

				//gephiNode.get(i).connectTo(gephiMap.get(idlelink.getNeighbor(j).getID()));
				nodeP2pInfo neighborInfo = (nodeP2pInfo) idlelink.getNeighbor(j).getProtocol(p2pInfoPid);

				// make edge based on the current group
				// this node is group owner and connected to the second node via wifi p2p interface
				if(nodeInfo.getStatus()==CONNECTED && nodeInfo.isGroupOwner() && 
						nodeInfo.currentGroup.getNodeList().contains(idlelink.getNeighbor(j)) && neighborInfo.getGroupOwner()==node){
					gephiNodeGroup.get(i).connectTo(gephiMap.get(idlelink.getNeighbor(j).getID()));

					// this node is not group owner and the second node is group owner for this node
				}else if(!nodeInfo.isGroupOwner() && nodeInfo.getStatus()==CONNECTED && 
						nodeInfo.getGroupOwner()==idlelink.getNeighbor(j) && 
						neighborInfo.currentGroup.getNodeList().contains(node) && neighborInfo.isGroupOwner()){
					gephiNodeGroup.get(i).connectTo(gephiMap.get(idlelink.getNeighbor(j).getID()));
				}
			}

			// check again for wifi interface as well
			for(int k=0; k<idlelink.degree(); k++){	

				nodeP2pInfo neighborInfo = (nodeP2pInfo) idlelink.getNeighbor(k).getProtocol(p2pInfoPid);
				WifiManager neighborWifiManager = (WifiManager) idlelink.getNeighbor(k).getProtocol(wifimanagerPid);


				// this Node is group owner and the second node is connected to this node via WiFi Interface
				if(nodeInfo.isGroupOwner() && 
						nodeInfo.currentGroup.getNodeList().contains(idlelink.getNeighbor(k)) && 
						neighborWifiManager.getWifiStatus()==CONNECTED && 
						neighborWifiManager.apSSID.equals(nodeInfo.currentGroup.getSSID())){

					if(!gephiNodeGroup.get(i).hasEdgeTo(gephiMap.get(idlelink.getNeighbor(k).getID()).getId())){
						gephiNodeGroup.get(i).connectTo(gephiMap.get(idlelink.getNeighbor(k).getID()));
					}

					// this node (client or group owner) is connected via WiFi Interface to a Group Owner	
				}else if(wifiManager.getWifiStatus() == CONNECTED && wifiManager.apSSID.equals(neighborInfo.currentGroup.getSSID())
						&& neighborInfo.isGroupOwner() && neighborInfo.currentGroup.getNodeList().contains(node)){	

					if(!gephiNodeGroup.get(i).hasEdgeTo(gephiMap.get(idlelink.getNeighbor(k).getID()).getId())){
						gephiNodeGroup.get(i).connectTo(gephiMap.get(idlelink.getNeighbor(k).getID()));
					}
				}
			}
		}
//		for(int i=0; i<Network.size(); i++){
//			node = (Node) Network.get(i);
//			nodeP2pInfo nodeInfo = (nodeP2pInfo) node.getProtocol(p2pInfoPid);
//			if(nodeInfo.getStatus()==CONNECTED && !nodeInfo.isGroupOwner() && gephiNodeGroup.get(i).getEdges().isEmpty()){
//				print("Node: " + node.getID() + " GO: " + nodeInfo.getGroupOwner().getID());
//				nodeP2pInfo neighborInfo = (nodeP2pInfo) nodeInfo.getGroupOwner().getProtocol(p2pInfoPid);
//				print(neighborInfo.currentGroup.getNodeList().contains(node));
//				print("Nodes inside this Group: ");
//				for(Node tempNode: neighborInfo.currentGroup.getNodeList()){
//					print(tempNode.getID());
//
//				}
//			}
//		}

		// generate output gexf file from the above mentioned graph
		StaxGraphWriter graphWriter = new StaxGraphWriter();			
		Writer out;
		File tempFile = null;
		try {
			tempFile = File.createTempFile("tempStream", ".gexf");
			tempFile.deleteOnExit();
			out =  new FileWriter(tempFile, false);
			graphWriter.writeToStream(gexf, out, "UTF-8");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// Import the created graph.gexf file to the gephi library
		//Init a project - and therefore a workspace
		ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
		pc.newProject();
		Workspace workspace = pc.getCurrentWorkspace();

		// get import controller
		ImportController importController = Lookup.getDefault().lookup(ImportController.class);

		//Import file
		Container container = null;
		try{	
			container = importController.importFile(tempFile);
			container.getLoader().setEdgeDefault(EdgeDefault.UNDIRECTED); //Force DIRECTED
			container.setAllowAutoNode(false); //Don’t create missing nodes
			container.setAutoScale(false);
		}catch (Exception ex) {
			ex.printStackTrace();
			//return true;
		}

		//Append imported data to GraphAPI
		importController.process(container, new DefaultProcessor(), workspace);

		//Get graph model of current workspace
		GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getModel();

		// Layout based on GeoLayout Plugin for Gephi
		GeoLayout layout = new GeoLayout(null);
		layout.setGraphModel(graphModel);
		layout.resetPropertiesValues();
		layout.setCentered(true);
		layout.setScale(500.0);
		layout.setProjection("Equirectangular");			
		layout.initAlgo();
		layout.goAlgo();

		//Preview
		PreviewModel model = Lookup.getDefault().lookup(PreviewController.class).getModel();
		model.getProperties().putValue(PreviewProperty.SHOW_NODE_LABELS, Boolean.TRUE);
		model.getProperties().putValue(PreviewProperty.NODE_LABEL_PROPORTIONAL_SIZE, Boolean.TRUE);
		model.getProperties().putValue(PreviewProperty.EDGE_COLOR, new EdgeColor(Color.BLACK));
		model.getProperties().putValue(PreviewProperty.EDGE_THICKNESS, new Float(3.9f));
		model.getProperties().putValue(PreviewProperty.EDGE_CURVED, Boolean.FALSE);
		model.getProperties().putValue(PreviewProperty.EDGE_OPACITY, new Float(100));
		//model.getProperties().putValue(PreviewProperty.NODE_BORDER_WIDTH, new Float(18));
		model.getProperties().putValue(PreviewProperty.NODE_LABEL_OUTLINE_SIZE, new Float(18));
		model.getProperties().putValue(PreviewProperty.NODE_LABEL_OUTLINE_OPACITY, new Float(50));
		model.getProperties().putValue(PreviewProperty.NODE_LABEL_FONT, model.getProperties().getFontValue(PreviewProperty.NODE_LABEL_FONT).deriveFont(8));				  

		// Ranking color of nodes by Indegree (number of in Edges)
		//			RankingController rankingController = Lookup.getDefault().lookup(RankingController.class);
		//			Ranking degreeRanking = rankingController.getModel().getRanking(Ranking.NODE_ELEMENT, Ranking.DEGREE_RANKING);
		//			AbstractColorTransformer colorTransformer = (AbstractColorTransformer) rankingController.getModel().getTransformer(Ranking.NODE_ELEMENT, Transformer.RENDERABLE_COLOR);	         
		//			colorTransformer.setColors(new Color[]{new Color(0xFEF0D9), new Color(0xB30000)});
		//			rankingController.transform(degreeRanking,colorTransformer);

		// Ranking size of nodes by Indegree
		RankingController rankingController = Lookup.getDefault().lookup(RankingController.class);
		Ranking degreeRanking = rankingController.getModel().getRanking(Ranking.NODE_ELEMENT, Ranking.DEGREE_RANKING);
		AbstractSizeTransformer sizeTransformer = (AbstractSizeTransformer) rankingController.getModel().getTransformer(Ranking.NODE_ELEMENT, Transformer.RENDERABLE_SIZE);
		sizeTransformer.setMinSize((float) (GephiSize+(0.05*FieldLength)));
		sizeTransformer.setMaxSize((float) (GephiSize+(0.15*FieldLength)));
		rankingController.transform(degreeRanking,sizeTransformer);


		//Export and show on Jframe
		ExportController ec = Lookup.getDefault().lookup(ExportController.class);
		//ec.exportFile(new File("log/autolayout.png"));
		PNGExporter exporter = (PNGExporter) ec.getExporter("png");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ec.exportStream(baos, exporter);
		showImage(baos);
	}

	/**
	 * Prints the.
	 *
	 * @param ob the ob
	 */
	public static void print(Object ob, Color color) {
		if(output!=null){
			//output.setCaretColor(color);
			
			output.append("\n" + ob);
			output.setForeground(color);
			
			if (scrollOutput){
				output.setCaretPosition(output.getDocument().getLength());
			}
		}
	}
}
