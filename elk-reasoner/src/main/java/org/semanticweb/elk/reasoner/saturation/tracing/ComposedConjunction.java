/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.NegativeSubsumerImpl;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.tracing.util.TracingUtils;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ComposedConjunction extends NegativeSubsumerImpl implements TracedConclusion {
	//TODO store this as ICE
	private final Conclusion first_;
	
	private final IndexedClassExpression second_;
	
	/**
	 * @param superClassExpression
	 */
	public ComposedConjunction(Conclusion subsumer, IndexedClassExpression conjunct, IndexedObjectIntersectionOf conjunction) {
		super(conjunction);
		first_ = subsumer;
		second_ = conjunct;
	}

	public Conclusion getFirstConjunct() {
		return first_;
	}
	
	public Conclusion getSecondConjunct() {
		return TracingUtils.getSubsumerWrapper(second_);
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
