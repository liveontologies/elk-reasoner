/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.rules;

import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.NegativeSuperClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.PositiveSuperClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.SuperClassExpression;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * Creates an engine which applies rules backwards, e.g., removes conclusions from the context instead of adding them
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class RuleDeapplicationFactory extends RuleApplicationFactory {

	public RuleDeapplicationFactory(OntologyIndex ontologyIndex) {
		super(ontologyIndex);
	}

	@Override
	public Engine getEngine() {
		return new RuleApplicationFactory.Engine(new DeleteConclusionVisitor());
	}

	/**
	 * Used to remove different kinds of conclusions from the context
	 */
	protected static class DeleteConclusionVisitor implements ConclusionVisitor<Boolean> {

		public Boolean visitSuperclass(SuperClassExpression sce, Context context) {
			if (context.removeSuperClassExpression(sce)) {
				return true;
			}
			
			return false;
		}		
		
		@Override
		public Boolean visit(NegativeSuperClassExpression negSCE,
				Context context) {
			return visitSuperclass(negSCE, context);
		}

		@Override
		public Boolean visit(PositiveSuperClassExpression posSCE,
				Context context) {
			return visitSuperclass(posSCE, context);
		}

		@Override
		public Boolean visit(BackwardLink link, Context context) {
			return !context.removeBackwardLink(link);
		}

		@Override
		public Boolean visit(ForwardLink link, Context context) {
			//statistics_.forwLinkInfNo++;
			return link.removeFromContextBackwardLinkRule(context);
			//statistics_.forwLinkNo++;
		}
	}	
}
