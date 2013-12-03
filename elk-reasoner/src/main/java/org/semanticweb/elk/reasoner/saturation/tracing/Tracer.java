/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;

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
