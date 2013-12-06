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
 * The main object responsible for storing and retrieving traces.
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

		/**
		 * 
		 * @param context
		 * @param conclusion
		 * @return
		 */
		public Iterable<Inference> getInferences(Context context, Conclusion conclusion);
		
		/**
		 * 
		 * @param context
		 * @param conclusion
		 * @return
		 */
		public Iterable<Inference> getSubsumerInferences(Context context, IndexedClassExpression conclusion);
		
		/**
		 * 
		 * @param context
		 * @param linkRelation
		 * @param linkSource
		 * @return
		 */
		public Iterable<Inference> getBackwardLinkInferences(Context context, IndexedPropertyChain linkRelation, Context linkSource);
	}

	/**
	 * 
	 */
	public interface Writer {
		
		/**
		 * 
		 * @param context
		 * @param conclusion
		 * @param inference
		 * @return
		 */
		public boolean addInference(Context context, Conclusion conclusion, Inference inference);
	}

	/**
	 * 
	 * @return
	 */
	public Reader getReader();

	/**
	 * 
	 * @return
	 */
	public Writer getWriter();
}
