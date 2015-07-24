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

import java.util.EventObject;

// TODO: Auto-generated Javadoc
/**
 * The Class wifiP2pEvent.
 */
public class wifiP2pEvent extends EventObject{

	/** This is an standard event class which recommended by Java event listening framwork. */
	private static final long serialVersionUID = 1L;
	
	/** The event. */
	private String event;
	
	/** The obj. */
	private Object obj;
	
	/**
	 * Instantiates a new wifi p2p event.
	 *
	 * @param source the source
	 * @param event the event
	 * @param obj the obj
	 */
	public wifiP2pEvent(Object source, String event, Object obj) {
		super(source);
		this.event = event;
		this.obj = obj;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "wifiP2pEvent [event=" + event + "]";
	}

	/**
	 * Gets the event.
	 *
	 * @return the event
	 */
	public String getEvent() {
		return event;
	}
	
	/**
	 * Gets the obj.
	 *
	 * @return the obj
	 */
	public Object getObj() {
		return obj;
	}	
}
