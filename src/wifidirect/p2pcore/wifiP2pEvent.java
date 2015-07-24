/*
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
