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

import peersim.config.Configuration;
import peersim.core.Linkable;
import peersim.dynamics.WireGraph;
import peersim.graph.Graph;

// TODO: Auto-generated Javadoc
/**
 * This class applies a HOT topology on a any {@link Linkable} implementing
 * protocol.
 * 
 * @author Naser Derakhshan
 */
public class WireInetTopology extends WireGraph {

	// --------------------------------------------------------------------------
	// Fields
	// --------------------------------------------------------------------------

	/** Coordinate protocol pid. */
	private final int coordPid;

	// --------------------------------------------------------------------------
	// Initialization
	// --------------------------------------------------------------------------

	/**
	 * Standard constructor that reads the configuration parameters. Normally
	 * invoked by the simulation engine.
	 * 
	 * @param prefix
	 *            the configuration prefix for this class
	 */
	public WireInetTopology(String prefix) {
		super(prefix);
		coordPid = Configuration.getPid(prefix + "." + "coord_protocol");
	}

	/**
	 * Performs the actual wiring.
	 * @param g a {@link peersim.graph.Graph} interface object to work on.
	 */
	public void wire(Graph g) {

		GraphFactoryM.wireCordXY(g, coordPid, (NodeMovement.radio/NodeMovement.FieldLength));

	}
}
