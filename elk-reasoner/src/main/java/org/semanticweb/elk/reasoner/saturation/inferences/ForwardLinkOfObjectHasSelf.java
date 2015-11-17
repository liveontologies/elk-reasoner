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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectHasSelf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.classes.AbstractClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionDecomposed;

/**
 * A {@link ForwardLink} obtained from a {@link SubClassInclusionDecomposed}
 * with {@link IndexedObjectHasSelf} super-class. 
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class ForwardLinkOfObjectHasSelf extends AbstractClassConclusion
		implements ForwardLinkInference {

	private final IndexedObjectHasSelf existential_;

	public ForwardLinkOfObjectHasSelf(IndexedContextRoot inferenceRoot,
			IndexedObjectHasSelf subsumer) {
		super(inferenceRoot);
		existential_ = subsumer;
	}

	public IndexedObjectHasSelf getDecomposedExistential() {
		return this.existential_;
	}

	@Override
	public IndexedPropertyChain getForwardChain() {
		return existential_.getProperty();
	}

	@Override
	public IndexedContextRoot getTarget() {
		return getInferenceRoot();
	}

	public SubClassInclusionDecomposed getPremise(SubClassInclusionDecomposed.Factory factory) {
		return factory.getDecomposedSubClassInclusion(getInferenceRoot(), existential_);
	}

	@Override
	public IndexedContextRoot getInferenceRoot() {
		return getConclusionRoot();
	}

	@Override
	public String toString() {
		return super.toString() + " (decomposition)";
	}

	@Override
	public <O> O accept(ClassConclusion.Visitor<O> visitor) {
		return visitor.visit(this);
	}
	
	@Override
	public <O> O accept(ForwardLink.Visitor<O> visitor) {
		return visitor.visit(this);
	}
	
	@Override
	public <I, O> O accept(SaturationInference.Visitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}
	
	@Override
	public <I, O> O accept(ClassInference.Visitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}

	@Override
	public <I, O> O accept(ForwardLinkInference.Visitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}
	
	/**
	 * Visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public static interface Visitor<I, O> {
		
		public O visit(ForwardLinkOfObjectHasSelf inference, I input);
		
	}

}
