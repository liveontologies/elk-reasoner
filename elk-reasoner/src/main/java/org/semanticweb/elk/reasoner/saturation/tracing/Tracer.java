/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.Inference;

/**
 * The main object responsible for storing and manipulating traces.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public interface Tracer {

	/**
	 * 
	 */
	public interface Reader {

		public Inference getInference(Context context, Conclusion conclusion);
		
		public Inference getSubsumerInference(Context context, IndexedClassExpression conclusion);
		
		public Inference getBackwardLinkInference(Context context, IndexedPropertyChain linkRelation, Context linkTarget);
	}

	/**
	 * 
	 */
	public interface Writer {

		public boolean addInference(Context context, Conclusion conclusion, Inference inference);
	}

	public Reader getReader();

	public Writer getWriter();
}
