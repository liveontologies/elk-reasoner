/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.NegativeSubsumerImpl;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ReflexiveSubsumer extends NegativeSubsumerImpl implements TracedConclusion {

	/**
	 * @param superClassExpression
	 */
	public ReflexiveSubsumer(IndexedObjectSomeValuesFrom existential) {
		super(existential);
	}

	public IndexedPropertyChain getRelation() {
		return ((IndexedObjectSomeValuesFrom) expression).getRelation();
	}
	
	@Override
	public <R, C> R acceptTraced(TracedConclusionVisitor<R, C> visitor, C parameter) {
		return visitor.visit(this, parameter);
	}

	@Override
	public Context getInferenceContext(Context defaultContext) {
		return defaultContext;
	}

}
