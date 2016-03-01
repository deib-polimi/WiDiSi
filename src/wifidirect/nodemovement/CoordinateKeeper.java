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

import peersim.core.Protocol;

// TODO: Auto-generated Javadoc
/**
 * <p>
 * This class does nothing. It is simply a container inside each node to collect
 * peer coordinates.
 * </p>
 * <p>
 * The actual "who knows whom" relation (the topology) container is decoupled
 * from other packages. It is maintained by any {@link peersim.core.Linkable} 
 * implementing
 * protocol declared in the config file.
 * </p>
 */
public class CoordinateKeeper implements Protocol {

    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------

    /** 2d coordinates components. */
    private double x, y;
    
    /** The mobile. */
    // If the node is mobile set this to true
    private boolean mobile;

    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------
    /**
     * Standard constructor that reads the configuration parameters. Invoked by
     * the simulation engine. By default, all the coordinates components are set
     * to -1 value. The {@link InetInitializer} class provides a coordinates
     * initialization.
     * 
     * @param prefix
     *            the configuration prefix for this class.
     */
    public CoordinateKeeper(String prefix) {
        /* Un-initialized coordinates defaults to -1. */
        x = -1;
        y = -1;
        mobile = false;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    public Object clone() {
    	CoordinateKeeper inp = null;
        try {
            inp = (CoordinateKeeper) super.clone();
        } catch (CloneNotSupportedException e) {} // never happens
        inp.x = -1;
        inp.y = -1;
        inp.mobile = false;
        return inp;
    }

    /**
     * Gets the x.
     *
     * @return the x
     */
    public double getX() {
        return x;
    }

    /**
     * Sets the x.
     *
     * @param x the new x
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Gets the y.
     *
     * @return the y
     */
    public double getY() {
        return y;
    }

    /**
     * Sets the y.
     *
     * @param y the new y
     */
    public void setY(double y) {
        this.y = y;
    }

	/**
	 * Checks if is mobile.
	 *
	 * @return true, if is mobile
	 */
	public boolean isMobile() {
		return mobile;
	}

	/**
	 * Sets the mobile.
	 *
	 * @param Mobile the new mobile
	 */
	public void setMobile(boolean Mobile) {
		this.mobile = Mobile;
	}

}
