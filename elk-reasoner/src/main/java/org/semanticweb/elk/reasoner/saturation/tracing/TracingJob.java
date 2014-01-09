/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.SaturationJob;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class TracingJob extends SaturationJob<IndexedClassExpression> {

	private final Conclusion conclusion_;
	
	public TracingJob(IndexedClassExpression input, Conclusion conclusion) {
		super(input);
		conclusion_ = conclusion;
	}

	public Conclusion getConclusion() {
		return conclusion_;
	}
}
