/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * The extended writer for situations when new contexts may need to be
 * created/initialized or removed. With every
 * {@link ExtendedSaturationStateWriter} one can register a
 * {@link ContextCreationListener} that will be executed every time this
 * {@link ExtendedSaturationStateWriter} creates a new {@code Context}. Although
 * all functions of this {@link ExtendedSaturationStateWriter} are thread safe,
 * the function of the {@link ContextCreationListener} might not be, in which
 * the access of functions of {@link ExtendedSaturationStateWriter} should be
 * synchronized between threads.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public interface ExtendedSaturationStateWriter extends
		BasicSaturationStateWriter {

	public Context getCreateContext(IndexedClassExpression root);

	public void initContext(Context context);

	public void removeContext(Context context);
}
