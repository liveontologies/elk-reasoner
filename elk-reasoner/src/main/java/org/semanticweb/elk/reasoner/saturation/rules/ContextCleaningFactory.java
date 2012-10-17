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

	/**
	 * Used to clean modified contexts after deleting conclusions
	 * 
	 * @author Pavel Klinov
	 *
	 * pavel.klinov@uni-ulm.de
	 */
	public class CleaningEngine extends Engine {

		private CleaningEngine() {
			//cleaning engine deletes conclusions
			super(new DeleteConclusionVisitor());
		}
		
		@Override
		protected void process(Context context) {
			factoryStats_.contContextProcess++;
			
			for (;;) {
				Conclusion conclusion = context.takeToDo();
				if (conclusion == null)
					if (context.deactivate())
						// context was re-activated
						continue;
					else
						break;
				
				if (!context.isSaturated()) {
					// do not modify saturated contexts
					conclusion.apply(this, context);
				}
			}
		}
	}	
}
