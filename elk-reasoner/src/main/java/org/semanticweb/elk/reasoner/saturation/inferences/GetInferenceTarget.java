/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.inferences;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.conclusions.classes.ConclusionBaseFactory;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SaturationConclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * Given an {@link ClassInference}, returns the root of the context to which
 * this inference should be produced.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class GetInferenceTarget extends
		AbstractClassInferenceVisitor<Context, IndexedContextRoot> {

	private final SaturationConclusion.Factory factory_ = new ConclusionBaseFactory();
	
	@Override
	protected IndexedContextRoot defaultTracedVisit(ClassInference conclusion,
			Context premiseContext) {
		// by default produce to the context where the inference has been
		// made (where its premises are stored)
		return premiseContext.getRoot();
	}

	@Override
	public IndexedContextRoot visit(SubClassInclusionComposedObjectSomeValuesFrom conclusion,
			Context premiseContext) {
		return conclusion.getFirstPremise(factory_).getOriginRoot();
	}

	@Override
	public IndexedContextRoot visit(BackwardLinkComposition conclusion,
			Context premiseContext) {
		return conclusion.getThirdPremise(factory_).getTarget();
	}

	@Override
	public IndexedContextRoot visit(BackwardLinkReversed conclusion,
			Context premiseContext) {
		return conclusion.getPremise(factory_).getTarget();
	}

	@Override
	public IndexedContextRoot visit(
			BackwardLinkOfObjectSomeValuesFrom conclusion, Context premiseContext) {
		return IndexedObjectSomeValuesFrom.Helper.getTarget(conclusion
				.getDecomposedExistential());
	}
}
