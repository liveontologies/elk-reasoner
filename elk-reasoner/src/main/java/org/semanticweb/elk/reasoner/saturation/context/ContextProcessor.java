package org.semanticweb.elk.reasoner.saturation.context;

/**
 * An abstract interfaces for implementing processors of {@link Context}s
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public interface ContextProcessor {

	/**
	 * @param context
	 *            the {@link Context} to be processed
	 * @return {@link true} if the {@link Context} has been modified as the
	 *         result of this operation
	 */
	public boolean process(Context context);

}
