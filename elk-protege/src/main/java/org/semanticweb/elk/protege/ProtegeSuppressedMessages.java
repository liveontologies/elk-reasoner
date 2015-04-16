package org.semanticweb.elk.protege;

import java.util.HashMap;
import java.util.Map;

import org.semanticweb.elk.protege.preferences.ElkWarningPreferences;

/**
 * Manages ELK warning messages that are suppressed, i.e., should not be
 * presented to the user
 * 
 * @author "Yevgeny Kazakov"
 */
public class ProtegeSuppressedMessages {

	private static final ProtegeSuppressedMessages INSTANCE_ = new ProtegeSuppressedMessages();

	private final Map<String, Integer> suppressedWarningCounts_;

	public ProtegeSuppressedMessages() {
		suppressedWarningCounts_ = new HashMap<String, Integer>();
	}

	public static ProtegeSuppressedMessages getInstance() {
		return INSTANCE_;
	}

	/**
	 * Loads the suppressed message types from the preferences, keeping the
	 * counters if there were any
	 */
	public ProtegeSuppressedMessages reload() {
		ElkWarningPreferences elkWarningPrefs = new ElkWarningPreferences()
				.load();
		suppressedWarningCounts_.keySet().retainAll(
				elkWarningPrefs.suppressedWarningTypes);
		for (String warningType : elkWarningPrefs.suppressedWarningTypes) {
			if (suppressedWarningCounts_.get(warningType) == null)
				suppressedWarningCounts_.put(warningType, 0);
		}
		return this;
	}

	public void addWarningType(String newWarningType) {
		suppressedWarningCounts_.put(newWarningType, 0);
		ElkWarningPreferences elkWarningPrefs = new ElkWarningPreferences()
				.load();
		elkWarningPrefs.suppressedWarningTypes.add(newWarningType);
		elkWarningPrefs.save();
	}

	/**
	 * 
	 * @param warningType
	 * @return {@code true} if the warningType is suppressed and increments the
	 *         counter for this warningType, or returns {@code false} otherwise
	 */
	public boolean checkSuppressed(String warningType) {
		Integer count = suppressedWarningCounts_.get(warningType);
		if (count == null)
			return false;
		// else
		suppressedWarningCounts_.put(warningType, count + 1);
		return true;
	}

	/**
	 * @param warningType
	 * @return the current value of the counter for the given warningType
	 */
	public int getCount(String warningType) {
		return suppressedWarningCounts_.get(warningType);
	}

}
