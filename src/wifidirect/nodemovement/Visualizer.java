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
 *
 */
package wifidirect.nodemovement;

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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
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
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

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
import wifidirect.p2pcore.nodeP2pInfo;

// TODO: Auto-generated Javadoc
/**
 * The Class Visualizer.
 */
public class Visualizer implements Control{
	
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

	/** The Constant PAR_COORD. */
	private static final String PAR_COORD = "coord";
	
	/** The Constant PAR_PROTOCOL. */
	private static final String PAR_PROTOCOL = "newapp";
	
	/** The Constant PAR_MANAGE. */
	private static final String PAR_MANAGE = "p2pmanager";
	
	/** The Constant PAR_LINKABLE. */
	private static final String PAR_LINKABLE = "linkable";
	
	/** The Constant PAR_APPLICATION. */
	private static final String PAR_APPLICATION = "application";
	
	/** The Constant PAR_P2PINFO. */
	private static final String PAR_P2PINFO = "p2pinfo";
	
	/** The Constant PAR_TRASP. */
	private static final String PAR_TRASP = "transport";
	
	/** The Constant PAR_CYCLE. */
	private static final String PAR_CYCLE = "cyclelen";
	
	/** The Constant PAR_FIELD. */
	private static final String PAR_FIELD = "fieldlen";
	
	/** The Constant PAR_GEPHI. */
	private static final String PAR_GEPHI = "gephisize";
	
	/** The Constant PAR_MAXSPEED. */
	private static final String PAR_MAXSPEED = "maxspeed";
	
	/** The Constant PAR_MINSPEED. */
	private static final String PAR_MINSPEED = "minspeed";
	
	/** The Constant PAR_SHOW_GROUPS. */
	private static final String PAR_SHOW_GROUPS = "showgroups";
	
	/** The coordinates pid. */
	private int coordinatesPid;
	
	/** The newapp id. */
	private int newappId;
	
	/** The transport id. */
	private int transportId;
	
	/** The linkable id. */
	private int linkableId;
	
	/** The p2pmanager id. */
	private int p2pmanagerId;
	
	/** The application id. */
	private int applicationId;
	
	/** The p2p info pid. */
	private int p2pInfoPid;
	
	/** The Cycle length. */
	private int CycleLength;
	
	/** The Field length. */
	private int FieldLength;
	
	/** The Gephi size. */
	private int GephiSize;
	
	/** The maxspeed. */
	private double maxspeed;
	
	/** The minspeed. */
	private double minspeed;
	
	/** The showgroups. */
	private boolean showgroups;

	/** The show netwok image. */
	private 			boolean 				showNetwokImage	= false;
	
	/** The start time real. */
	private 			long 					startTimeReal 	= 0;
	
	/** The cycle. */
	private 			long 					cycle 			= 0;

	
	/** The frame. */
	private 			JFrame 					frame 			= null;
	
	/** The frame2. */
	private 			JFrame 					frame2 			= null;
	
	/** The node. */
	private 			Node 					node 			= null;
	
	/** The image. */
	private				Image 					image			= null;
	
	/** The image2. */
	private				Image 					image2			= null;
	
	/** The label. */
	private				JLabel 					label			= null;
	
	/** The label2. */
	private				JLabel 					label2			= null;
	
	/** The output. */
	private static		JTextArea 				output			= null;
	
	/** The content pane. */
	private 			JPanel 					contentPane		= null;
	
	/** The text field13. */
	public static JTextField textField1, textField2, textField3, textField4, textField5, textField6, textField7, textField8, textField9, textField10, textField11, textField12, textField13;;
	
	/** The label field13. */
	private static JLabel labelField1, labelField2, labelField3, labelField4, labelField5, labelField6, labelField7, labelField8, labelField9, labelField10, labelField11, labelField12, labelField13;
	
	/** The rules text15. */
	public static JTextField rulesText1, rulesText2, rulesText3, rulesText4, rulesText5, rulesText6, rulesText7, rulesText8,
	rulesText9, rulesText10, rulesText11, rulesText12, rulesText13, rulesText14, rulesText15;
	
