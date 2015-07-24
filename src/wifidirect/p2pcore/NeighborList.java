/*
 * Copyright (c) 2003-2005 The BISON Project
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
 *	Source: PeerSim Project
 *	Modified By: Naser Derakhshan
 *	Politecnico di Milano
 */

package wifidirect.p2pcore;

import peersim.config.Configuration;
import peersim.core.Linkable;
import peersim.core.Node;
import peersim.core.Protocol;

// TODO: Auto-generated Javadoc
/**
 * A protocol that stores links. It does nothing apart from that.
 * It is useful to model a static link-structure
 * (topology). The only function of this protocol is to serve as a source of
 * neighborhood information for other protocols.
 */
public class NeighborList implements Protocol, Linkable
{

// --------------------------------------------------------------------------
// Parameters
// --------------------------------------------------------------------------

/** Default init capacity. */
private static final int DEFAULT_INITIAL_CAPACITY = 10;

/**
 * Initial capacity. Defaults to {@value #DEFAULT_INITIAL_CAPACITY}.
 * @config
 */
private static final String PAR_INITCAP = "capacity";

// --------------------------------------------------------------------------
// Fields
// --------------------------------------------------------------------------

/**  Neighbors. */
protected Node[] neighbors;

/**  Actual number of neighbors in the array. */
protected int len;

// --------------------------------------------------------------------------
// Initialization
// --------------------------------------------------------------------------

/**
 * Instantiates a new neighbor list.
 *
 * @param s the s
 */
public NeighborList(String s)
{
	neighbors = new Node[Configuration.getInt(s + "." + PAR_INITCAP,
			DEFAULT_INITIAL_CAPACITY)];
	len = 0;
}

//--------------------------------------------------------------------------

/* (non-Javadoc)
 * @see java.lang.Object#clone()
 */
public Object clone()
{
	NeighborList ip = null;
	try { ip = (NeighborList) super.clone(); }
	catch( CloneNotSupportedException e ) {} // never happens
	ip.neighbors = new Node[neighbors.length];
	System.arraycopy(neighbors, 0, ip.neighbors, 0, len);
	ip.len = len;
	return ip;
}

// --------------------------------------------------------------------------
// Methods
// --------------------------------------------------------------------------

/* (non-Javadoc)
 * @see peersim.core.Linkable#contains(peersim.core.Node)
 */
public boolean contains(Node n)
{
	for (int i = 0; i < len; i++) {
		if (neighbors[i] == n)
			return true;
	}
	return false;
}

// --------------------------------------------------------------------------

/**
 *  Adds given node if it is not already in the network. There is no limit
 * to the number of nodes that can be added.
 *
 * @param n the n
 * @return true, if successful
 */
public boolean addNeighbor(Node n)
{
	for (int i = 0; i < len; i++) {
		if (neighbors[i] == n)
			return false;
	}
	if (len == neighbors.length) {
		Node[] temp = new Node[3 * neighbors.length / 2];
		System.arraycopy(neighbors, 0, temp, 0, neighbors.length);
		neighbors = temp;
	}
	neighbors[len] = n;
	len++;
	return true;
}

// --------------------------------------------------------------------------

/* (non-Javadoc)
 * @see peersim.core.Linkable#getNeighbor(int)
 */
public Node getNeighbor(int i)
{
	return neighbors[i];
}

// --------------------------------------------------------------------------

/* (non-Javadoc)
 * @see peersim.core.Linkable#degree()
 */
public int degree()
{
	return len;
}

// --------------------------------------------------------------------------

/* (non-Javadoc)
 * @see peersim.core.Linkable#pack()
 */
public void pack()
{
	if (len == neighbors.length)
		return;
	Node[] temp = new Node[len];
	System.arraycopy(neighbors, 0, temp, 0, len);
	neighbors = temp;
}

// --------------------------------------------------------------------------

/* (non-Javadoc)
 * @see java.lang.Object#toString()
 */
public String toString()
{
	if( neighbors == null ) return "DEAD!";
	StringBuffer buffer = new StringBuffer();
	buffer.append("len=" + len + " maxlen=" + neighbors.length + " [");
	for (int i = 0; i < len; ++i) {
		buffer.append(neighbors[i].getIndex() + " ");
	}
	return buffer.append("]").toString();
}

// --------------------------------------------------------------------------

/* (non-Javadoc)
 * @see peersim.core.Cleanable#onKill()
 */
public void onKill()
{
	neighbors = new Node[DEFAULT_INITIAL_CAPACITY];
	len = 0;
}

}
