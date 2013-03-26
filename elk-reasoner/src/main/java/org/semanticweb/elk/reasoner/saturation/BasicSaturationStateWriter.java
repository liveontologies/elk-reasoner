/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * Functions that can write the saturation state are grouped here.
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

	public void resetContexts();
}
