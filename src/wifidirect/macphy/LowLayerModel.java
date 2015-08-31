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
package wifidirect.macphy;

import peersim.core.Node;

// TODO: Auto-generated Javadoc
/**
 * The Interface LowLayerModel.
 */
public interface LowLayerModel {

	/*
	 * Delay needed for given src node to discover one device around (could vary in different scenarios)
	 * the object could be abything needed for calculating the delay betwen source and destination at a given time
	 * for instance the object could be the size of the package if it matters
	 */
	
	/**
	 * Channel delay.
	 *
	 * @param src the src
	 * @param dest the dest
	 * @param object the object
	 * @return the long
	 */
	// channel delay is the time required that the object traverse from src to dest at MAC layer
	public long channelDelay (Node src, Node dest, Object object);
	
	/**
	 * Authentication delay.
	 *
	 * @param src the src
	 * @param dest the dest
	 * @param object the object
	 * @return the long
	 */
	// User dependant delay. The time needed for accepting/rejecting an invitation
	public long authenticationDelay (Node src, Node dest, Object object);
	
	/**
	 * Encryptiondelay.
	 *
	 * @param src the src
	 * @param dest the dest
	 * @param object the object
	 * @return the long
	 */
	// Encryption delay is the time required for object message to get encrypted at source and decrypted at dest
	public long encryptiondelay(Node src, Node dest, Object object);
	
	/**
	 * Power management delay.
	 *
	 * @param src the src
	 * @param dest the dest
	 * @param object the object
	 * @return the long
	 */
	// Power management delay is the effect of going to sleep in all processes in wifi direct
	public long powerManagementDelay(Node src, Node dest, Object object);
	
	/**
	 * Internal delay.
	 *
	 * @param src the src
	 * @param dest the dest
	 * @param object the object
	 * @return the long
	 */
	// Internal Processing delay is the time needed for each process
	public long internalDelay(Node src, Node dest, Object object);
	
	/**
	 * Switching delay.
	 *
	 * @param src the src
	 * @param dest the dest
	 * @param object the object
	 * @return the long
	 */
	// Switching delay is the time needed for src and dest to find a common channel at discovery phase
	public long switchingDelay(Node src, Node dest, Object object);
	
	/**
	 * Drop rate.
	 *
	 * @param src the src
	 * @param dest the dest
	 * @param object the object
	 * @return the double
	 */
	public double dropRate(Node src, Node dest, Object object);
	
	public int getRSSIdbm(Node node, Node ap);
}
