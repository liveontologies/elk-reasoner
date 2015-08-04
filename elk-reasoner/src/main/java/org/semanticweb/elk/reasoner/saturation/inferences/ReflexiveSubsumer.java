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
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.inferences.properties.ReflexivePropertyChainImpl;
import org.semanticweb.elk.reasoner.saturation.inferences.visitors.SubsumerInferenceVisitor;

/**
 * Represents an inference of the form A => R some A where R is a reflexive
 * {@link IndexedPropertyChain}.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ReflexiveSubsumer extends
		AbstractComposedSubsumerInference<IndexedObjectSomeValuesFrom>
		implements SubsumerInference<IndexedObjectSomeValuesFrom> {

	/**
	 * @param superClassExpression
	 */
	public ReflexiveSubsumer(IndexedContextRoot inferenceRoot,
			IndexedObjectSomeValuesFrom existential) {
		super(inferenceRoot, existential);
	}

	@Override
	public IndexedContextRoot getInferenceRoot() {
		return getConclusionRoot();
	}

	public IndexedObjectProperty getRelation() {
		return getExpression().getProperty();
	}

	// this is a premise of this inference
	public ReflexivePropertyChainImpl<IndexedObjectProperty> getReflexivityPremise() {
		return new ReflexivePropertyChainImpl<IndexedObjectProperty>(
				getExpression().getProperty());
	}

	@Override
	public <I, O> O accept(SubsumerInferenceVisitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}

}
