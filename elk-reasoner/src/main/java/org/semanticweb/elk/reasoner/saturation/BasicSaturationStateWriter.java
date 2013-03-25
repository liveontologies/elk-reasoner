/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * Functions that can write the saturation state are grouped here. With every
 * {@link Writer} one can register a {@link ContextCreationListener} that will
 * be executed every time this {@link Writer} creates a new {@code Context}.
 * Although all functions of this {@link Writer} are thread safe, the function
 * of the {@link ContextCreationListener} might not be, in which the access of
 * functions of {@link Writer} should be synchronized between threads.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public interface BasicSaturationStateWriter {

	public IndexedClassExpression getOwlThing();

	public IndexedClassExpression getOwlNothing();

	public Context pollForActiveContext();

	public void produce(Context context, Conclusion conclusion);

	public boolean markAsNotSaturated(Context context);

	public void clearNotSaturatedContexts();
}
