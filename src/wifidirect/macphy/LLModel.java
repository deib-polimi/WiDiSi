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

import peersim.config.Configuration;
import peersim.core.Node;
import peersim.core.Protocol;
import wifidirect.nodemovement.CoordinateKeeper;
import wifidirect.nodemovement.NodeMovement;

// TODO: Auto-generated Javadoc
/**
 * The Class LLModel.
 */
public class LLModel implements LowLayerModel, Protocol{
	
	/** The coordinates pid. */
	private int coordinatesPid;
	
	/** The Constant frequency. */
	private static final double frequency = 2400; // in MHz -- Frequency
	
	/** The Constant Gtx. */
	private static final double Gtx = 5; // in dBi -- Gain TX antenna
	
	/** The Constant Grx. */
	private static final double Grx = 5; // in dBi -- Gain RX antenna:
	
	/** The rate. */
	private double rate;
	
	/**
	 * Instantiates a new LL model.
	 *
	 * @param prefix the prefix
	 */
	public LLModel(String prefix) {
		coordinatesPid 	 = Configuration.getPid(prefix + "." + "coordinate");
		rate = 0;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public LLModel clone(){
		LLModel LLM = null;
		try { LLM = (LLModel) super.clone(); }
		catch( CloneNotSupportedException e ) {} // never happens
		LLM.coordinatesPid = coordinatesPid;
		LLM.rate = 0;
		return LLM;	
	}

	/* (non-Javadoc)
	 * @see peersim.wifidirect.macphy.LowLayerModel#dropRate(peersim.core.Node, peersim.core.Node, java.lang.Object)
	 */
	@Override
	public double dropRate(Node src, Node dest, Object object) {
		/*
		 * 	- Calculates the Path Loss (attenuation in dB) in a free field like space communications.
			- Also the distance can be calculated if the Path Loss is given (in dB).
			- Substract the field attenuation from the TX power in dBm to get the power in dBm at the RX input.
			- Convert the TX power from watt to dBm and the RX power from dBm to uV with use of one of the calculators.
			- The formula is: PATH LOSS(dB) = 32.44 + 20*log(F(MHz)) + 20*log(D(km)) - Gtx(dBi) - Grx(dBi).
		 */
		
		double loss = 32.44 + 20*Math.log10(frequency) + 20*Math.log10(distance(src, dest, coordinatesPid)) - Gtx - Grx;
		if (loss<75) {
			rate = 0;
		}if(loss>90){
			rate = 1;
		}else{
			rate = 0.066*loss-5;
		}
		return rate;
	}
	
	@Override
	public int getRSSIdbm(Node node, Node ap){
		double loss = 32.44 + 20*Math.log10(frequency) + 20*Math.log10(distance(node, ap, coordinatesPid)) - Gtx - Grx;
		return (int) loss*(-1);
	}
	/**
	 * Distance.
	 *
	 * @param new_node the new_node
	 * @param old_node the old_node
	 * @param coordPid the coord pid
	 * @return the double
	 */
	// Naser: Calculating distance
		public static double distance(Node new_node, Node old_node, int coordPid) {
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


		/* (non-Javadoc)
		 * @see peersim.wifidirect.macphy.LowLayerModel#channelDelay(peersim.core.Node, peersim.core.Node, java.lang.Object)
		 */
		@Override
		public long channelDelay(Node src, Node dest, Object object) {
			// This method should return the channel dealy between src and dest in each call
			// for now we just assume that the delay is constant
			// It is open for furthur implementation in the future
			long Channel = 400;  //milliseconds
			return (long)(Channel/NodeMovement.CycleLenght);
		}


		/* (non-Javadoc)
		 * @see peersim.wifidirect.macphy.LowLayerModel#authenticationDelay(peersim.core.Node, peersim.core.Node, java.lang.Object)
		 */
		@Override
		public long authenticationDelay(Node src, Node dest, Object object) {
			long  Authentication= 100;  //milliseconds
			return (long)(Authentication/NodeMovement.CycleLenght);
		}


		/* (non-Javadoc)
		 * @see peersim.wifidirect.macphy.LowLayerModel#encryptiondelay(peersim.core.Node, peersim.core.Node, java.lang.Object)
		 */
		@Override
		public long encryptiondelay(Node src, Node dest, Object object) {
			long  Encryption= 100;  //milliseconds
			return (long)(Encryption/NodeMovement.CycleLenght);
		}


		/* (non-Javadoc)
		 * @see peersim.wifidirect.macphy.LowLayerModel#powerManagementDelay(peersim.core.Node, peersim.core.Node, java.lang.Object)
		 */
		@Override
		public long powerManagementDelay(Node src, Node dest, Object object) {
			long  PManagement= 100;  //milliseconds
			return (long)(PManagement/NodeMovement.CycleLenght);
		}


		/* (non-Javadoc)
		 * @see peersim.wifidirect.macphy.LowLayerModel#internalDelay(peersim.core.Node, peersim.core.Node, java.lang.Object)
		 */
		@Override
		public long internalDelay(Node src, Node dest, Object object) {
			long  Internal= 200;  //milliseconds
			return (long)(Internal/NodeMovement.CycleLenght);
		}


		/* (non-Javadoc)
		 * @see peersim.wifidirect.macphy.LowLayerModel#switchingDelay(peersim.core.Node, peersim.core.Node, java.lang.Object)
		 */
		@Override
		public long switchingDelay(Node src, Node dest, Object object) {
			long  Switching= 500;  //milliseconds
			return (long)(Switching/NodeMovement.CycleLenght);
		}
}
