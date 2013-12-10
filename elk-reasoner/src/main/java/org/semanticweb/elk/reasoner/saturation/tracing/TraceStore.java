/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.Inference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.InferenceVisitor;

/**
 * The main object responsible for storing and retrieving traces.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public interface TraceStore {

	/**
	 * 
	 */
	public interface Reader {

		/**
		 * 
		 * @param context
		 * @param conclusion
		 * @param visitor
		 */
		public void accept(Context context, Conclusion conclusion, InferenceVisitor<?> visitor);
		
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
