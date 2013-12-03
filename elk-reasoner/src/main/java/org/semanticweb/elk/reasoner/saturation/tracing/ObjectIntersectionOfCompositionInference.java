/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.Subsumer;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * Represents an inference of the form A => C_1, A => C_2, and C_1 \and C_2 occurs in the ontology, thus A => C_1 \and C_2.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ObjectIntersectionOfCompositionInference extends AbstractInference {

	private final Subsumer firstConjunct_;
	
	private final Subsumer secondConjunct_;
	
	public ObjectIntersectionOfCompositionInference(Subsumer first, Subsumer second) {
		firstConjunct_ = first;
		secondConjunct_ = second;
	}

	@Override
	public <O> O accept(ConclusionVisitor<O> conclusionVisitor, Context defaultContext) {
		firstConjunct_.accept(conclusionVisitor, defaultContext);
		
		return secondConjunct_.accept(conclusionVisitor, defaultContext);
	}
	
	public Subsumer getFirstSubsumer() {
		return firstConjunct_;
	}
	
	public Subsumer getSecondSubsumer() {
		return secondConjunct_;
	}
}
