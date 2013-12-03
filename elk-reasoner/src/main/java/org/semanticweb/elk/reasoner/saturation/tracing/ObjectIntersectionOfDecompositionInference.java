/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.Subsumer;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * Represents an inference of the form A => C_1 \and C_2, thus A => C_i
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ObjectIntersectionOfDecompositionInference extends AbstractInference {

	private final Subsumer conjunction_;

	public ObjectIntersectionOfDecompositionInference(Subsumer subsumer) {
		conjunction_ = subsumer;
	}
	
	public Subsumer getConjunction() {
		return conjunction_;
	}

	@Override
	public <O> O accept(ConclusionVisitor<O> conclusionVisitor, Context defaultContext) {
		return conjunction_.accept(conclusionVisitor, defaultContext);
	}
}
