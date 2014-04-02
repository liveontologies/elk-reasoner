/**
 * 
 */
package org.semanticweb.elk.alc.saturation;

import java.util.Set;

import org.semanticweb.elk.alc.indexing.hierarchy.IndexedClass;

/**
 * TODO
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class SaturatedContext {

	private final Set<IndexedClass> atomicSubsumers_;
	
	SaturatedContext(Set<IndexedClass> atomicSubsumers) {
		atomicSubsumers_ = atomicSubsumers;
	}
	
	public Set<IndexedClass> getAtomicSubsumers() {
		return atomicSubsumers_;
	}
}
