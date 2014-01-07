package org.semanticweb.elk.reasoner.saturation.context;

import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;

/**
 * An abstract interfaces for implementing processors of {@link Conclusion}s in
 * {@link Context}s
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public interface ConclusionProcessor {

	/**
	 * @param context
	 *            the {@link Context} in which to processed
	 * @param conclusion
	 *            the {@link Conclusion} to be processed
	 */
	public void process(Context context, Conclusion conclusion);

}
