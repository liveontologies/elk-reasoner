/**
 * 
 */
package org.semanticweb.elk.alc.saturation;

/**
 * A very simple bean for keeping the relevant metrics in one place.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class SaturationStatistics {

	public long addedConclusions = 0;
	
	public long removedConclusions = 0;
	
	public long inconsistentRoots = 0;
	
	public void add(SaturationStatistics stats) {
		addedConclusions += stats.addedConclusions;
		removedConclusions += stats.removedConclusions;
		inconsistentRoots += stats.inconsistentRoots;
	}
	
	public void reset() {
		addedConclusions = 0;
		removedConclusions = 0;
		inconsistentRoots = 0;
	}
}
