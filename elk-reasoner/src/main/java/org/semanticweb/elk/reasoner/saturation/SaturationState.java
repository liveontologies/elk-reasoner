/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation;

import java.util.Collection;

import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.RuleApplicationVisitor;

/**
 * TODO javadoc
 * 
 * Represents the state of saturation which can be changed by applying reasoning
 * rules and processing their conclusions.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public interface SaturationState {

	/**
	 * 
	 * @return
	 */
	public Collection<Context> getContexts();
	
	public Context getContext(IndexedClassExpression ice);
	
	public OntologyIndex getOntologyIndex();
	
	public Collection<IndexedClassExpression> getNotSaturatedContexts();

	/**
	 * Creates a new {@link ExtendedSaturationStateWriter} for modifying this
	 * {@link SaturationState} associated with the given
	 * {@link ContextCreationListener}. If {@link ContextCreationListener} is
	 * not thread safe, the calls of the methods should be synchronized
	 * 
	 */
	public ExtendedSaturationStateWriter getExtendedWriter(
			ContextCreationListener contextCreationListener,
			ContextModificationListener contextModificationListener,
			RuleApplicationVisitor ruleAppVisitor,
			ConclusionVisitor<?> conclusionVisitor,
			boolean trackNewContextsAsUnsaturated);

	public BasicSaturationStateWriter getWriter(
			ContextModificationListener contextModificationListener,
			ConclusionVisitor<?> conclusionVisitor);
	
	public BasicSaturationStateWriter getWriter(ConclusionVisitor<?> conclusionVisitor);

	public ExtendedSaturationStateWriter getExtendedWriter(ConclusionVisitor<?> conclusionVisitor);
	

}
