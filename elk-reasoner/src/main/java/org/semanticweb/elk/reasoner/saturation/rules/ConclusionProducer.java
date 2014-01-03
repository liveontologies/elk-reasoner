package org.semanticweb.elk.reasoner.saturation.rules;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * An object using which {@link Conclusion}s of inferences can be produced
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public interface ConclusionProducer {

	/**
	 * Tells that the given {@link Conclusion} is derived in the given
	 * {@link Context}
	 * 
	 * @param context
	 * @param conclusion
	 */
	public void produce(Context context, Conclusion conclusion);

	/**
	 * Tells that the given {@link Conclusion} is derived for the
	 * {@link Context} which root is the given {@link IndexedClassExpression} as
	 * the root. It may be used instead of {
	 * {@link #produce(Context, Conclusion)} when the {@link Context} is not
	 * known.
	 * 
	 * @see Context#getRoot()
	 * 
	 * @param root
	 * @param conclusion
	 */
	public void produce(IndexedClassExpression root, Conclusion conclusion);

}
