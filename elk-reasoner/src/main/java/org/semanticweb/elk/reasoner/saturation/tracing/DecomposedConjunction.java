/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.saturation.conclusions.PositiveSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.PositiveSubsumerImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.Subsumer;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.tracing.util.TracingUtils;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class DecomposedConjunction extends PositiveSubsumerImpl implements PositiveSubsumer, TracedConclusion {

	private final IndexedObjectIntersectionOf conjunction_;
	
	DecomposedConjunction(IndexedObjectIntersectionOf conjunction, IndexedClassExpression expression) {
		super(expression);
		conjunction_ = conjunction;
	}

	public Subsumer getConjunction() {
		return TracingUtils.getSubsumerWrapper(conjunction_);
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
