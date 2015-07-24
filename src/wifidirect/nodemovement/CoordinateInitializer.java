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
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;
import peersim.dynamics.NodeInitializer;

// TODO: Auto-generated Javadoc
/**
 * The Class CoordinateInitializer.
 */
public class CoordinateInitializer implements Control, NodeInitializer {
    // ------------------------------------------------------------------------
    // Parameters
    // ------------------------------------------------------------------------
    /**
     * The protocol to operate on.
     * 
     * @config
     */
    private static final String PAR_PROT = "protocol";

    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------

    /** Protocol identifier, obtained from config property {@link #PAR_PROT}. */
    private final int pid;

    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------

    /**
     * Standard constructor that reads the configuration parameters. Invoked by
     * the simulation engine.
     * 
     * @param prefix
     *            the configuration prefix for this class.
     */
    public CoordinateInitializer(String prefix) {
        pid = Configuration.getPid(prefix + "." + PAR_PROT);
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------
    /**
     * Initialize the node coordinates. The first node in the {@link Network} is
     * the root node by default and it is located in the middle (the center of
     * the square) of the surface area.
     *
     * @return true, if successful
     */
    public boolean execute() {
        // Set the root: the index 0 node by default.
        Node n = Network.get(0);
        CoordinateKeeper prot = (CoordinateKeeper) n
                .getProtocol(pid);
        prot.setX(0.0);
        prot.setY(0.0);

        // Set coordinates x,y
        for (int i = 1; i < Network.size(); i++) {
            n = Network.get(i);
            prot = (CoordinateKeeper) n.getProtocol(pid);
            prot.setX(CommonState.r.nextDouble()*2 - 1);
            prot.setY(CommonState.r.nextDouble()*2 - 1);
            prot.setMobile(CommonState.r.nextBoolean());
        }
        return false;
    }

	/* (non-Javadoc)
	 * @see peersim.dynamics.NodeInitializer#initialize(peersim.core.Node)
	 */
	@Override
	public void initialize(Node n) {
		// TODO Auto-generated method stub
	       // Set the root: the index 0 node by default.
		CoordinateKeeper prot = (CoordinateKeeper) n
                .getProtocol(pid);
        prot.setX(CommonState.r.nextDouble()*2 - 1);
        prot.setY(CommonState.r.nextDouble()*2 - 1);
        prot.setMobile(CommonState.r.nextBoolean());
       
	}

}
