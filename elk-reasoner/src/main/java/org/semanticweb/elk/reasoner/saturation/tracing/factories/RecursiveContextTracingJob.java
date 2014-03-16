/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.factories;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;

/**
 * An extension that stores the target {@link Conclusion}, should be used for
 * recursive context tracing.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class RecursiveContextTracingJob extends ContextTracingJob {

	private final Conclusion target_;
	
	public RecursiveContextTracingJob(IndexedClassExpression input, Conclusion t) {
		super(input);
		target_ = t;
	}

	public Conclusion getTarget() {
		return target_;
	}
}
