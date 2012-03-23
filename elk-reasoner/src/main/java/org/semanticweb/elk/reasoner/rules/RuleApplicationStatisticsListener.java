package org.semanticweb.elk.reasoner.rules;

import org.apache.log4j.Logger;

/**
 * Listener object for accumulating statistics of rule applications. Each object
 * should be used only from one thread as operations are not thread-safe.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class RuleApplicationStatisticsListener {

	// logger for events
	private static final Logger LOGGER_ = Logger
			.getLogger(RuleApplicationStatisticsListener.class);

	private int derivedNo = 0;
	private int derivedInfNo = 0;
	private int backLinkNo = 0;
	private int backLinkInfNo = 0;
	private int propNo = 0;
	private int propInfNo = 0;
	private int forwLinkNo = 0;
	private int forwLinkInfNo = 0;

	void incrementDerivedNo() {
		derivedNo++;
	}

	void incrementDerivedInfNo() {
		derivedInfNo++;
	}

	void incrementBackLinkNo() {
		backLinkNo++;
	}

	void incrementBackLinkInfNo() {
		backLinkInfNo++;
	}

	void incrementPropNo() {
		propNo++;
	}

	void incrementPropInfNo() {
		propInfNo++;
	}

	void incrementForwLinkNo() {
		forwLinkNo++;
	}

	void incrementForwLinkInfNo() {
		forwLinkInfNo++;
	}

	/**
	 * Prints the aggregated statistics for several statistics listeners
	 */
	public static void printListener(
			Iterable<RuleApplicationStatisticsListener> listeners) {

		int derivedNoSum = 0;
		int derivedInfNoSum = 0;
		int backLinkNoSum = 0;
		int backLinkInfNoSum = 0;
		int propNoSum = 0;
		int propInfNoSum = 0;
		int forwLinkNoSum = 0;
		int forwLinkInfNoSum = 0;

		for (RuleApplicationStatisticsListener listener : listeners) {
			derivedNoSum += listener.derivedNo;
			derivedInfNoSum += listener.derivedInfNo;
			backLinkNoSum += listener.backLinkNo;
			backLinkInfNoSum += listener.backLinkInfNo;
			propNoSum += listener.propNo;
			propInfNoSum += listener.propInfNo;
			forwLinkNoSum += listener.forwLinkNo;
			forwLinkInfNoSum += listener.forwLinkInfNo;
		}

		if (LOGGER_.isDebugEnabled()) {
			LOGGER_.debug("Derived Produced/Unique:" + derivedInfNoSum + "/"
					+ derivedNoSum);
			LOGGER_.debug("Backward Links Produced/Unique:" + backLinkInfNoSum
					+ "/" + backLinkNoSum);
			LOGGER_.debug("Propagations Produced/Unique:" + propInfNoSum + "/"
					+ propNoSum);
			LOGGER_.debug("Forward Links Produced/Unique:" + forwLinkInfNoSum
					+ "/" + forwLinkNoSum);
		}

	}

}
