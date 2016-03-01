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

/*
 * PeerSim Unreliable Transport is modified in order to change the package drop rate dynamiclly 
 * based on the distance of the sender and receiver. 
 * for now we only used a simple model for calculating the loss. However, later we could use more sophesticated, and more accurate
 * model. 
 * Modified by : Naser Derakhshan
 */

package wifidirect.p2pcore;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Node;
import peersim.transport.Transport;
import wifidirect.macphy.LowLayerModel;

// TODO: Auto-generated Javadoc
/**
 * The Class UnreliableTransportModified.
 */
public class UnreliableTransportModified implements Transport{


	//---------------------------------------------------------------------
	//Parameters
	//---------------------------------------------------------------------

	/**
	 * The name of the underlying transport protocol. This transport is
	 * extended with dropping messages.
	 * @config
	 */
	private static final String PAR_TRANSPORT = "transport";

	/** 
	 * String name of the parameter used to configure the probability that a 
	 * message sent through this transport is lost.
	 * @config
	 */
	//private static final String PAR_DROP = "drop";
	
	public static final String PAR_LLM = "lowlayermodel";
	
	/** The lowlayer pid. */
	public int lowlayerPid;
	

	//---------------------------------------------------------------------
	//Fields
	//---------------------------------------------------------------------

	/**  Protocol identifier for the support transport protocol. */
	private int transport;

	/**
	 *  Probability of dropping messages
	 *  Just Initial Value.
	 *
	 * @param prefix the prefix
	 */
	//private final float loss;

	//---------------------------------------------------------------------
	//Initialization
	//---------------------------------------------------------------------

	/**
	 * Reads configuration parameter.
	 */
	public UnreliableTransportModified(String prefix)
	{
		transport 		= Configuration.getPid(prefix+"."+PAR_TRANSPORT);
		lowlayerPid 	= Configuration.getPid(prefix + "." + PAR_LLM);
	}

	//---------------------------------------------------------------------

	/**
	 * Returns <code>this</code>. This way only one instance exists in the system
	 * that is linked from all the nodes. This is because this protocol has no
	 * state that depends on the hosting node.
	 *
	 * @return the object
	 */
	public Object clone()
	{
		return this;
	}

	//---------------------------------------------------------------------
	//Methods
	//---------------------------------------------------------------------

	/**
	 *  Sends the message according to the underlying transport protocol.
	 * With the configured probability, the message is not sent (i.e. the method does
	 * nothing).
	 *
	 * @param src the src
	 * @param dest the dest
	 * @param msg the msg
	 * @param pid the pid
	 */
	public void send(Node src, Node dest, Object msg, int pid)
	{
		// First we have to calculate loss based on the distanse
		// We use a simple formula here
		// If the distance is less than half of the radio range, loss is zero.
		// else loss is proportional to the following formula:
		// y=(20)x - 2 (if(loss<0) loss=0 and if(loss>1)loss=1;)
		LowLayerModel llModel = (LowLayerModel) src.getProtocol(lowlayerPid);
		double loss = llModel.dropRate(src, dest, null);
		

		try
		{
			if (CommonState.r.nextDouble() >= loss)
			{
				// Message is not lost
				Transport t = (Transport) src.getProtocol(transport);
				t.send(src, dest, msg, pid);
			}
		}
		catch(ClassCastException e)
		{
			throw new IllegalArgumentException("Protocol " +
					Configuration.lookupPid(transport) + 
					" does not implement Transport");
		}
	}

	/**
	 *  Returns the latency of the underlying protocol.
	 *
	 * @param src the src
	 * @param dest the dest
	 * @return the latency
	 */
	public long getLatency(Node src, Node dest)
	{
		Transport t = (Transport) src.getProtocol(transport);
		return t.getLatency(src, dest);
	}
}
