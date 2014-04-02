/**
 * 
 */
package org.semanticweb.elk.alc.saturation.reduction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.semanticweb.elk.alc.indexing.hierarchy.IndexedClass;

/**
 * Contains direct subsumers and equivalent classes for a specific class.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class SubsumptionReduct {

	final List<IndexedClass> equivalent = new ArrayList<IndexedClass>(1);
	
	final List<IndexedClass> directSubsumers = new ArrayList<IndexedClass>(4);
	
	public Collection<IndexedClass> getEquivalentClasses() {
		return equivalent;
	}
	
	public Collection<IndexedClass> getDirectSubsumers() {
		return directSubsumers;
	}
}
