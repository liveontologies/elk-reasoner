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
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ComposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.inferences.visitors.ComposedSubsumerInferenceVisitor;

/**
 * A {@link ComposedSubsumer} for {@link IndexedObjectIntersectionOf} obtained
 * from its conjunts.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ComposedConjunction extends
		AbstractComposedSubsumerInference<IndexedObjectIntersectionOf> {

	/**
	 */
	public ComposedConjunction(IndexedContextRoot inferenceRoot,
			IndexedObjectIntersectionOf conjunction) {
		super(inferenceRoot, conjunction);
	}

	@Override
	public IndexedContextRoot getInferenceRoot() {
		return getConclusionRoot();
	}

	public ComposedSubsumer getFirstPremise(ComposedSubsumer.Factory factory) {
		return factory.getComposedSubsumer(getInferenceRoot(),
				getExpression().getFirstConjunct());
	}

	public ComposedSubsumer getSecondPremise(ComposedSubsumer.Factory factory) {
		return factory.getComposedSubsumer(getInferenceRoot(),
				getExpression().getSecondConjunct());
	}

	@Override
	public String toString() {
		return super.toString() + " (conjunction+)";
	}

	@Override
	public <I, O> O accept(ComposedSubsumerInferenceVisitor<I, O> visitor,
			I input) {
		return visitor.visit(this, input);
	}

}
