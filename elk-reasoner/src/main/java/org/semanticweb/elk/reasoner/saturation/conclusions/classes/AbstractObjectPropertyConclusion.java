/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.conclusions.classes;

import org.semanticweb.elk.reasoner.saturation.conclusions.model.ObjectPropertyConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ObjectPropertyConclusionEquality;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ObjectPropertyConclusionHash;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ObjectPropertyConclusionPrinter;

/**
 * A skeleton for implementation of {@link ObjectPropertyConclusion}s.
 * 
 * @author "Yevgeny Kazakov"
 */
public abstract class AbstractObjectPropertyConclusion implements
		ObjectPropertyConclusion {

	@Override
	public boolean equals(Object o) {
		return ObjectPropertyConclusionEquality.equals(this, o);
	}

	@Override
	public int hashCode() {
		return ObjectPropertyConclusionHash.hashCode(this);
	}

	@Override
	public String toString() {
		return ObjectPropertyConclusionPrinter.toString(this);
	}

}
