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
package wifidirect.p2pcore;

import peersim.config.*;
import peersim.core.*;
import peersim.edsim.*;
import peersim.transport.Transport;
import wifidirect.macphy.LowLayerModel;

// TODO: Auto-generated Javadoc
/**
 * The Class UniformRandomTransportModified.
 */
public final class UniformRandomTransportModified implements Transport
{
	
//---------------------------------------------------------------------
//Fields
//---------------------------------------------------------------------
	
/** The error. */
// How exact is the calculation 0 means completely wrong and 1 is completely accurate
private final double error;

/** The lowlayermodel pid. */
private final int lowlayermodelPid;

/** The delay type. */
private final int delayType;


/** The Constant Zero_Delay. */
//delay types
private static final int Zero_Delay 				= 0;

/** The Constant Device_Discovery_Delay. */
private static final int Device_Discovery_Delay 	= 1;

/** The Constant service_Discovery_Delay. */
private static final int service_Discovery_Delay 	= 2;

/** The Constant Message_Delivery_Delay. */
private static final int Message_Delivery_Delay 	= 3;

/** The Constant Group_Invitation_Delay. */
private static final int Group_Invitation_Delay 	= 4;

/** The Constant Internal_Processing_Delay. */
private static final int Internal_Processing_Delay 	= 5;
	
//---------------------------------------------------------------------
//Initialization
//---------------------------------------------------------------------

/**
 * Reads configuration parameter.
 *
 * @param prefix the prefix
 */
public UniformRandomTransportModified(String prefix)
{
	error 				= Configuration.getDouble(prefix + "." + "error");
	lowlayermodelPid 	= Configuration.getPid(prefix + "." + "lowlayermodel");
	delayType 			= Configuration.getInt(prefix + "." + "delaytype");
}

//---------------------------------------------------------------------

/**
 * Returns <code>this</code>. This way only one instance exists in the system
 * that is linked from all the nodes. This is because this protocol has no
 * node specific state.
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
 * Delivers the message with a random
 * delay, that is drawn from the configured interval according to the uniform
 * distribution.
 *
 * @param src the src
 * @param dest the dest
 * @param msg the msg
 * @param pid the pid
 */
public void send(Node src, Node dest, Object msg, int pid)
{
	long delay = 0;
	LowLayerModel llModel = (LowLayerModel) src.getProtocol(lowlayermodelPid);
	switch (delayType)
	{
	case Zero_Delay:
		delay = 0;
		EDSimulator.add(delay, msg, dest, pid);
		break;
		
	case Device_Discovery_Delay:
		// Device discovery delay is a delay needed that device src find device dest
		// this delay comprises Switching delay, channel delay, internal processing delay, and power mamnagement delay
		long ddDelay = 	llModel.switchingDelay(src, dest, msg) + llModel.channelDelay(src, dest, msg) + 
						llModel.powerManagementDelay(src, dest, msg) + llModel.internalDelay(src, dest, msg);
		// now we apply the error rate of delay calculation here
		delay = (long)((ddDelay-error*ddDelay)+ CommonState.r.nextLong((long)(ddDelay+2*error*ddDelay)));
		EDSimulator.add(delay, msg, dest, pid);
		break;
	
	case service_Discovery_Delay:
		// Service discovery Delay is performed after two devices are found each other. so we do not have Swtitching delay
		// however, the service discovery consist of sending and receiving some messages which is the reason that we multiply the channel delay and other
		// delays by two
		long sdDelay = 	2*(llModel.channelDelay(src, dest, msg) + 
						llModel.powerManagementDelay(src, dest, msg) + llModel.internalDelay(src, dest, msg));
		// now we apply the error rate of delay calculation here
		delay = ((long)(sdDelay-error*sdDelay)+ CommonState.r.nextLong((long)(sdDelay+2*error*sdDelay)));
		EDSimulator.add(delay, msg, dest, pid);
		break;
		
	case Message_Delivery_Delay:
		long mdDelay = 	llModel.channelDelay(src, dest, msg) + llModel.encryptiondelay(src, dest, msg);
						//llModel.powerManagementDelay(src, dest, msg) + llModel.internalDelay(src, dest, msg);
		// now we apply the error rate of delay calculation here
		delay = ((long)(mdDelay-error*mdDelay)+ CommonState.r.nextLong((long)(mdDelay+2*error*mdDelay)));
		EDSimulator.add(delay, msg, dest, pid);
		break;
		
	case Group_Invitation_Delay:
		long giDelay = 	llModel.channelDelay(src, dest, msg) + llModel.encryptiondelay(src, dest, msg) +
						llModel.powerManagementDelay(src, dest, msg) + llModel.internalDelay(src, dest, msg) + llModel.authenticationDelay(src, dest, msg);
		// now we apply the error rate of delay calculation here
		delay = ((long)(giDelay-error*giDelay)+ CommonState.r.nextLong((long)(giDelay+2*error*giDelay)));
		EDSimulator.add(delay, msg, dest, pid);
		break;
		
	case Internal_Processing_Delay:
		delay = llModel.internalDelay(src, dest, msg);
		EDSimulator.add(delay, msg, dest, pid);
		break;
	
	default:
		EDSimulator.add(0, msg, dest, pid);
		break;
	}
}

/**
 * Returns a random
 * delay, that is drawn from the configured interval according to the uniform
 * distribution.
 *
 * @param src the src
 * @param dest the dest
 * @return the latency
 */
public long getLatency(Node src, Node dest)
{
	LowLayerModel llModel = (LowLayerModel) src.getProtocol(lowlayermodelPid);
	switch (delayType)
	{
	case Zero_Delay:
		return 0;
		
	case Device_Discovery_Delay:
		// Device discovery delay is a delay needed that device src find device dest
		// this delay comprises Switching delay, channel delay, internal processing delay, and power mamnagement delay
		long ddDelay = 	llModel.switchingDelay(src, dest, null) + llModel.channelDelay(src, dest, null) + 
						llModel.powerManagementDelay(src, dest, null) + llModel.internalDelay(src, dest, null);
		// now we apply the error rate of delay calculation here
		return (long)((ddDelay-error*ddDelay)+ CommonState.r.nextLong((long)(ddDelay+2*error*ddDelay)));
	
	case service_Discovery_Delay:
		// Service discovery Delay is performed after two devices are found each other. so we do not have Swtitching delay
		// however, the service discovery consist of sending and receiving some messages which is the reason that we multiply the channel delay and other
		// delays by two
		long sdDelay = 	2*(llModel.channelDelay(src, dest, null) + 
						llModel.powerManagementDelay(src, dest, null) + llModel.internalDelay(src, dest, null));
		// now we apply the error rate of delay calculation here
		return ((long)(sdDelay-error*sdDelay)+ CommonState.r.nextLong((long)(sdDelay+2*error*sdDelay)));
		
	case Message_Delivery_Delay:
		long mdDelay = 	llModel.channelDelay(src, dest, null) + llModel.encryptiondelay(src, dest, null) +
						llModel.powerManagementDelay(src, dest, null) + llModel.internalDelay(src, dest, null);
		// now we apply the error rate of delay calculation here
		return ((long)(mdDelay-error*mdDelay)+ CommonState.r.nextLong((long)(mdDelay+2*error*mdDelay)));
		
	case Group_Invitation_Delay:
		long giDelay = 	llModel.channelDelay(src, dest, null) + llModel.encryptiondelay(src, dest, null) +
						llModel.powerManagementDelay(src, dest, null) + llModel.internalDelay(src, dest, null) + llModel.authenticationDelay(src, dest, null);
		// now we apply the error rate of delay calculation here
		return ((long)(giDelay-error*giDelay)+ CommonState.r.nextLong((long)(giDelay+2*error*giDelay)));
		
	case Internal_Processing_Delay:
		return llModel.internalDelay(src, dest, null);
		
	default:
		return 0;
	}
}


}
