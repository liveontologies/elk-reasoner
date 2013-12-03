/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.Subsumer;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * Represents an inference of the form A => R some B, B => C, (R some C) occurs
 * negatively in the ontology, thus A => R some C.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ObjectSomeValuesFromInference extends AbstractForeignContextInference {

	private final Subsumer subsumer_;
	
	private final BackwardLink backwardLink_;
	
	ObjectSomeValuesFromInference(Subsumer subsumer, BackwardLink link, Context cxt) {
		super(cxt);
		subsumer_ = subsumer;
		backwardLink_ = link;
	}

	public Subsumer getSubsumer_() {
		return subsumer_;
	}

	public BackwardLink getBackwardLink_() {
		return backwardLink_;
	}

	@Override
	public <O> O accept(ConclusionVisitor<O> conclusionVisitor, Context defaultContext) {
		subsumer_.accept(conclusionVisitor, context);
		return backwardLink_.accept(conclusionVisitor, context);
	}
	
	
}
