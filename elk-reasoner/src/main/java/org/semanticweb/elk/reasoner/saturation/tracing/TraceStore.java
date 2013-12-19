/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;

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
		public void accept(Context context, Conclusion conclusion, TracedConclusionVisitor<?,?> visitor);
		
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
		public boolean addInference(Context context, TracedConclusion conclusion);
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
