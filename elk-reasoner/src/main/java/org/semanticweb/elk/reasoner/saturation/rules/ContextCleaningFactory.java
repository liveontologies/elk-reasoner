/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.rules;
/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.conclusions.DisjointnessAxiom;
import org.semanticweb.elk.reasoner.saturation.conclusions.SuperClassExpression;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * Creates an engine which works as the de-application engine except that it
 * doesn't modify saturated contexts. The engine is used to "clean" contexts
 * after de-application but if the context is saturated, then cleaning is
 * unnecessary because it's not going to get any extra super-classes after
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