package org.semanticweb.elk.reasoner.saturation.elkrulesystem;

import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.rulesystem.Context;

/**
 * This interface is implemented by {@link Context}s that are used in computing
 * and storing superclass relationships. They provide basic methods for
 * accessing this information.
 * 
 * @author Markus Kroetzsch
 * 
 */
public interface ContextClassSaturation extends Context {

	/**
	 * Get the set of super class expressions that have been derived for this
	 * context so far.
	 * 
	 * @return the set of derived indexed class expressions
	 */
	public Set<IndexedClassExpression> getSuperClassExpressions();

	/**
	 * Mark context as saturated. A context is saturated if all superclass
	 * expressions of the root expression have been computed.
	 */
	public void setSaturated();

	/**
	 * Return if context is saturated. A context is saturated if all superclass
	 * expressions of the root expression have been computed. This needs to be
	 * set explicitly by some processor.
	 * 
	 * @return <tt>true</tt> if this context is saturated and <tt>false</tt>
	 *         otherwise
	 */
	public boolean isSaturated();
	
	/**
	 * Return true if this context is set to isSatisfiable. A value of true
	 * means that owl:Nothing was stored as a superclass in this context.
	 * 
	 * @return
	 */
	public boolean isSatisfiable();

	/**
	 * Set the satisfiability of this context. A value of true means that
	 * owl:Nothing was stored as a superclass in this context.
	 * 
	 * @param satisfiable
	 */
	public void setSatisfiable(boolean satisfiable);

}
