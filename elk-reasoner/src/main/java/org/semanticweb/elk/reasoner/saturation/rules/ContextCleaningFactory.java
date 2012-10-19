/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.rules;

import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
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

	public ContextCleaningFactory(OntologyIndex ontologyIndex) {
		super(ontologyIndex);
	}

	@Override
	public Engine getEngine() {
		return new CleaningEngine();
	}

	/**
	 * Used to clean modified contexts after deleting conclusions
	 * 
	 * @author Pavel Klinov
	 *
	 * pavel.klinov@uni-ulm.de
	 */
	public class CleaningEngine extends DeletionEngine {

		private CleaningEngine() {
			//cleaning engine deletes conclusions
			super(new DeleteConclusionVisitor());
		}
		
		@Override
		protected boolean preApply(Conclusion conclusion, Context context) {
			// this engine should not modify saturated contexts
			return !context.isSaturated() && super.preApply(conclusion, context);
		}
	}	
}
