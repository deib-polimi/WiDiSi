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
package wifidirect.JUnit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import peerSimEngine.Simulator;


// TODO: Auto-generated Javadoc
/**
 * The Class JunitTestSuite.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
	NodeInitializerTest.class,
	NodeMovementTest.class,
	WifiP2pGroupTest.class,
	EventListenersTest.class,
	nodeP2pInfoTest.class
})
public class JunitTestSuite {
	
	/**
	 * Sets the up.
	 *
	 * @throws Exception the exception
	 */
	@BeforeClass
	public static void setUp() throws Exception {
		System.out.println("setting up");
		Simulator.main(null);
	}

	/**
	 * Tear down.
	 *
	 * @throws Exception the exception
	 */
	@AfterClass
	public static void tearDown() throws Exception {
		System.out.println("tearing down");
	}
}
