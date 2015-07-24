/*
 * 
 */
package wifidirect.JUnit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

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
		SimulatorForTest.main(null);
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
