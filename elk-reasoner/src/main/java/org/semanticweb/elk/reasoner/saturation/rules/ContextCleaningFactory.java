/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.rules;

import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.conclusions.DisjointnessAxiom;
import org.semanticweb.elk.reasoner.saturation.conclusions.SuperClassExpression;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * Creates an engine which works as the de-application engine except that it
 * doesn't modify saturated contexts. The engine is used to "clean" contexts
 * after de-application but if the context is saturated, then cleaning is
 * unnecessary because it's not going to get any extra conclusions after
 * re-application.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ContextCleaningFactory extends RuleDeapplicationFactory {

	public ContextCleaningFactory(final SaturationState saturationState, boolean trackModifiedContexts) {
		super(saturationState, trackModifiedContexts);
	}

	@Override
	public Engine getEngine() {
		return new DeletionEngine(new PreApplyConclusionVisitor(), new DeleteConclusionVisitor());
	}

	/**
	 * Used to check whether conclusions are contained in the context
	 * but also returns false for context-modifying conclusions
	 * if the context is saturated
	 */
	protected class PreApplyConclusionVisitor extends ContainsConclusionVisitor {

		@Override
		protected Boolean visitSuperclass(SuperClassExpression sce, Context context) {
			return !context.isSaturated() && context.containsSuperClassExpression(sce.getExpression());
		}		
		
		@Override
		public Boolean visit(DisjointnessAxiom axiom, Context context) {
			return !context.isSaturated() && context.containsDisjointnessAxiom(axiom.getAxiom()) > 0;
		}		
	}	
}