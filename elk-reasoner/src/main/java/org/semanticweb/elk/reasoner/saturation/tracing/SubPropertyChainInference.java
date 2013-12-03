/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * Represents an inference of the form: A => R_1 some B, B => R_2 some C, and
 * the role index entails R_1 o R_2 => S and (S some C) occurs in the ontology,
 * thus A => S some C.
 * 
 * Here the context is where the composition actually happened, i.e. B. Both the
 * forward link and the backward link are stored in that context.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class SubPropertyChainInference extends AbstractForeignContextInference {

	private final BackwardLink backwardLink_;

	private final ForwardLink forwardLink_;

	SubPropertyChainInference(BackwardLink bwLink, ForwardLink fwLink,
			Context cxt) {
		super(cxt);
		backwardLink_ = bwLink;
		forwardLink_ = fwLink;
	}

	@Override
	public <O> O accept(ConclusionVisitor<O> conclusionVisitor,
			Context defaultContext) {
		backwardLink_.accept(conclusionVisitor, context);

		return forwardLink_.accept(conclusionVisitor, context);
	}

}
