/*
 * 
 */
package wifidirect.JUnit;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

// TODO: Auto-generated Javadoc
/**
 * The Class TestRunner.
 */
public class TestRunner {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		Result result = JUnitCore.runClasses(JunitTestSuite.class);
		for (Failure failure : result.getFailures()) {
			System.out.println(failure.toString());
		}
		System.out.println(result.wasSuccessful());

	}

}
