/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.BaseConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * Collects all contexts which must have been traced 
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class TracedContextsCollector extends BaseConclusionVisitor<Boolean, Context> {

	private final Set<IndexedClassExpression> tracedRoots_ = new HashSet<IndexedClassExpression>();
	
	@Override
	protected Boolean defaultVisit(Conclusion conclusion, Context cxt) {
		tracedRoots_.add(conclusion.getSourceContext(cxt).getRoot());
		
		return true;
	}

	public Set<IndexedClassExpression> getTracedRoots() {
		return tracedRoots_;
	}
}
