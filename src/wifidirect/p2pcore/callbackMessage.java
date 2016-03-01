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

import applications.magnet.Routing.MacAddress;

// TODO: Auto-generated Javadoc
/**
 * The Class callbackMessage.
 * This message class could be used whenever a socket delivery is needed
 */
public class callbackMessage {
	   /**
     * User-defined message code so that the recipient can identify 
     * what this message is about. Each {@link Handler} has its own name-space
     * for message codes, so you do not need to worry about yours conflicting
     * with other handlers.
     */
    public int what;

    /**
     * arg1 and arg2 are lower-cost alternatives to using
     * {@link #setData(Bundle) setData()} if you only need to store a
     * few integer values.
     */
    public int arg1; 

    /**
     * arg1 and arg2 are lower-cost alternatives to using
     * {@link #setData(Bundle) setData()} if you only need to store a
     * few integer values.
     */
    public int arg2;

    /**
     * An arbitrary object to send to the recipient.  When using
     * {@link Messenger} to send the message across processes this can only
     * be non-null if it contains a Parcelable of a framework class (not one
     * implemented by the application).   For other data transfer use
     * {@link #setData}.
     * 
     * <p>Note that Parcelable objects here are not supported prior to
     * the {@link android.os.Build.VERSION_CODES#FROYO} release.
     */
    public Object obj;

    /**
     * additional filed that I addedto this class -- this is the last hop mac address
     * Naser
     */
    public MacAddress lastHopMacAddr;
    /**
     * Instantiates a new callback message.
     */
    public callbackMessage(){
    	
    }
    
    /**
     * Instantiates a new callback message.
     *
     * @param what the what
     * @param obj the obj
     */
    public callbackMessage(int what, Object obj){
    	this.what = what;
    	this.obj = obj;
    }
    
    /**
     * Instantiates a new callback message.
     *
     * @param what the what
     * @param obj the obj
     * @param arg1 the arg1
     */
    public callbackMessage(int what, Object obj, int arg1){
    	this.what = what;
    	this.obj = obj;
    	this.arg1 = arg1;
    }
    
    /**
     * Instantiates a new callback message.
     *
     * @param what the what
     * @param obj the obj
     * @param arg1 the arg1
     * @param arg2 the arg2
     */
    public callbackMessage(int what, Object obj, int arg1, int arg2){
    	this.what = what;
    	this.obj = obj;
    	this.arg1 = arg1;
    	this.arg2 = arg2;
    }
}
