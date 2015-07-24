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

/** modified by: Naser Derakhsha*/
package wifidirect.nodemovement;

import peersim.core.*;
import peersim.config.Configuration;
import peersim.dynamics.NodeInitializer;

// TODO: Auto-generated Javadoc
/**
 * The Class RandNImodified.
 */
public class RandNImodified implements NodeInitializer{


//--------------------------------------------------------------------------
//Parameters
//--------------------------------------------------------------------------

/**
 * The protocol to operate on.
 * @config
 */
private static final String PAR_PROT = "protocol";

/**
 * If this config property is defined, method {@link Linkable#pack()} is 
 * invoked on the specified protocol at the end of the wiring phase. 
 * Default to false.
 * @config
 */
private static final String PAR_PACK = "pack";

/**
 * The coordinate protocol to look at.
 * 
 * @config
 */
private static final String PAR_COORDINATES_PROT = "coord_protocol";

/** The Constant PAR_RANGE. */
private static final String PAR_RANGE = "radio_range";


//--------------------------------------------------------------------------
//Fields
//--------------------------------------------------------------------------

/** The protocol we want to wire. */
private final int pid;


/** If true, method pack() is invoked on the initialized protocol. */
private final boolean pack;

/** Coordinate protocol pid. */
private final int coordPid;

/**  wifi radio range. */
private final double radio_range;

//--------------------------------------------------------------------------
//Initialization
//--------------------------------------------------------------------------

/**
 * Standard constructor that reads the configuration parameters. Invoked by the
 * simulation engine.
 * @param prefix the configuration prefix for this class
 */
public RandNImodified(String prefix)
{
	pid = Configuration.getPid(prefix + "." + PAR_PROT);
	pack = Configuration.contains(prefix + "." + PAR_PACK);
	coordPid = Configuration.getPid(prefix + "." + PAR_COORDINATES_PROT);
	radio_range = Configuration.getDouble(prefix + "." + PAR_RANGE, 0.2);
}

//--------------------------------------------------------------------------
//Methods
//--------------------------------------------------------------------------

/**
 * Takes {@value #PAR_DEGREE} random samples with replacement from the nodes of
 * the overlay network. No loop edges are added.
 *
 * @param n the n
 */
public void initialize(Node n)
{
	if (Network.size() == 0) return;
	Linkable linkable = (Linkable) n.getProtocol(pid);	
	for(int i=0; i<Network.size(); i++){
		if (distance(n, Network.get(i), coordPid)<radio_range){
			Linkable neigborLinkable = (Linkable) Network.get(i).getProtocol(pid);
			linkable.addNeighbor(Network.get(i));
			neigborLinkable.addNeighbor(n);			
		}
	}
	if (pack) linkable.pack();
}

/**
 * Utility function: returns the Euclidean distance based on the x,y
 * coordinates of a node. A {@link RuntimeException} is raised if a not
 * initialized coordinate is found.
 * 
 * @param new_node
 *            the node to insert in the topology.
 * @param old_node
 *            a node already part of the topology.
 * @param coordPid
 *            identifier index.
 * @return the distance value.
 */
private static double distance(Node new_node, Node old_node, int coordPid) {
    double x1 = ((CoordinateKeeper) new_node.getProtocol(coordPid))
            .getX();
    double x2 = ((CoordinateKeeper) old_node.getProtocol(coordPid))
            .getX();
    double y1 = ((CoordinateKeeper) new_node.getProtocol(coordPid))
            .getY();
    double y2 = ((CoordinateKeeper) old_node.getProtocol(coordPid))
            .getY();
    if (x1 == -1 || x2 == -1 || y1 == -1 || y2 == -1)
        throw new RuntimeException(
                "Found un-initialized coordinate. Use e.g., InetInitializer class in the config file.");
    return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
}


}