	/**
	 * Instantiates a new visualizer.
	 *
	 * @param prefix the prefix
	 */
	public Visualizer(String prefix){
		newappId 		= Configuration.getPid(prefix + "." + PAR_PROTOCOL);
		transportId 	= Configuration.getPid(prefix + "." + PAR_TRASP);
		p2pInfoPid 		= Configuration.getPid(prefix + "." + PAR_P2PINFO);
		linkableId 		= Configuration.getPid(prefix + "." + PAR_LINKABLE);
		p2pmanagerId 	= Configuration.getPid(prefix + "." + PAR_MANAGE);
		applicationId 	= Configuration.getPid(prefix + "." + PAR_APPLICATION);
		CycleLength		= Configuration.getInt(prefix + "." + PAR_CYCLE);
		FieldLength		= Configuration.getInt(prefix + "." + PAR_FIELD);
		GephiSize		= Configuration.getInt(prefix + "." + PAR_GEPHI);
		maxspeed		= Configuration.getDouble(prefix + "." + PAR_MAXSPEED);
		minspeed		= Configuration.getDouble(prefix + "." + PAR_MINSPEED);
		showgroups		= (Configuration.getInt(prefix + "." + PAR_SHOW_GROUPS)>0)? true:false;
	}

	/* (non-Javadoc)
	 * @see peersim.core.Control#execute()
	 */
	@Override
	public boolean execute() {
		//System.out.println("Cycle: " + cycle + " time: " + CommonState.getTime() + " phase: " + CommonState.getPhase() + " int time: " + CommonState.getIntTime());
		
		if(cycle%5==0 && showgroups){
			showGroups();
		}
		if(showNetwokImage){
			showNetwork();
		}
		
		// add cyclye in each round
		if(cycle%10==0)	print(cycle);
		
		if(cycle==1){
			showFields();
			rulesViolationCheck();
			
			labelField1.setText("Real Time(S): ");
			labelField2.setText("Simulator Time(S): ");			
			labelField3.setText("Max Speed(m/s): ");
			textField3.setText(String.valueOf(maxspeed));
			labelField4.setText("Min Speed(m/s): ");
			textField4.setText(String.valueOf(minspeed));
			labelField5.setText("Network Size: ");
			labelField6.setText("No. Groups: ");
			labelField7.setText("No. Connected Peers: ");
			labelField8.setText("Max No. Clients: ");
			labelField9.setText("Max No. Neighbors: ");
			labelField10.setText("No. invitation timeout: ");
			textField10.setText(String.valueOf(0));
			labelField11.setText("Cycle: ");
			labelField12.setText("Standard Deviation: ");
			labelField13.setText("Mean Value: ");
			startTimeReal = System.currentTimeMillis();	
		}
		
		if(cycle>1){		
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
			textField1.setText(String.valueOf(hour + ":" + min + ":" + sec));
			
			//calculate the simulator time
			elapsedTime = (CycleLength * CommonState.getTime());
			elapsedSeconds = elapsedTime / 1000;
			hour = (int) (elapsedSeconds/3600);
			remain = elapsedSeconds%3600;
			min = (int) remain/60;
			sec = (int) remain%60;
			textField2.setText(String.valueOf(hour + ":" + min + ":" + sec));
			
			textField5.setText(String.valueOf(Network.size()));
			textField6.setText(String.valueOf(noGroups));
			textField7.setText(String.valueOf(connectedDevices));
			textField8.setText(String.valueOf(maxClient));
			textField9.setText(String.valueOf(maxNeighbor));
			textField11.setText(String.valueOf(CommonState.getTime()));		
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

				frame.setJMenuBar(mb);

				showNet.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent actionEvent) {
						showNetwokImage = true;
						frame2.setVisible(true);
					}
				});

				hideNet.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent actionEvent) {
						showNetwokImage = false;
						frame2.setVisible(false);
					}
				});

				exit.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent actionEvent) {
						frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
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

//	public Visualizer clone(){
//		Visualizer tf = null;
//		try { tf = (Visualizer) super.clone(); }
//		catch( CloneNotSupportedException e ) {} // never happen
//		tf.newappId = newappId;
//		tf.transportId = transportId;
//		tf.p2pInfoPid = p2pInfoPid;
//		tf.linkableId = linkableId;
//		tf.p2pmanagerId = p2pmanagerId;
//		tf.applicationId = applicationId;
//		tf.CycleLength = CycleLength;
//		tf.FieldLength = FieldLength;
//		tf.GephiSize = GephiSize;
//		tf.maxspeed = maxspeed;
//		tf.minspeed = minspeed;
//		return tf;	
//	}

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
			}else if(nodeInfo.getStatus()==AVAILABLE && nodeInfo.isPeerDiscoveryStarted()){
				nodeColor.setB(150);
				nodeColor.setG(150);
				nodeColor.setR(150);
			}else if(nodeInfo.getStatus()==INVITED && nodeInfo.isPeerDiscoveryStarted()){
				nodeColor.setB(255);
				nodeColor.setG(255);
				nodeColor.setR(0);
			}else if(!nodeInfo.isPeerDiscoveryStarted()){
				nodeColor.setB(0);
				nodeColor.setG(0);
				nodeColor.setR(255);	
			}else if((nodeInfo.getStatus()==UNAVAILABLE)){
				nodeColor.setB(255);
				nodeColor.setG(255);
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

//		try{
//			writer = new PrintWriter(new BufferedWriter(new FileWriter("log/nodeInfoObs.txt", true)));
//		} catch (IOException e) {
//		    System.out.println("File nodeInfoObs.txt not found");
//		}
		
		
		// create the gephi layer by setting the neighbors
		for(int i=0; i<Network.size(); i++){
			node = (Node) Network.get(i);
			Linkable idlelink = (Linkable) node.getProtocol(linkableId);
			nodeP2pInfo nodeInfo = (nodeP2pInfo) node.getProtocol(p2pInfoPid);
			for(int j=0; j<idlelink.degree(); j++){	
				
				//gephiNode.get(i).connectTo(gephiMap.get(idlelink.getNeighbor(j).getID()));
				nodeP2pInfo neighborInfo = (nodeP2pInfo) idlelink.getNeighbor(j).getProtocol(p2pInfoPid);
				
				
				
				// make edge based on the current group
				if(nodeInfo.getStatus()==CONNECTED && nodeInfo.isGroupOwner() && 
						nodeInfo.currentGroup.getNodeList().contains(idlelink.getNeighbor(j)) && neighborInfo.getGroupOwner()==node){
					gephiNode.get(i).connectTo(gephiMap.get(idlelink.getNeighbor(j).getID()));
				}else if(!nodeInfo.isGroupOwner() && nodeInfo.getStatus()==CONNECTED && 
						nodeInfo.getGroupOwner()==idlelink.getNeighbor(j) && 
						neighborInfo.currentGroup.getNodeList().contains(node) && neighborInfo.isGroupOwner()){
					gephiNode.get(i).connectTo(gephiMap.get(idlelink.getNeighbor(j).getID()));
				}
			}
		}
		for(int i=0; i<Network.size(); i++){
			node = (Node) Network.get(i);
			nodeP2pInfo nodeInfo = (nodeP2pInfo) node.getProtocol(p2pInfoPid);
			if(nodeInfo.getStatus()==CONNECTED && !nodeInfo.isGroupOwner() && gephiNode.get(i).getEdges().isEmpty()){
				print("Node: " + node.getID() + " GO: " + nodeInfo.getGroupOwner().getID());
				nodeP2pInfo neighborInfo = (nodeP2pInfo) nodeInfo.getGroupOwner().getProtocol(p2pInfoPid);
				print(neighborInfo.currentGroup.getNodeList().contains(node));
				print("Nodes inside this Group: ");
				for(Node tempNode: neighborInfo.currentGroup.getNodeList()){
					print(tempNode.getID());
					
				}
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

		//							File gexfFile = new File("log/gexfNetwork.gexf");
		//							
		//							try {
		//								Writer out1 =  new FileWriter(gexfFile, false);
		//								graphWriter.writeToStream(gexf, out1, "UTF-8");
		//							} catch (IOException e) {
		//								// TODO Auto-generated catch block
		//								e.printStackTrace();
		//							}
		//				System.out.println(outputStream.size());
		//				buffer = outputStream.toByteArray();
		//				System.out.println(buffer.length);
		//				inStream = new ByteArrayInputStream(buffer);

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

			//				File myGraph = new File("E:\\Polimi\\adt-bundle-windows-x86_64-20140702\\PeerSimSource\\log\\gexfNetwork.gexf");
			//				container = importController.importFile(myGraph);

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
		model.getProperties().putValue(PreviewProperty.EDGE_THICKNESS, new Float(0.1f));
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
	public static void print(Object ob) {
		if(output!=null){
			output.append("\n" + ob);
			output.setCaretPosition(output.getDocument().getLength());
		}
	}

	/**
	 * Show fields.
	 */
	public void showFields(){

		JFrame frameField = new JFrame("Fileds");
		java.awt.Container content = frameField.getContentPane();
		content.setLayout(new GridBagLayout());
		content.setBackground(UIManager.getColor("control"));
	    GridBagConstraints c = new GridBagConstraints();
	    
	    c.gridx = 0;
	    c.gridy = GridBagConstraints.RELATIVE;
	    c.gridwidth = 1;
	    c.gridheight = 1;
	    c.insets = new Insets(2, 2, 2, 2);
	    c.anchor = GridBagConstraints.EAST;

		//JPanel Field1 = new JPanel(new BorderLayout());
		labelField1 = new JLabel("Label 1", SwingConstants.RIGHT);
		labelField2 = new JLabel("Label 2", SwingConstants.RIGHT);
		labelField3 = new JLabel("Label 3", SwingConstants.RIGHT);
		labelField4 = new JLabel("Label 4", SwingConstants.RIGHT);
		labelField5 = new JLabel("Label 5", SwingConstants.RIGHT);
		labelField6 = new JLabel("Label 6", SwingConstants.RIGHT);
		labelField7 = new JLabel("Label 7", SwingConstants.RIGHT);
		labelField8 = new JLabel("Label 8", SwingConstants.RIGHT);
		labelField9 = new JLabel("Label 9", SwingConstants.RIGHT);
		labelField10 = new JLabel("Label 10", SwingConstants.RIGHT);
		labelField11 = new JLabel("Label 11", SwingConstants.RIGHT);
		labelField12 = new JLabel("Label 12", SwingConstants.RIGHT);
		labelField13 = new JLabel("Label 13", SwingConstants.RIGHT);
		
		content.add(labelField1, c);
		labelField1.setDisplayedMnemonic('1');
		content.add(labelField2, c);
		labelField2.setDisplayedMnemonic('2');
		content.add(labelField3, c);
		labelField3.setDisplayedMnemonic('3');
		content.add(labelField4, c);
		labelField4.setDisplayedMnemonic('4');
		content.add(labelField5, c);
		labelField5.setDisplayedMnemonic('5');
		content.add(labelField6, c);
		labelField6.setDisplayedMnemonic('6');
		content.add(labelField7, c);
		labelField7.setDisplayedMnemonic('7');
		content.add(labelField8, c);
		labelField8.setDisplayedMnemonic('8');
		content.add(labelField9, c);
		labelField9.setDisplayedMnemonic('9');
		content.add(labelField10, c);
		labelField10.setDisplayedMnemonic('A');
		content.add(labelField11, c);
		labelField11.setDisplayedMnemonic('B');
		content.add(labelField12, c);
		labelField12.setDisplayedMnemonic('C');
		content.add(labelField13, c);
		labelField13.setDisplayedMnemonic('D');
		
		 c.gridx = 1;
		 c.gridy = 0;
		 c.weightx = 1.0;
		 c.fill = GridBagConstraints.HORIZONTAL;
		 c.anchor = GridBagConstraints.CENTER;
		 
		 textField1 = new JTextField(15);
		 textField2 = new JTextField(15);
		 textField3 = new JTextField(15);
		 textField4 = new JTextField(15);
		 textField5 = new JTextField(15);
		 textField6 = new JTextField(15);
		 textField7 = new JTextField(15);
		 textField8 = new JTextField(15);
		 textField9 = new JTextField(15);
		 textField10 = new JTextField(15);
		 textField11 = new JTextField(15);
		 textField12 = new JTextField(15);
		 textField13 = new JTextField(15);
		 
		 content.add(textField1, c);
		 textField1.setFocusAccelerator('1');
		 c.gridx = 1;
		 c.gridy = GridBagConstraints.RELATIVE;
		 content.add(textField2, c);
		 textField2.setFocusAccelerator('2');
		 content.add(textField3, c);
		 textField3.setFocusAccelerator('3');
		 content.add(textField4, c);
		 textField4.setFocusAccelerator('4');
		 content.add(textField5, c);
		 textField5.setFocusAccelerator('5');
		 content.add(textField6, c);
		 textField6.setFocusAccelerator('6');
		 content.add(textField7, c);
		 textField7.setFocusAccelerator('7');
		 content.add(textField8, c);
		 textField8.setFocusAccelerator('8');
		 content.add(textField9, c);
		 textField9.setFocusAccelerator('9');
		 content.add(textField10, c);
		 textField10.setFocusAccelerator('A');
		 content.add(textField11, c);
		 textField11.setFocusAccelerator('B');
		 content.add(textField12, c);
		 textField12.setFocusAccelerator('C');
		 content.add(textField13, c);
		 textField13.setFocusAccelerator('D');
		 c.weightx = 0.0;
		 c.fill = GridBagConstraints.NONE;
		 

		frameField.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frameField.pack();
		frameField.setLocationRelativeTo(null);
		frameField.setAlwaysOnTop(true);
		frameField.setName("Fields");
		frameField.setResizable(true);
		frameField.setTitle("Fields");
		frameField.setVisible(true);	
	}
	
	/**
	 * Rules violation check.
	 */
	public void rulesViolationCheck(){
		JLabel rulesLabel1, rulesLabel2, rulesLabel3, rulesLabel4, rulesLabel5, rulesLabel6, rulesLabel7, rulesLabel8, 
		rulesLabel9, rulesLabel10, rulesLabel11, rulesLabel12, rulesLabel13, rulesLabel14, rulesLabel15;

		JFrame frameField = new JFrame("Fileds");
		java.awt.Container content = frameField.getContentPane();
		content.setLayout(new GridBagLayout());
		content.setBackground(UIManager.getColor("control"));
	    GridBagConstraints c = new GridBagConstraints();
	    
	    c.gridx = 0;
	    c.gridy = GridBagConstraints.RELATIVE;
	    c.gridwidth = 1;
	    c.gridheight = 1;
	    c.insets = new Insets(2, 2, 2, 2);
	    c.anchor = GridBagConstraints.EAST;

		//JPanel Field1 = new JPanel(new BorderLayout());
		rulesLabel1 = new JLabel("Rule 1", SwingConstants.RIGHT);
		rulesLabel2 = new JLabel("Rule 2", SwingConstants.RIGHT);
		rulesLabel3 = new JLabel("Rule 3", SwingConstants.RIGHT);
		rulesLabel4 = new JLabel("Rule 4", SwingConstants.RIGHT);
		rulesLabel5 = new JLabel("Rule 5", SwingConstants.RIGHT);
		rulesLabel6 = new JLabel("Rule 6", SwingConstants.RIGHT);
		rulesLabel7 = new JLabel("Rule 7", SwingConstants.RIGHT);
		rulesLabel8 = new JLabel("Rule 8", SwingConstants.RIGHT);
		rulesLabel9 = new JLabel("Rule 9", SwingConstants.RIGHT);
		rulesLabel10 = new JLabel("Rule 10", SwingConstants.RIGHT);
		rulesLabel11 = new JLabel("Rule 11", SwingConstants.RIGHT);
		rulesLabel12 = new JLabel("Rule 12", SwingConstants.RIGHT);
		rulesLabel13 = new JLabel("Rule 13", SwingConstants.RIGHT);
		rulesLabel14 = new JLabel("Rule 14", SwingConstants.RIGHT);
		rulesLabel15 = new JLabel("Rule 15", SwingConstants.RIGHT);
		
		content.add(rulesLabel1, c);
		rulesLabel1.setDisplayedMnemonic('1');
		content.add(rulesLabel2, c);
		rulesLabel2.setDisplayedMnemonic('2');
		content.add(rulesLabel3, c);
		rulesLabel3.setDisplayedMnemonic('3');
		content.add(rulesLabel4, c);
		rulesLabel4.setDisplayedMnemonic('4');
		content.add(rulesLabel5, c);
		rulesLabel5.setDisplayedMnemonic('5');
		content.add(rulesLabel6, c);
		rulesLabel6.setDisplayedMnemonic('6');
		content.add(rulesLabel7, c);
		rulesLabel7.setDisplayedMnemonic('7');
		content.add(rulesLabel8, c);
		rulesLabel8.setDisplayedMnemonic('8');
		content.add(rulesLabel9, c);
		rulesLabel9.setDisplayedMnemonic('9');
		content.add(rulesLabel10, c);
		rulesLabel10.setDisplayedMnemonic('A');
		content.add(rulesLabel11, c);
		rulesLabel11.setDisplayedMnemonic('B');
		content.add(rulesLabel12, c);
		rulesLabel12.setDisplayedMnemonic('C');
		content.add(rulesLabel13, c);
		rulesLabel13.setDisplayedMnemonic('D');
		content.add(rulesLabel14, c);
		rulesLabel14.setDisplayedMnemonic('E');
		content.add(rulesLabel15, c);
		rulesLabel15.setDisplayedMnemonic('F');
		
		 c.gridx = 1;
		 c.gridy = 0;
		 c.weightx = 1.0;
		 c.fill = GridBagConstraints.HORIZONTAL;
		 c.anchor = GridBagConstraints.CENTER;
		 
		 rulesText1 = new JTextField(15);
		 rulesText2 = new JTextField(15);
		 rulesText3 = new JTextField(15);
		 rulesText4 = new JTextField(15);
		 rulesText5 = new JTextField(15);
		 rulesText6 = new JTextField(15);
		 rulesText7 = new JTextField(15);
		 rulesText8 = new JTextField(15);
		 rulesText9 = new JTextField(15);
		 rulesText10 = new JTextField(15);
		 rulesText11 = new JTextField(15);
		 rulesText12 = new JTextField(15);
		 rulesText13 = new JTextField(15);
		 rulesText14 = new JTextField(15);
		 rulesText15 = new JTextField(15);
		 
		 content.add(rulesText1, c);
		 rulesText1.setFocusAccelerator('1');
		 c.gridx = 1;
		 c.gridy = GridBagConstraints.RELATIVE;
		 content.add(rulesText2, c);
		 rulesText2.setFocusAccelerator('2');
		 content.add(rulesText3, c);
		 rulesText3.setFocusAccelerator('3');
		 content.add(rulesText4, c);
		 rulesText4.setFocusAccelerator('4');
		 content.add(rulesText5, c);
		 rulesText5.setFocusAccelerator('5');
		 content.add(rulesText6, c);
		 rulesText6.setFocusAccelerator('6');
		 content.add(rulesText7, c);
		 rulesText7.setFocusAccelerator('7');
		 content.add(rulesText8, c);
		 rulesText8.setFocusAccelerator('8');
		 content.add(rulesText9, c);
		 rulesText9.setFocusAccelerator('9');
		 content.add(rulesText10, c);
		 rulesText10.setFocusAccelerator('A');
		 content.add(rulesText11, c);
		 rulesText11.setFocusAccelerator('B');
		 content.add(rulesText12, c);
		 rulesText12.setFocusAccelerator('C');
		 content.add(rulesText13, c);
		 rulesText13.setFocusAccelerator('D');
		 content.add(rulesText14, c);
		 rulesText14.setFocusAccelerator('E');
		 content.add(rulesText15, c);
		 rulesText15.setFocusAccelerator('F');
		 c.weightx = 0.0;
		 c.fill = GridBagConstraints.NONE;
		 
		 rulesText1.setText(String.valueOf(0));
		 rulesText2.setText(String.valueOf(0));
		 rulesText3.setText(String.valueOf(0));
		 rulesText4.setText(String.valueOf(0));
		 rulesText5.setText(String.valueOf(0));
		 rulesText6.setText(String.valueOf(0));
		 rulesText7.setText(String.valueOf(0));
		 rulesText8.setText(String.valueOf(0));
		 rulesText9.setText(String.valueOf(0));
		 rulesText10.setText(String.valueOf(0));
		 //rulesText11.setText(String.valueOf(0));
		// rulesText12.setText(String.valueOf(0));
		 //rulesText13.setText(String.valueOf(0));
		 //rulesText14.setText(String.valueOf(0));
		// rulesText15.setText(String.valueOf(0));
	
		 

		frameField.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frameField.pack();
		frameField.setLocationRelativeTo(null);
		frameField.setAlwaysOnTop(true);
		frameField.setName("Violation Check");
		frameField.setResizable(true);
		frameField.setTitle("Violation Check");
		frameField.setVisible(true);	
	}
	
}
