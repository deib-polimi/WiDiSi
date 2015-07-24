/*
 * 
 */
package wifidirect.nodemovement;

import peersim.config.Configuration;
import peersim.core.Linkable;
import peersim.dynamics.WireGraph;
import peersim.graph.Graph;

// TODO: Auto-generated Javadoc
/**
 * This class applies a HOT topology on a any {@link Linkable} implementing
 * protocol.
 * 
 * @author Naser Derakhshan
 */
public class WireInetTopology extends WireGraph {
	// ------------------------------------------------------------------------
	// Parameters
	// ------------------------------------------------------------------------
	/**
	 * The coordinate protocol to look at.
	 * 
	 * @config
	 */
	private static final String PAR_COORDINATES_PROT = "coord_protocol";

	/**  WiFi Range. */
	private static final String PAR_RANGE = "radio_range";

	// --------------------------------------------------------------------------
	// Fields
	// --------------------------------------------------------------------------

	/** Coordinate protocol pid. */
	private final int coordPid;

	/**  Wifi radio Range. */
	private final double radio_range;

	// --------------------------------------------------------------------------
	// Initialization
	// --------------------------------------------------------------------------

	/**
	 * Standard constructor that reads the configuration parameters. Normally
	 * invoked by the simulation engine.
	 * 
	 * @param prefix
	 *            the configuration prefix for this class
	 */
	public WireInetTopology(String prefix) {
		super(prefix);
		coordPid = Configuration.getPid(prefix + "." + PAR_COORDINATES_PROT);
		radio_range = Configuration.getDouble(prefix + "." + PAR_RANGE, 0.2);
	}

	/**
	 * Performs the actual wiring.
	 * @param g a {@link peersim.graph.Graph} interface object to work on.
	 */
	public void wire(Graph g) {

		GraphFactoryM.wireCordXY(g, coordPid, radio_range);

	}
}
