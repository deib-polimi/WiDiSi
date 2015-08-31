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

import java.util.HashMap;

import peersim.core.Node;

// TODO: Auto-generated Javadoc
/**
 * The Class wifiP2pService.
 */
public class wifiP2pService {
	
	/** The service name. */
	public String serviceName = "";					// Service instanceName
	
	/** The service type. */
	public String serviceType = "";					// Service registerationType
	
	/** The service record. */
	public HashMap<String, String> serviceRecord;	// Record
	
	/** The service holder address. */
	protected Node serviceHolder= null;
	
	/**
	 * Instantiates a new wifi p2p service.
	 *
	 * @param name the name
	 * @param type the type
	 * @param record the record
	 */
	public wifiP2pService (String name, String type, HashMap<String, String> record){
		serviceName = name;
		serviceType = type;
		serviceRecord = record;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "wifiP2pService [serviceName=" + serviceName + ", serviceType="
				+ serviceType + ", serviceRecord=" + serviceRecord + "]";
	}
}
