/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.Subsumer;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * Represents an inference of the form A => B, B => C is in the the ontology, thus A => C
 * 
 * TODO represent the side condition explicitly
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class SubClassOfInference extends AbstractInference {
	
	/**
	 * This is B in the example above.
	 */
	private final Subsumer subsumer_;

	public SubClassOfInference(Subsumer subsumer) {
		subsumer_ = subsumer;
	}
	
	public Subsumer getSubsumer() {
		return subsumer_;
	}

	@Override
	public <O> O accept(ConclusionVisitor<O> conclusionVisitor, Context defaultContext) {
		return subsumer_.accept(conclusionVisitor, defaultContext);
	}
	
	
}
