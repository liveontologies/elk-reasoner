/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation;

import java.util.Collection;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.CompositionRuleApplicationVisitor;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface ExtendedSaturationState extends SaturationState {

	public Collection<IndexedClassExpression> getNotSaturatedContexts();

	/**
	 * Creates a new {@link ExtendedSaturationStateWriter} for modifying this
	 * {@link SaturationState} associated with the given
	 * {@link ContextCreationListener}. If {@link ContextCreationListener} is
	 * not thread safe, the calls of the methods should be synchronized
	 * 
	 * The passed rule application visitor is used to apply initialization rules
	 * to the newly created contexts
	 * 
	 */
	public ExtendedSaturationStateWriter getExtendedWriter(
			ContextCreationListener contextCreationListener,
			ContextModificationListener contextModificationListener,
			CompositionRuleApplicationVisitor ruleAppVisitor,
			ConclusionVisitor<?, Context> conclusionVisitor,
			boolean trackNewContextsAsUnsaturated);
	/**
	 * TODO
	 * @param contextModificationListener
	 * @param conclusionVisitor
	 * @return
	 */
	public BasicSaturationStateWriter getWriter(
			ContextModificationListener contextModificationListener,
			ConclusionVisitor<?, Context> conclusionVisitor);
}
