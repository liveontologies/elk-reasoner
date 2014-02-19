/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.AbstractConclusionVisitor;

/**
 * Collects roots of all contexts which must have been traced 
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class TracedContextsCollector extends AbstractConclusionVisitor<IndexedClassExpression, Boolean> {

	private final Set<IndexedClassExpression> tracedRoots_ = new HashSet<IndexedClassExpression>();
	
	@Override
	protected Boolean defaultVisit(Conclusion conclusion, IndexedClassExpression root) {
		tracedRoots_.add(conclusion.getSourceRoot(root));
		
		return true;
	}

	public Set<IndexedClassExpression> getTracedRoots() {
		return tracedRoots_;
	}
}
